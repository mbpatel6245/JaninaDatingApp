package com.jda.application.fragments.matchesModule

import androidx.annotation.Keep

@Keep
data class MatchesRequestAcceptedModel(
        val `data`: Data,
        val msg: String,
        val status: Boolean,
        val statusCode: Int,
        val type: String
) {
    @Keep
    data class Data(
            val __v: Int,
            val _id: String,
            val createdAt: String,
            val from: String,
            val status: Int,
            val to: String,
            val type: Int,
            val updatedAt: String
    )
}
