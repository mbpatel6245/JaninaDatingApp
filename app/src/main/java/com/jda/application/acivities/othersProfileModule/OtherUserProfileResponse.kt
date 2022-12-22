package com.jda.application.acivities.othersProfileModule


import android.annotation.SuppressLint
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class OtherUserProfileResponse(
    val message: String?,
    val result: Result,
    val status: Int?
) : Parcelable {
    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class Result(
        val _id: String?,
        val address: String?,
        val age: Int?,
        val answers: List<Answer>?,
        val belief: String?,
        val beliefId: Int?,
        val ethinicity: List<String?>,
        val etinicityId: List<Int?>,
        val firstName: String?,
        val gallery: List<String>,
        val gender: String?,
        val genderId: Int?,
        val height: Int,
        val image: String,
        val lastName: String?,
        val latitude: Double?,
        val location: Location?,
        val longitude: Double?,
        val lookingFor: LookingFor,
        val rating: Double?,
        val relationshipStatus: String?,
        val relationshipStatusId: Int?,
        val reviews: Int?
    ) : Parcelable {
        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class Answer(
            val _id: String?,
            val choice: String?,
            val question: String?,
            val text: String?,
            val type: Int?
        ) : Parcelable

        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class Location(
            val coordinates: List<Double?>?,
            val name: String?,
            val type: String?
        ) : Parcelable

        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class Rating(
            val overallRating: OverallRating?
        ) : Parcelable {
            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class OverallRating(
                val value: Int?
            ) : Parcelable
        }
        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class LookingFor(
            var beliefId: List<Int>,
            val ethinicity: List<String>,
            val ethinicityId: List<Int>,
            val gender: String,
//            val genderId: Int,
            val genderId: List<Int>,
            val maxAge: Int,
            val maxHeight: Int,
            val minAge: Int,
            val minHeight: Int
        ) : Parcelable

    }
}