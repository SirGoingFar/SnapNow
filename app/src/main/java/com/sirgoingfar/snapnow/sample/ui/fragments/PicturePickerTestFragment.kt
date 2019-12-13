package com.sirgoingfar.snapnow.sample.ui.fragments

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sirgoingfar.snapnow.R
import com.sirgoingfar.snapnow.camera.models.PicturePickerOption
import com.sirgoingfar.snapnow.camera.ui.callbacks.PicturePickerCallback
import com.sirgoingfar.snapnow.camera.ui.fragments.PicturePickerFragment
import kotlinx.android.synthetic.main.fragment_test.*

/**
 *
 *
 * Author = {
 *
 * 'name' : 'Olanrewaju E. Akintunde <eaolanrewaju@gmail.com>',
 * 'date' : '27-October-2019',
 *
 * }
 *
 * */
class PicturePickerTestFragment : Fragment(), PicturePickerCallback {

    private var picturePicker: PicturePickerFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val option = PicturePickerOption(
            "Show a prompt text here",
            showCaptureFrame = true
        )

        picturePicker = PicturePickerFragment.newInstance(this, option)

        childFragmentManager.beginTransaction()
            .replace(R.id.container, picturePicker!!)
            .commit()

        btn_continue.setOnClickListener {
            uploadImage()
        }

        tv_pick_from_gallery.setOnClickListener {
            picturePicker!!.chooseFromGallery()
        }

    }

    private fun uploadImage() {
        picturePicker!!.uploadPictureToMediaService()
    }

    //Camera Action Callback Functions
    override fun onPictureTaken(isPictureAvailable: Boolean, bitmap: Bitmap) {
        toast("Picture taken")
    }

    override fun onPictureCancelled() {
        toast("Picture cancelled")
    }

    override fun onNoPictureSelected() {
        toast("Pick or snap a picture first")
    }

    override fun onPermissionDenied(permission: String) {
        toast("Permission denied: $permission")
        if (permission == Manifest.permission.CAMERA) {
            //Todo: Do something ---> Camera Permission denied
        } else if (permission == Manifest.permission.READ_EXTERNAL_STORAGE) {
            //Todo: Do something ---> Read external storage Permission denied
        }
    }

    override fun onUploadStarted() {
        toast("Picture upload started")
        toggleActionBtn()
    }

    override fun onPictureUploadSuccessful(pictureId: String, bitmap: Bitmap) {
        toast("Picture upload successful:\n\n $pictureId")
        toggleActionBtn(true)
    }

    override fun onPictureUploadFailure() {
        toast("Picture upload failed")
        toggleActionBtn(true)
    }

    private fun toast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    private fun toggleActionBtn(enable: Boolean = false) {

        if (enable) {
            pb_loader.visibility = View.GONE
        } else {
            pb_loader.visibility = View.VISIBLE
        }

        btn_continue.isEnabled = enable
        tv_pick_from_gallery.isEnabled = enable
    }

    companion object {
        fun newInstance() = PicturePickerTestFragment()
    }
}
