package com.jda.application.fragments.profileModule

import com.jda.application.base.presenter.BasePresenterImp
import com.jda.application.base.presenter.MVPView
import com.jda.application.base.service.URLs
import com.jda.application.utils.Constants

class MyProfilePresenterImp(mvpView: MVPView) : BasePresenterImp(mvpView), MyProfilePresenter {

    override fun apiHitGetProfile() {
        doRequest(URLs.APIs.sApiGetUserProfile, Any(), ProfileFetchResponse::class.java, Constants.RequestType.Get, true)
    }

    override fun apiLogout() {
        doRequest(URLs.APIs.sApiLogout, Any(), LogoutSuccessModel::class.java, Constants.RequestType.Get, true)
    }

}