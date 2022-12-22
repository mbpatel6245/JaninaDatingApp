package com.jda.application.fragments.signInModule.preferencesFragment


import androidx.annotation.Keep
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PreferencesSuccessModel1(
    val message: String?,
    val result: Result?,
    val status: Int?
) : Parcelable {
    @Keep
    @Parcelize
    data class Result(
        val __v: Int?,
        val _id: String?,
        val belief: List<Int?>?,
        val createdAt: String?,
        val ethinicity: List<Int?>?,
        val gender: List<Int?>?,
        val isDeleted: Boolean?,
        val maxAge: Int?,
        val maxHeight: Int?,
        val minAge: Int?,
        val minHeight: Int?,
        val updatedAt: String?,
        val userId: String?,
        val myGender:Int?
    ) : Parcelable
}