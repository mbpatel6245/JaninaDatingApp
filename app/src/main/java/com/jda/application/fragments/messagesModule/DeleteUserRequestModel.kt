package com.jda.application.fragments.messagesModule


import androidx.annotation.Keep

@Keep
data class DeleteUserRequestModel(
    val conversationId: String
)