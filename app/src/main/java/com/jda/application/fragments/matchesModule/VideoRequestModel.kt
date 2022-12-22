package com.jda.application.fragments.matchesModule

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VideoRequestModel(
		@SerializedName("userId") var userId: String? = null,
		@SerializedName("type") var type: Int? = null,
		@SerializedName("status") var status: Int = 0
)