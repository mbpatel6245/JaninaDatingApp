package com.jda.application.fragments.signInModule.preferencesFragment

import com.jda.application.fragments.edit_profile.PreferencesRequestModelEdit
import okhttp3.MultipartBody

interface PreferencesPresenter {
    fun apiPreferences(data: PreferencesRequestModel)
    fun apiDeleteImage(data: HashMap<String, Any>)
    fun apiPreferencesPut(data: PreferencesRequestModelEdit)
    fun apiUploadFile(param: MultipartBody.Part)
    fun apiGalleryFile(param: MultipartBody)
    fun apiDeleteGallery()
}