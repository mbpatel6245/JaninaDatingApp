package com.jda.application.fragments.chatModule


import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class MeetRatingRequest(
        var chatId: String? = null,
        var comment: String? = " ",
        var rated: String? = null,

        var communication: Int? = null,
        var punctuality: Int? = null,
        var behavior: Int? = null,
        var pics: Int? = null,
        var praiseExpression: Int? = null
) : Parcelable