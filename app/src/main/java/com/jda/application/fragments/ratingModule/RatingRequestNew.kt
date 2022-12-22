package com.jda.application.fragments.ratingModule


import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class RatingRequestNew(
        val message: String?,
        val result: Result?,
        val status: Float?
) : Parcelable {
    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class Result(
            val ratings: Ratings?,
            val reviews: List<Review?>?,
            val pageCount: Float
    ) : Parcelable {
        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class Ratings(
                val behavior: Behavior?,
                val praiseExpression: PraiseExpression?,
                val pics: Pics?,
                val communication: Communication?,
                val goodListener: GoodListener?,
                val overallRating: OverallRating?,
                val punctuality: Punctuality?,
                val responseTime: ResponseTime?
        ) : Parcelable {
            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class Behavior(
                    val count: Float?,
                    val value: Float?
            ) : Parcelable

            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class PraiseExpression(
                    val count: Float?,
                    val value: Float?
            ) : Parcelable

            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class Pics(
                    val count: Float?,
                    val value: Float?
            ) : Parcelable

            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class Communication(
                    val count: Float?,
                    val value: Float?
            ) : Parcelable

            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class GoodListener(
                    val count: Float?,
                    val value: Float?
            ) : Parcelable

            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class OverallRating(
                    val count: Int?,
                    var value: Double = 0.0
            ) : Parcelable

            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class Punctuality(
                    val count: Float?,
                    val value: Float?
            ) : Parcelable

            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class ResponseTime(
                    val count: Float?,
                    val value: Float?
            ) : Parcelable
        }

        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class Review(
                val _id: String?,
                val comment: String?,
                val createdAt: String?,
                val type: Int?,
                val user: User?
        ) : Parcelable {
            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class User(
                    val firstName: String?,
                    val image: String?,
                    val lastName: String?,
                    val rating: Float
            ) : Parcelable
        }
    }
}