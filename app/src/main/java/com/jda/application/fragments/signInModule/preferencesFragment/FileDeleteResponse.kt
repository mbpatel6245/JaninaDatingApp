package com.jda.application.fragments.signInModule.preferencesFragment


import android.annotation.SuppressLint
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
@Keep
data class FileDeleteResponse(
    val message: String?,
    val result: Result?,
    val status: Int?
) : Parcelable {
    @Parcelize
    @Keep
    class Result(
    ) : Parcelable
}