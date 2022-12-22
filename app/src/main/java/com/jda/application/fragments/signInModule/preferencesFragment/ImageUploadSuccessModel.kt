package com.jda.application.fragments.signInModule.preferencesFragment

import androidx.annotation.Keep

@Keep
data class ImageUploadSuccessModel(
        val message: String,
        val result: Result,
        val status: Int
) {
    @Keep
    data class Result(
            val image: String
    )
}