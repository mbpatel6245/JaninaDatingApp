package com.jda.application.fragments.subscriptions


import androidx.annotation.Keep

@Keep
data class SubscriptionRequest(
        val productId: String?,
        val receipt: String?,
        val platform: Int?
)