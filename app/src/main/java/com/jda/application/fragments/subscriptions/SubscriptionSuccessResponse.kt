package com.jda.application.fragments.subscriptions


import androidx.annotation.Keep

@Keep
data class SubscriptionSuccessResponse(
        val result: Result,
        val status: Int
) {
    @Keep
    data class Result(
            val expiryDate: String,
            val isActive: Boolean,
            val message: String,
            val productId: String
    )
}