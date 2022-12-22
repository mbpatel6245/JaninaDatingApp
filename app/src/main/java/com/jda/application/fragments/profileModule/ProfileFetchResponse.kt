package com.jda.application.fragments.profileModule


import android.annotation.SuppressLint
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class ProfileFetchResponse(
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
            val age: Int,
            val answers: List<Answer>,
            val belief: String,
            val beliefId: String,
            val ethinicity: List<String>,
            val etinicityId: List<Int>,
            val firstName: String,
            val gallery: List<String>,
            val gender: String?,
            val genderId: String,
            val height: Int,
            val image: String,
            val lastName: String,
            val latitude: Double,
            val location: Location,
            val longitude: Double,
            val lookingFor: LookingFor,
            val relationshipStatus: String,
            var relationshipStatusId: String
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