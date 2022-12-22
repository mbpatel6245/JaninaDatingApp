package com.jda.application.base.models

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName("msg") val message: String? = null,
    @SerializedName("statusCode") val statusCode: Int? = null,
    @SerializedName("status") val status: Boolean? = null,
    @SerializedName("type") val type: String? = null
)
