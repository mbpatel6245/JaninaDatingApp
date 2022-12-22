package com.jda.application.fragments.matchesModule

import androidx.annotation.Keep
import com.jda.application.utils.Constants

@Keep
data class MatchesListSuccessModel(
        val message: String,
        val result: Result,
        val status: Int
) {
    @Keep
    data class Result(
            val pageCount: Int,
            val users: List<User>
    ) {
        @Keep
        data class User(
                val _id: String,
                val user: UserX,
                var status: Int
        ) {
            @Keep
            data class UserX(
                    val _id: String,
                    val firstName: String,
                    val image: String,
                    val lastName: String
            )
        }
    }
}