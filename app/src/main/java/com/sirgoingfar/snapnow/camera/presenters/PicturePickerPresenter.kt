package com.sirgoingfar.snapnow.camera.presenters

import ai.kudi.agent.picture_picker.views.PicturePickerView
import android.annotation.SuppressLint
import android.os.Handler
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import javax.inject.Inject

class PicturePickerPresenter @Inject constructor() : MvpBasePresenter<PicturePickerView>() {

    @SuppressLint("CheckResult")
    fun uploadImage(
        currentPhotoPath: String,
        callback: (isSuccessful: Boolean, pictureUrl: String?) -> Unit
    ) {
        //Todo: Do the following:

        //Step 1: Upload the image to the server (Simulated with a delayed task in this use case)
        //Step 2: Invoke the callback() depending on the outcome of the picture upload
        //E.g. callback(isSuccessful, picture_url_or_id)

        Handler().postDelayed({
            callback(true, "https://blob.example.com/image?upload_id=1234567890")
        }, 10000)


    }

    fun stopImageUpload() {
        //Todo: Stop the upload based on the mechanism used for the upload
    }

}