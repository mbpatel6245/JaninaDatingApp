package com.jda.application.socialLogin

import androidx.annotation.Keep

@Keep
data class FacebookRequestModel(
    var profile: Profile?=null
)

@Keep
data class Profile(
    var email: String?=null,
    var firstName: String?=null,
    var lastName: String?=null,
    var picture: Picture?=null,
    var userID: String?=null
)

@Keep
data class Picture(
    var `data`: Data?=null
)

@Keep
data class Data(
    var height: Int?=null,
    var is_silhouette: Boolean,
    var url: String?=null,
    var width: Int?=null
)