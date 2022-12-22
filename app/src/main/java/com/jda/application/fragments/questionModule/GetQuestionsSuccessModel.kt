package com.jda.application.fragments.questionModule

import androidx.annotation.Keep

@Keep
data class GetQuestionsSuccessModel(
        val message: String,
        val result: List<Result>,
        val status: Int
)

@Keep
data class Result(
        val _id: String,
        val answers: List<Answer>,
        var nestedQuestions: List<NestedQuestion>? = null,
        val page: Int,
        val question: String,
        val type: Int,
        var choice:String
) {
}

@Keep
data class NestedQuestion(
        val _id: String,
        val choice: String,
        val questions: List<Question>?
)

@Keep
data class Answer(
        var _id: String?=null,
        var value: String?=null,
        var isSelected: Boolean=false
)

@Keep
data class Question(
        val _id: String,
        val answers: List<AnswerX>,
        val question: String,
        val type: Int
)

@Keep
data class AnswerX(
        val _id: String,
        val value: String,
        var isSelected: Boolean=false
)
