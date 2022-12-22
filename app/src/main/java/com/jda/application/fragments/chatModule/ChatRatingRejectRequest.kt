package com.jda.application.fragments.chatModule


import android.annotation.SuppressLint
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Keep
data class ChatRatingRejectRequest(
    var chatId: String?=null
)