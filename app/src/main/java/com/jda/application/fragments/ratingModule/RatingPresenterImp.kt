package com.jda.application.fragments.ratingModule

import com.jda.application.base.presenter.BasePresenterImp
import com.jda.application.base.presenter.MVPView
import com.jda.application.base.service.URLs
import com.jda.application.utils.Constants

class RatingPresenterImp(mvpView: MVPView) : RatingPresenter, BasePresenterImp(mvpView) {
    override fun apiHitRating(param: HashMap<String, Any>) {
        doRequest(URLs.APIs.sRatingGet, param, RatingRequestNew::class.java, Constants.RequestType.Get, true)
    }


}