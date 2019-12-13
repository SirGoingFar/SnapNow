package com.sirgoingfar.snapnow.camera.ui.callbacks

import android.graphics.Bitmap

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
interface PicturePickerCallback {

    fun onPictureTaken(isPictureAvailable: Boolean, bitmap: Bitmap)

    fun onPictureCancelled()

    fun onPermissionDenied(permission: String)

    fun onNoPictureSelected()

    fun onUploadStarted()

    fun onPictureUploadSuccessful(pictureId: String, bitmap: Bitmap)

    fun onPictureUploadFailure()

}