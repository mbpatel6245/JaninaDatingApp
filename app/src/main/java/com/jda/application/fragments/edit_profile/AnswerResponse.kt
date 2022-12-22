package com.jda.application.fragments.edit_profile


import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class AnswerResponse(
        val message: String?,
        val result: List<Result?>?,
        val status: Int?
) : Parcelable {
    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class Result(
            val _id: String?,
            val choice: String?,
            val question: String?,
            val text: String?,
            val type: Int?
    ) : Parcelable
}