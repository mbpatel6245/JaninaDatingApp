package com.jda.application.fragments.homeFragment

import com.jda.application.fragments.subscriptions.SubscriptionRequest

interface HomePresenter {
    fun apiHitGetProfile(param: Map<String, Any>, isPaginationCall: Boolean)
    fun apiVerifySub(param: SubscriptionRequest)
}