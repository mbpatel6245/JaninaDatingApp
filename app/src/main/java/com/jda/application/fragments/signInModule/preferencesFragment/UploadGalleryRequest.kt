package com.jda.application.fragments.signInModule.preferencesFragment

import android.os.Parcelable
import com.jda.application.utils.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UploadGalleryRequest(
        var urlPath: String,
        var isUploaded: Int = Constants.INITAIL,
        var isServerUploaded: Boolean = false
) : Parcelable
