package com.jda.application.fragments.signInModule.signInFragment

import android.util.Patterns
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.base.presenter.BasePresenterImp
import com.jda.application.base.presenter.MVPView
import com.jda.application.base.service.URLs
import com.jda.application.socialLogin.Picture
import com.jda.application.utils.Constants

class SignInPresenterImpl(var pMVPView: MVPView) : BasePresenterImp(pMVPView), SignInPresenter {

//    override fun apiLogin(data: RegisterModel) {
//        doRequest(URLs.APIs.sLoginApi, data, ProfileModel::class.java, Constants.RequestType.Post, true)
//    }

    override fun loginValidation(email: String?, password: String?): Boolean {
        when {
            email.isNullOrBlank() -> pMVPView.onError(JDAApplication.mInstance.getString(R.string.please_enter_email_address), Constants.ErrorType.VALIDATION, Constants.ResponseCode.sSOMETHING_WRONG)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> pMVPView.onError(JDAApplication.mInstance.getString(R.string.please_enter_valid_email_address), Constants.ErrorType.VALIDATION, Constants.ResponseCode.sSOMETHING_WRONG)
            password.isNullOrBlank() -> pMVPView.onError(JDAApplication.mInstance.getString(R.string.please_enter_password), Constants.ErrorType.VALIDATION, Constants.ResponseCode.sSOMETHING_WRONG)
            else -> return true
        }
        return false
    }

    override fun apiFacebookProfilePic(pUserId: String, data: HashMap<String, Any>) {
        doFacebookProfilePicRequest(pUserId + URLs.APIs.sFacebookProfilePic, data, Picture::class.java, Constants.RequestType.Get, true)
    }

    override fun apiSocialLogin(data: SocialLoginModel) {
        doRequest(URLs.APIs.sSocialLoginApi, data, SocialLoginSuccessModel::class.java, Constants.RequestType.Post, true)
    }

}