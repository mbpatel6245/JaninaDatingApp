package com.jda.application.fragments.chatModule


import androidx.annotation.Keep

@Keep
data class ClearChatSuccessModel(
        val `data`: Data,
        val msg: String,
        val status: Boolean,
        val statusCode: Int,
        val type: String
) {
    @Keep
    data class Data(
            val _id: String,
            val initiator: String,
            val lastMessage: LastMessage,
            val users: List<User>
    ) {
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
                val userId: String
        )
    }
}