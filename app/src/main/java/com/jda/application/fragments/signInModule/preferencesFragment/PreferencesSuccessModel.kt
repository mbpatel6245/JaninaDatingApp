package com.jda.application.fragments.signInModule.preferencesFragment

import androidx.annotation.Keep

@Keep
data class PreferencesSuccessModel(
        val message: String,
        val result: Result,
        val status: Int
) {
    @Keep
    data class Result(
            val __v: Int,
            val _id: String,
            val belief: List<Int?>?,
            val createdAt: String,
            val ethinicity: Int,
            val gender: Int,
            val isDeleted: Boolean,
            val maxAge: Int,
            val maxHeight: Int,
            val minAge: Int,
            val minHeight: Int,
            val updatedAt: String,
            val userId: String
    )
}