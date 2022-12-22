package com.jda.application.fragments.homeFragment

import androidx.annotation.Keep

@Keep
data class HomeSuccessResponse(
        val message: String,
        val result: Result,
        val status: Int
) {
    @Keep
    data class Result(
            val pageCount: Int,
            val isActive: Boolean,
            val expiryDate: String,
            val productId: String,
            val users: List<User>
    )

    @Keep
    data class User(
            val _id: String,
            val age: Int,
            val firstName: String,
            val image: String,
            val lastName: String,
            val location: String
    )
}