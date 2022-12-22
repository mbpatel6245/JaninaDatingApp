package com.jda.application.base.presenter

import com.jda.application.utils.Constants


interface MVPView {
    fun onError(pErrorMessage: String?, pType: Constants.ErrorType, pErrorCode: Int?)
    fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean)
    fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean)

    //    fun showProgress()
//    fun hideProgress()
    fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?)
    fun onFailure(pErrorMessage: String?, pType: Constants.ErrorType)
}