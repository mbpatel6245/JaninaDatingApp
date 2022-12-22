package com.jda.application.fragments.questionModule

interface QuestionPresenter {
    fun apiGetQuestionList(param: Map<String, Any>, isPaginationCall: Boolean)
    fun apiSaveAnswer(data:SaveAnswersRequestModel)
    fun apiGetAnswer()
}