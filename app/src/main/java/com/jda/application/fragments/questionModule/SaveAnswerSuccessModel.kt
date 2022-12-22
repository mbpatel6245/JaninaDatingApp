package com.jda.application.fragments.questionModule

import androidx.annotation.Keep

@Keep
data class SaveAnswerSuccessModel(
        val message: String,
        val result: Result,
        val status: Int
) {
    @Keep
    class Result(
    )
}