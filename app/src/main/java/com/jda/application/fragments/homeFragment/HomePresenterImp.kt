package com.jda.application.fragments.homeFragment

import com.jda.application.base.presenter.BasePresenterImp
import com.jda.application.base.presenter.MVPView
import com.jda.application.base.service.URLs
import com.jda.application.fragments.subscriptions.SubscriptionRequest
import com.jda.application.fragments.subscriptions.SubscriptionSuccessResponse
import com.jda.application.utils.Constants

class HomePresenterImp(mvpView: MVPView) : BasePresenterImp(mvpView), HomePresenter {
    override fun apiHitGetProfile(param: Map<String, Any>, isPaginationCall: Boolean) {
        doRequest(URLs.APIs.sApiHome, param, HomeSuccessResponse::class.java, Constants.RequestType.Get, false, isPaginationCall)
    }

    override fun apiVerifySub(param: SubscriptionRequest) {
        doRequest(URLs.APIs.sSendSubscriptionApiRequest, param, SubscriptionSuccessResponse::class.java, Constants.RequestType.Post, true)
    }
}