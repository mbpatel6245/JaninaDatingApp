package com.jda.application.fragments.profileModule


import android.annotation.SuppressLint
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class LocationModel(
    val coordinates: List<Double?>?,
    val name: String?,
    val type: String?
) : Parcelable