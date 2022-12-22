package com.jda.application.fragments.questionModule

import androidx.annotation.Keep

@Keep
data class SaveAnswersRequestModel(
        var page: Int,
        val answers: List<Answer>,
) {
    @Keep
    data class Answer(
            var choice: String? = null,
            var questionId: String,
            var text: String?,
            var type: Int
    )
}
