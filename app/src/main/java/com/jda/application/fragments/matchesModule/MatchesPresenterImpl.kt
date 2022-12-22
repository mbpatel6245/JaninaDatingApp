package com.jda.application.fragments.matchesModule

import com.jda.application.base.presenter.BasePresenterImp
import com.jda.application.base.presenter.MVPView
import com.jda.application.base.service.URLs
import com.jda.application.utils.Constants

class MatchesPresenterImpl(mvpView: MVPView) : BasePresenterImp(mvpView), MatchesPresenter {

    override fun apiGetMatchesRequestList(param: Map<String, Any>, isPaginationCall: Boolean) {
        doRequest(URLs.APIs.sApiMatchList, param, MatchesListSuccessModel::class.java, Constants.RequestType.Get, true, isPaginationCall)
    }
}