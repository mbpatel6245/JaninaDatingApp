package com.jda.application.fragments.signInModule.signInFragment

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LinkedInEmailModel(
    val elements: List<ElementEmail>
)
@Keep
@Serializable
data class ElementEmail(
    @SerialName("handle~")
    val elementHandle: Handle,

    val handle: String
)

@Keep
@Serializable
data class Handle(
    val emailAddress: String
)