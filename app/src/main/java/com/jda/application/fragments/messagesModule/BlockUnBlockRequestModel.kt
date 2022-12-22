package com.jda.application.fragments.messagesModule

import androidx.annotation.Keep

@Keep
data class BlockUnBlockRequestModel(
        var status: Boolean,
        var userId: String
)