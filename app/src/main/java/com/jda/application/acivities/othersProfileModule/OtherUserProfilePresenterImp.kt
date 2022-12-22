package com.jda.application.acivities.othersProfileModule

import com.jda.application.base.presenter.BasePresenterImp
import com.jda.application.base.presenter.MVPView
import com.jda.application.base.service.URLs
import com.jda.application.utils.Constants


class OtherUserProfilePresenterImp(mvpView: MVPView) : BasePresenterImp(mvpView), OtherUserProfilePresenter {
    override fun apiHitGetProfile(userId: HashMap<String, Any>) {
        doRequest(URLs.APIs.sApiGetUserProfile, userId, OtherUserProfileResponse::class.java, Constants.RequestType.Get, true)
    }
}