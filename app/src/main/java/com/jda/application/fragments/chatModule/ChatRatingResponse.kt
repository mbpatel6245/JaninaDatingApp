package com.jda.application.fragments.chatModule


import android.annotation.SuppressLint
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class ChatRatingResponse(
    val message: String,
    val result: Result,
    val status: Int
) : Parcelable {
    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    class Result(
    ) : Parcelable
}