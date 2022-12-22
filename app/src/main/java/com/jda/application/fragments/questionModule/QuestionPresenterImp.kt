package com.jda.application.fragments.questionModule

import com.jda.application.base.presenter.BasePresenterImp
import com.jda.application.base.presenter.MVPView
import com.jda.application.base.service.URLs
import com.jda.application.fragments.edit_profile.AnswerResponse
import com.jda.application.utils.Constants


class QuestionPresenterImp(mvpView: MVPView) : BasePresenterImp(mvpView), QuestionPresenter {
    override fun apiGetQuestionList(param: Map<String, Any>, isPaginationCall: Boolean) {
        doRequest(URLs.APIs.sGetQuestionList, param, GetQuestionsSuccessModel::class.java, Constants.RequestType.Get, true,isPaginationCall)
    }

    override fun apiSaveAnswer(data:SaveAnswersRequestModel) {
        doRequest(URLs.APIs.sSaveAnswers, data, SaveAnswerSuccessModel::class.java, Constants.RequestType.Post, true)
    }

    override fun apiGetAnswer() {
        doRequest(URLs.APIs.sGetAnswerList, Any(), AnswerResponse::class.java, Constants.RequestType.Get, true)
    }
}