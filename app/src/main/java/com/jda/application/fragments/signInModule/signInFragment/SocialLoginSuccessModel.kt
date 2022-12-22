package com.jda.application.fragments.signInModule.signInFragment

import androidx.annotation.Keep

@Keep
data class SocialLoginSuccessModel(
        var message: String,
        var result: Result?,
        var status: Int
)

@Keep
data class Result(
        val _id: String,
        val authToken: String,
        val countryCode: String,
        val email: String,
        val firstName: String?,
        val image: String,
        val lastName: String?,
        val phone: String,
        val status: Int,
        var arePreferencesSet: Boolean = false,
        var areQuestionsSet: Boolean = false,
        var isActive: Boolean = false,
        var productId: String?,
        var expiryDate: String?,
        var myGender: Int?
)