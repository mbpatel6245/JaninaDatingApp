package com.jda.application.fragments.chatModule


import android.annotation.SuppressLint
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Keep
data class ChatRatingRequest(
    var chatId: String?=null,
    var comment: String?=" ",
    var goodListener: Int?=null,
    var behavior: Int?=null,
    var rated: String?=null,
    var responseTime: Int?=null
)