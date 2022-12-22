package com.jda.application.fragments.messagesModule

import androidx.annotation.Keep

@Keep
data class ReportSuccessModel(
        val msg: String,
        val status: Boolean,
        val statusCode: Int,
        val type: String
)