package com.jda.application.fragments.messagesModule

import androidx.annotation.Keep

@Keep
data class BlockUnBlockSuccessModel(
        val `data`: Data,
        val msg: String,
        val status: Boolean,
        val statusCode: Int,
        val type: String
) {
    @Keep
    data class Data(
            val __v: Int,
            val _id: String,
            val blockedBy: String,
            val createdAt: String,
            val updatedAt: String,
            val userId: String,
            val n: Int,
            val ok: Int,
            val deletedCount: Int
    )
}