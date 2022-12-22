package com.jda.application.fragments.signInModule.signInFragment

import androidx.annotation.Keep

@Keep
data class SocialLoginModel(
        val deviceId: String,
        val deviceToken: String,
        var email: String? = null,
        val firstName: String?,
        var lastName: String? = "",
        val image: String?=null,
        val loginType: Int,
        var socialId: String?,
        var phone: String? = null,
        var countryCode: String? = null,
        var deviceType: Int = 0  // 0 for android, 1 for Ios and 2 for web
)