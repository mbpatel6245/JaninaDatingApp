package com.jda.application.fragments.signInModule.preferencesFragment


import android.annotation.SuppressLint
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class GallerySuccessModel(
    val message: String?,
    val result: Result?,
    val status: Int?
) : Parcelable {
    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class Result(
        val images: List<String>
    ) : Parcelable
}