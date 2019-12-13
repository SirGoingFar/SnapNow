package com.sirgoingfar.snapnow.camera.models

import android.graphics.Color
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PicturePickerOption(
    var promptText: String? = null,
    var promptTextColor: Int = Color.parseColor("#FFFFFF"),
    var backgroundColor: Int = Color.parseColor("#223856"),
    var showCaptureFrame: Boolean = false,
    var enableAutoUpload: Boolean = false,
    var enableSelfie: Boolean = true,
    var enableGallerySelection: Boolean = true
) : Parcelable