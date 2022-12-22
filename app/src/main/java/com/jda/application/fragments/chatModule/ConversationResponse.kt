package com.jda.application.fragments.chatModule

import androidx.annotation.Keep

@Keep
data class ConversationResponse(
        val conversationId: String,
        val messages: List<Message>,
        val msg: String,
        val status: Boolean,
        val statusCode: Int,
        val type: String
) {
    @Keep
    data class Message(
            val AllConversation: List<AllConversation1>,
            val _id: String,
            val clearedAt: String,
            val initiator: String,
            val lastMessage: LastMessage,
            val users: List<User>
    ) {
        @Keep
        data class AllConversation1(
                val __v: Int,
                val _id: String,
                val conversationId: String,
                val createdAt: String,
                val fromId: String,
                val isDeleted: Boolean,
                val unsend: Boolean,
                val message: Message,
                val toId: String,
                var isSelected: Boolean,
                val updatedAt: String
        ) {
            @Keep
            data class Message(
                    val createdAt: String,
                    val text: String,
                    val url: String,
                    val type: Int
            )
        }

        @Keep
        data class LastMessage(
                val createdAt: String,
                val text: String,
                val type: Int
        )

        @Keep
        data class User(
                val _id: String,
                val clearedAt: String,
                val isChatDeleted: Boolean,
                val lastReadAt: String?,
                val userId: String
        )
    }
}