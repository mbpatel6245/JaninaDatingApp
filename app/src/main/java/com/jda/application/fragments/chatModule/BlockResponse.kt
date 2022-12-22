package com.jda.application.fragments.chatModule


import android.annotation.SuppressLint
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
@Keep
data class BlockResponse(
    val `data`: Data?,
    val message: String?,
    val status: Int?
) : Parcelable {
    @Parcelize
    @Keep
    data class Data(
        val blocked: String?,
        val blockedBy: String?,
        val unBlocked: String?,
        val unBlockedBy: String?,
    ) : Parcelable
}