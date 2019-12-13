package com.sirgoingfar.snapnow.camera.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View

/**
 *
 *
 * Author = {
 *
 * 'name' : 'Olanrewaju E. Akintunde <eaolanrewaju@gmail.com>',
 * 'date' : '27-October-2019',
 * 'reference' : 'Mobin <GitHub>'
 *
 * }
 *
 * */
class ApiLevel19AndBelowCameraManager(context: Context) : SurfaceView(context),
    SurfaceHolder.Callback {

    private var onPictureListener: (Bitmap) -> Unit = {}

    private val pictureCallback = Camera.PictureCallback { data, _ ->
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        onPictureListener(bitmap)
        refreshCamera()
    }

    private var camera: Camera? = null
    private var cameraFront = false
    private var previewSize: Camera.Size? = null

    init {
        camera = Camera.open()
        camera!!.setDisplayOrientation(90)


        holder.addCallback(this)
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }


    private fun findFrontFacingCamera(): Int {

        var cameraId = -1
        // Search for the front facing camera
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i
                cameraFront = true
                break
            }
        }
        return cameraId

    }


    private fun refreshCamera() {
        if (camera == null || holder.surface == null) {
            return
        }

        // stop preview before making changes
        try {
            camera!!.stopPreview()
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        //setCamera(camera)
        try {
            val params = camera!!.parameters
            params.setPreviewSize(previewSize!!.width, previewSize!!.height)
            camera!!.parameters = params
            camera!!.setPreviewDisplay(holder)
            camera!!.startPreview()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private fun findBackFacingCamera(): Int {
        var cameraId = -1
        //Search for the back facing camera
        //get the number of cameras
        val numberOfCameras = Camera.getNumberOfCameras()
        //for every camera check
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i
                cameraFront = false
                break

            }

        }
        return cameraId
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        refreshCamera()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        releaseCamera()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (camera == null)
            return

        camera!!.setPreviewDisplay(holder)
        camera!!.startPreview()
    }

    fun switchCamera() {
        val cameras = Camera.getNumberOfCameras()
        if (cameras > 1) {
            releaseCamera()
            chooseCamera()
        }

    }

    fun takePhoto(listener: (Bitmap) -> Unit) {
        onPictureListener = listener
        camera!!.takePicture(null, null, pictureCallback)
    }

    private fun chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            val cameraId = findBackFacingCamera()
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                camera = Camera.open(cameraId)
                camera!!.setDisplayOrientation(90)
                refreshCamera()
            }
        } else {
            val cameraId = findFrontFacingCamera()
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview
                camera = Camera.open(cameraId)
                camera!!.setDisplayOrientation(90)
                refreshCamera()
            }
        }
    }

    fun pauseCamera() {
        releaseCamera()
    }

    fun resumeCamera() {
        if (camera == null) {
            camera = Camera.open()
            camera!!.setDisplayOrientation(90)
            refreshCamera()
        }
    }

    private fun releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera?.stopPreview()
            camera?.setPreviewCallback(null)
            camera?.release()
            camera = null
        }
    }

    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = h.toDouble() / w

        if (sizes == null) return null

        var optimalSize: Camera.Size? = null
        var minDiff = java.lang.Double.MAX_VALUE

        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - h).toDouble()
            }
        }

        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - h).toDouble()
                }
            }
        }
        return optimalSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = View.resolveSize(suggestedMinimumWidth, heightMeasureSpec)
        setMeasuredDimension(width, height)
        val supportedPreviewSizes = camera!!.parameters.supportedPreviewSizes
        if (supportedPreviewSizes != null)
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height)

    }
}