package com.sirgoingfar.snapnow.camera.ui.fragments

import ai.kudi.agent.picture_picker.views.PicturePickerView
import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.sirgoingfar.snapnow.R
import com.sirgoingfar.snapnow.camera.custom.ApiLevel19AndBelowCameraManager
import com.sirgoingfar.snapnow.camera.custom.ApiLevel21AndAboveCameraManager
import com.sirgoingfar.snapnow.camera.di.DaggerApplicationComponent
import com.sirgoingfar.snapnow.camera.models.PicturePickerOption
import com.sirgoingfar.snapnow.camera.presenters.PicturePickerPresenter
import com.sirgoingfar.snapnow.camera.ui.callbacks.PicturePickerCallback
import com.sirgoingfar.snapnow.camera.utils.FileProcessor
import kotlinx.android.synthetic.main.fragment_picture_picker.*
import javax.inject.Inject

/**
 *
 *
 * Author = {
 *
 * 'name' : 'Olanrewaju E. Akintunde <eaolanrewaju@gmail.com>',
 * 'date' : '27-October-2019'
 *
 * }
 *
 * */
class PicturePickerFragment : MvpFragment<PicturePickerView, PicturePickerPresenter>(),
    PicturePickerView {

    @Inject
    lateinit var screenPresenter: PicturePickerPresenter

    private lateinit var apiLevel21AndAboveCameraManager: ApiLevel21AndAboveCameraManager
    private lateinit var apiLevel19AndBelowCameraManager: ApiLevel19AndBelowCameraManager

    private var selectedImageBitmap: Bitmap? = null
    private var callback: PicturePickerCallback? = null

    private lateinit var option: PicturePickerOption

    override fun createPresenter(): PicturePickerPresenter = screenPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerApplicationComponent.builder()
            .build()
            .inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_picture_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        if (isLollipopAndAbove()) {
            if (isCameraPermissionGiven())
                initCamera2Api()
            else {
                if (isMashmallowAndAbove())
                    requestCameraPermission()
                else initCamera2Api()
            }
        } else {
            initCameraApi()
        }

    }

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION)
    }

    private fun requestExtStoragePermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_READ_EXT_STORAGE_PERMISSION
        )
    }

    private fun isCameraPermissionGiven() =
        arrayOf(Manifest.permission.CAMERA).all {
            ContextCompat.checkSelfPermission(activity!!, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun isStoragePermissionGiven() =
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE).all {
            ContextCompat.checkSelfPermission(activity!!, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun initCameraApi() {
        apiLevel19AndBelowCameraManager = ApiLevel19AndBelowCameraManager(activity!!)
        fl_camera_view.addView(apiLevel19AndBelowCameraManager)
        setupOtherViews()
    }

    private fun initCamera2Api() {
        apiLevel21AndAboveCameraManager = ApiLevel21AndAboveCameraManager(activity!!, camera_view)
        setupOtherViews()
    }

    private fun setupOtherViews() {

        //retrieve the option object
        arguments?.let {
            if (arguments!!.containsKey(KEY_PICKER_OPTION))
                option = arguments!!.getParcelable(KEY_PICKER_OPTION)!!
        }

        //setup views
        option.let {
            if (it.promptText.isNullOrEmpty())
                tv_prompt.visibility = View.GONE
            else {
                tv_prompt.visibility = View.VISIBLE
                tv_prompt.text = it.promptText
                tv_prompt.setTextColor(it.promptTextColor)
            }

            if (it.showCaptureFrame)
                iv_focus_frame.visibility = View.VISIBLE
            else
                iv_focus_frame.visibility = View.GONE

            if (it.enableGallerySelection) {
                fl_gallery.visibility = View.VISIBLE
                fl_gallery.setOnClickListener {
                    chooseFromGallery()
                }
            } else {
                fl_gallery.visibility = View.GONE
            }

            if (it.enableSelfie)
                fl_rotate_camera.visibility = View.VISIBLE
            else
                fl_rotate_camera.visibility = View.GONE

            cl_preview_state.setBackgroundColor(option.backgroundColor)
        }

        //set listener on views
        fl_rotate_camera.setOnClickListener {
            if (isLollipopAndAbove())
                apiLevel21AndAboveCameraManager.switchCamera()
            else
                apiLevel19AndBelowCameraManager.switchCamera()
        }

        fl_capture.setOnClickListener { _ ->
            if (isLollipopAndAbove()) {
                apiLevel21AndAboveCameraManager.takePhoto {
                    onPictureCaptured(it)
                }
            } else {
                apiLevel19AndBelowCameraManager.takePhoto {
                    onPictureCaptured(it)
                }
            }
        }

        fl_cancel.setOnClickListener {
            cancelSelectedPicture()
        }

        //switch view state
        toggleViewState()

        //set flash light ON
        if (isLollipopAndAbove())
            apiLevel21AndAboveCameraManager.setFlash(ApiLevel21AndAboveCameraManager.FLASH.ON)
        else {
            //Todo: Do something
        }

    }

    private fun cancelSelectedPicture() {
        selectedImageBitmap = null
        callback?.onPictureCancelled()
        toggleViewState()

        if (option.enableAutoUpload)
            screenPresenter.stopImageUpload()
    }

    override fun onPause() {
        if (isLollipopAndAbove()) {
            if (::apiLevel21AndAboveCameraManager.isInitialized)
                apiLevel21AndAboveCameraManager.close()
        } else {
            if (::apiLevel19AndBelowCameraManager.isInitialized) {
                apiLevel19AndBelowCameraManager.pauseCamera()
            }
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        if (isLollipopAndAbove()) {
            if (::apiLevel21AndAboveCameraManager.isInitialized)
                apiLevel21AndAboveCameraManager.onResume()
        } else {
            if (::apiLevel19AndBelowCameraManager.isInitialized) {
                apiLevel19AndBelowCameraManager.resumeCamera()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (permissions.isEmpty())
            return

        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (isCameraPermissionGiven()) {
                initCamera2Api()
                return
            } else {
                callback!!.onPermissionDenied(Manifest.permission.CAMERA)
            }
        } else if (requestCode == REQUEST_CODE_READ_EXT_STORAGE_PERMISSION) {
            if (isStoragePermissionGiven()) {
                showGalleryPicturePicker()
                return
            } else {
                callback!!.onPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null || resultCode != RESULT_OK) {
            init()
            return
        }

        if (requestCode == REQUEST_CODE_PICK_FROM_GALLERY) {
            onGalleryPicturePicked(data.data)
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    fun uploadPictureToMediaService() {
        if (selectedImageBitmap == null) {
            callback?.onNoPictureSelected()
            return
        }

        callback?.onUploadStarted()
        toggleCancelBtn()

        screenPresenter.uploadImage(
            FileProcessor.getCompressedBitmapFilePathFrom(
                context!!,
                selectedImageBitmap!!
            )!!
        )
        { isSuccessful, pictureUrl ->
            onPictureUploadResponse(isSuccessful, pictureUrl)
        }
    }

    private fun onPictureUploadResponse(isSuccessful: Boolean, pictureUrl: String?) {

        if (isSuccessful) {
            callback?.onPictureUploadSuccessful(pictureUrl!!, selectedImageBitmap!!)
        } else {
            callback?.onPictureUploadFailure()
        }

        toggleCancelBtn(true)
    }

    private fun onPictureCaptured(bitmap: Bitmap) {
        selectedImageBitmap = bitmap
        callback?.onPictureTaken(selectedImageBitmap != null, bitmap)
        setImageView()

        if (option.enableAutoUpload)
            uploadPictureToMediaService()
    }

    private fun onGalleryPicturePicked(data: Uri?) {
        data?.let {
            val inputStream = activity!!.contentResolver.openInputStream(data)
            onPictureCaptured(BitmapFactory.decodeStream(inputStream)!!)
        }
    }

    fun chooseFromGallery() {

        if (!option.enableGallerySelection) {
            //Gallery selection is not allowed
            toast("Gallery selection not allowed")
            return
        }

        if (isMashmallowAndAbove())
            requestExtStoragePermission()
        else
            showGalleryPicturePicker()
    }

    private fun showGalleryPicturePicker() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(intent, REQUEST_CODE_PICK_FROM_GALLERY)
    }

    private fun setImageView() {
        activity!!.runOnUiThread {
            if (selectedImageBitmap != null) {
                iv_selected_image.setImageBitmap(selectedImageBitmap)
                toggleViewState(ViewState.STATE_IMAGE_PREVIEW)
            }
        }
    }

    private fun toggleCancelBtn(enable: Boolean = false) {
        fl_cancel.isEnabled = enable
    }

    private fun isLollipopAndAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    private fun isMashmallowAndAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    private fun toggleViewState(state: ViewState = ViewState.STATE_CAMERA) {
        activity!!.runOnUiThread {
            if (state == ViewState.STATE_CAMERA) {
                cl_preview_state.visibility = View.GONE
                cl_camera_state.visibility = View.VISIBLE

                setImageView()
            } else if (state == ViewState.STATE_IMAGE_PREVIEW) {
                cl_preview_state.visibility = View.VISIBLE
                cl_camera_state.visibility = View.GONE
            }
        }
    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    enum class ViewState {
        STATE_CAMERA, STATE_IMAGE_PREVIEW
    }

    companion object {

        private const val REQUEST_CODE_CAMERA_PERMISSION = 3
        private const val REQUEST_CODE_READ_EXT_STORAGE_PERMISSION = 4

        private const val REQUEST_CODE_PICK_FROM_GALLERY = 10

        private const val KEY_PICKER_OPTION = "key_picker_option"

        fun newInstance(
            callback: PicturePickerCallback,
            option: PicturePickerOption
        ): PicturePickerFragment {
            val fragment = PicturePickerFragment()
            fragment.callback = callback
            fragment.arguments = Bundle().apply {
                this.putParcelable(KEY_PICKER_OPTION, option)
            }

            return fragment
        }
    }
}