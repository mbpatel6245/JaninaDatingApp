package com.jda.application.fragments.subscriptions

import com.jda.application.base.presenter.BasePresenterImp
import com.jda.application.base.presenter.MVPView
import com.jda.application.base.service.URLs
import com.jda.application.utils.Constants

class SubscriptionPresenterImp(pMVPView: MVPView) : BasePresenterImp(pMVPView),
        SubscriptionPresenter {
    override fun apiSendSub(param: SubscriptionRequest) {
        doRequest(URLs.APIs.sSendSubscriptionApiRequest, param, SubscriptionSuccessResponse::class.java, Constants.RequestType.Post, true)
    }
}