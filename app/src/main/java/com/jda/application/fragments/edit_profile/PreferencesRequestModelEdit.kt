package com.jda.application.fragments.edit_profile

import androidx.annotation.Keep

@Keep
data class PreferencesRequestModelEdit(
    var address: String?=null,
    var age: Int?=null,
    var belief: Int?=null,
    var beliefChoice: Array<Int>?=null,
    var ethinicity: Array<Int>?=null,
    var ethinicityChoice: Array<Int>?=null,
    var firstName: String?=null,
    var gender: Int?=null,
//    var genderChoice: Int?=null,
    var genderChoice: Array<Int>?=null,
    var height: Int?=null,
    var image: String?=null,
    var lastName: String?=null,
    var latitude: Double?=null,
    var longitude: Double?=null,
    var maxAge: Int?=null,
    var maxHeight: Int?=null,
    var minAge: Int?=null,
    var minHeight: Int?=null,
    var relationshipStatus: Int?=null
)