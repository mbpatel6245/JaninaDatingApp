package com.jda.application.fragments.signInModule.signInFragment

interface SignInPresenter {
    //    fun apiLogin(data: RegisterModel)
    fun apiSocialLogin(data: SocialLoginModel)
    fun loginValidation(email: String?, password: String?): Boolean
    fun apiFacebookProfilePic(pUserId: String, data: HashMap<String, Any>)
}