package com.jda.application.fragments.messagesModule


import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ChatListModel(
        val `data`: List<Data>,
        val msg: String,
        val status: Boolean,
        val statusCode: Int,
        val type: String
) : Parcelable {
    @Keep
    @Parcelize
    data class Data(
            val OtherPersonInformation: OtherPersonInformations,
            val _id: String,
            val conversationId: String,
            var blockedByMe: Boolean,
            var blockedByOther: Boolean,
            val isFavourite: Boolean,
            val lastMessage: LastMessage,
            val showOriginalVideo: Boolean,
            val isCleared: Boolean,
            val unreadMsgCount: Int
    ) : Parcelable {
        @Keep
        @Parcelize
        data class OtherPersonInformations(
                val Name: String,
                val _id: String,
                val isPrivateAccount: Boolean,
                val onlineStatus: OnlineStatus,
                val phone: String,
                val age: Int,
                val video: Video
        ) : Parcelable {
            @Keep
            @Parcelize
            data class OnlineStatus(
                    val isOnline: Boolean,
                    val lastSeen: String
            ) : Parcelable

            @Keep
            @Parcelize
            data class Video(
                    val thumbnail: String
            ) : Parcelable
        }

        @Keep
        @Parcelize
        data class LastMessage(
                var createdAt: String,
                val text: String,
                var type: Int? = null
        ) : Parcelable
    }
}