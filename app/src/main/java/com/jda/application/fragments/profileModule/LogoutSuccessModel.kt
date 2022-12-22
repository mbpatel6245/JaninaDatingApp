package com.jda.application.fragments.profileModule

import androidx.annotation.Keep

@Keep
data class LogoutSuccessModel(
        val message: String,
        val result: Result,
        val status: Int
) {
    @Keep
    class Result(
    )
}