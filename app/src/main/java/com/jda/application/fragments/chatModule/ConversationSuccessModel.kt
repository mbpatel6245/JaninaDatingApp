package com.jda.application.fragments.chatModule

import androidx.annotation.Keep

@Keep
data class ConversationSuccessModel(
        val message: String,
        val result: Result?,
        val status: Int
) {
    @Keep
    data class Result(
            val blockedByMe:Boolean= false,
            val blockedByOther:Boolean= false,
            val isMeetReviewed:Boolean= false,
            val isChatReviewed:Boolean= false,
            val isChatRatingDone:Boolean= false,
            val messages: List<Message>?,
            var chatId: String?,
            val pageCount: Int
    ) {
        @Keep
        data class Message(
                val _id: String,
                val messages: List<MessageX>
        ) {
            @Keep
            data class MessageX(
                    val _id: String,
                    val chatId: String?,
                    val createdAt: String,
                    var isReaded: Boolean,
                    val reciever: Reciever,
                    val sender: Sender,
                    val text: String,
                    val type: Int
            ) {
                @Keep
                data class Reciever(
                        val _id: String,
                        val firstName: String,
                        val image: String,
                        val lastName: String
                )

                @Keep
                data class Sender(
                        val _id: String,
                        val firstName: String,
                        val image: String,
                        val lastName: String
                )
            }
        }
    }
}