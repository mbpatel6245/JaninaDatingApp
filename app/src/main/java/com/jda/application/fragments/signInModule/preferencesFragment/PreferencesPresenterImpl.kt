package com.jda.application.fragments.signInModule.preferencesFragment


import com.jda.application.base.presenter.BasePresenterImp
import com.jda.application.base.presenter.MVPView
import com.jda.application.base.service.URLs
import com.jda.application.fragments.edit_profile.PreferencesRequestModelEdit
import com.jda.application.utils.Constants
import okhttp3.MultipartBody

class PreferencesPresenterImpl(pMVPView: MVPView) : BasePresenterImp(pMVPView),
        PreferencesPresenter {

    override fun apiPreferences(data: PreferencesRequestModel) {
        doRequest(
                URLs.APIs.sSetPreferences,
                data,
                PreferencesSuccessModel1::class.java,
                Constants.RequestType.Post,
                true
        )
    }

    override fun apiPreferencesPut(data: PreferencesRequestModelEdit) {
        doRequest(
                URLs.APIs.sSetPreferences,
                data,
                PreferencesSuccessModel1::class.java,
                Constants.RequestType.Put,
                true
        )
    }

    override fun apiUploadFile(param: MultipartBody.Part) {
        doRequestMultipart(
                URLs.APIs.sUploadFile,
                param,
                ImageUploadSuccessModel::class.java,
                Constants.RequestType.Post,
                true
        )
    }

    override fun apiGalleryFile(param: MultipartBody) {
        doRequestMultipart(
                URLs.APIs.sApiGalleryCall,
                param,
                GallerySuccessModel::class.java,
                Constants.RequestType.Post,
                true
        )
    }

    override fun apiDeleteGallery() {
        doDeleteGalleryRequest( URLs.APIs.sDeleteAllProfilePics, FileDeleteResponse::class.java)
    }

    override fun apiDeleteImage(data: HashMap<String, Any>) {
        doRequest(
                URLs.APIs.sApiGalleryCall,
                data,
                FileDeleteResponse::class.java,
                Constants.RequestType.Delete,
                true
        )
    }
}