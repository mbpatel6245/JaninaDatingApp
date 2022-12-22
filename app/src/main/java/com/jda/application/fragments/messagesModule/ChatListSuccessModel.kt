package com.jda.application.fragments.messagesModule

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ChatListSuccessModel(
        val message: String,
        val result: Result,
        val status: Int
) : Parcelable {
    @Keep
    @Parcelize
    data class Result(
            val chats: List<Chat>,
            val pageCount: Int
    ) : Parcelable {
        @Keep
        @Parcelize
        data class Chat(
                val _id: String,
                val lastMessage: LastMessage?,
                var unReadCount: Int,
                val user: User,
                val isUnmatchedByOtherUser: Boolean,
                val isFirst: Boolean,
        ) : Parcelable {
            @Keep
            @Parcelize
            data class LastMessage(
                    val _id: String?,
                    var createdAt: String?,
                    var text: String?,
                    val type: Int?
            ) : Parcelable

            @Keep
            @Parcelize
            data class User(
                    val _id: String,
                    val firstName: String,
                    val image: String,
                    val lastName: String,
                    val gender: Int,
            ) : Parcelable
        }
    }
}