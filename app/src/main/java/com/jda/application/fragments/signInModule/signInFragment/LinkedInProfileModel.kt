package com.jda.application.fragments.signInModule.signInFragment

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LinkedInProfileModel (
    val firstName: StName,
    val lastName: StName,
    val profilePicture: ProfilePicture?=null,
    val id: String
)

@Keep
@Serializable
data class StName (
    val localized: Localized
)
@Keep
@Serializable
data class Localized (
    @SerialName("en_US")
    val enUS: String
)

@Keep
@Serializable
data class ProfilePicture (
    @SerialName("displayImage~")
    val displayImage: DisplayImage
)

@Keep
@Serializable
data class DisplayImage (
    val elements: List<Element>
)

@Keep
@Serializable
data class Element (
    val identifiers: List<Identifier>
)

@Keep
@Serializable
data class Identifier (
    val identifier: String
)