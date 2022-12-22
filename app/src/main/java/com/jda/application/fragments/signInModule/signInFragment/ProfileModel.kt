package com.jda.application.fragments.signInModule.signInFragment

import androidx.annotation.Keep

@Keep
data class ProfileModel(
        val msg: String,
        val status: Boolean,
        val statusCode: Int,
        var token: String,
        val type: String,
        var user: User?

) {
    @Keep
    data class User(
            var _id: String,
            var arePreferencesSet: Boolean,
            var areQuestionsSet: Boolean,
            var areVideosUploaded: Boolean,
            var isVerified: Boolean
    )
}

