package com.jda.application.fragments.subscriptions

import android.os.Parcelable
import androidx.annotation.Keep
import com.android.billingclient.api.SkuDetails
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Keep
@Parcelize
data class PlansModel(
        var productId: String? = null,
        var name: String? = null,
        var description: String? = null,
        var duration: String? = null,
        var price: String? = null,
        var skuDetails: @RawValue SkuDetails? = null
) : Parcelable
