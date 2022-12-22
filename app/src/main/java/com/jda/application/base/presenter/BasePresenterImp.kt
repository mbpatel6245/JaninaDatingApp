package com.jda.application.base.presenter

import android.os.Bundle
import android.util.Log
import androidx.annotation.StringRes
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.base.models.ErrorResponse
import com.jda.application.base.network.API
import com.jda.application.base.service.BaseService
import com.jda.application.utils.Constants
import com.jda.application.utils.FirebaseLogsUtil
import com.jda.application.utils.NetworkUtility
import com.jda.application.utils.UserAlertUtility.Companion.showToast
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

open class BasePresenterImp(private val pMVPView: MVPView) {
    var api: API? = null
    var facebookProfilePicApi: API? = null

    init {
        api = BaseService.getClient().create(API::class.java)
        facebookProfilePicApi = BaseService.getClientForFacebookProfilePic().create(API::class.java)
    }

    protected fun doRequest(pUrl: String, pRequestParams: Any, tClass: Class<*>, requestType: Constants.RequestType, showProgress: Boolean = true, pIsPaginatedCall: Boolean = false) {
        try {
            if (NetworkUtility.isConnected()) {
                if (showProgress) {
                    pMVPView.showProgress(tClass, pIsPaginatedCall)
                }
                val requestParams = if (pRequestParams is Map<*, *>) {
                    pRequestParams
                } else {
                    HashMap<String, Any>()
                }
                if (JDAApplication.mInstance?.getRequestObject() != null) {
                    JDAApplication.mInstance?.getRequestObject()?.cancel()
                }
                val request: Call<Any?>? = when (requestType) {
                    Constants.RequestType.Post -> api!!.request(pUrl, pRequestParams)
                    Constants.RequestType.Put -> api!!.putRequest(pUrl, pRequestParams)
                    Constants.RequestType.Get -> {
                        api!!.getRequest(pUrl, requestParams as Map<String, Any>)
                    }
                    Constants.RequestType.Delete -> api!!.deleteFile(pUrl, requestParams as Map<String, Any>)
                    else -> return
                }
                JDAApplication.mInstance?.setRequestObject(request)
                makeRequest(request, tClass, pIsPaginatedCall)
            } else {
                JDAApplication.mInstance?.showToast(JDAApplication.mInstance?.getString(R.string.check_internet_connection))
                Log.i(Constants.LOG_TAG, "Network Error :" + JDAApplication.mInstance?.getString(R.string.check_internet_connection))

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    protected fun doRequestMultipart(pUrl: String, pRequestParams: MultipartBody.Part, tClass: Class<*>, requestType: Constants.RequestType, showProgress: Boolean = true, pIsPaginatedCall: Boolean = false) {
        try {
            if (NetworkUtility.isConnected()) {
                if (showProgress) {
                    pMVPView.showProgress(tClass, pIsPaginatedCall)
                }
                if (JDAApplication.mInstance?.getRequestObject() != null) {
                    JDAApplication.mInstance?.getRequestObject()?.cancel()
                }
                val request: Call<Any?>? = when (requestType) {
                    Constants.RequestType.Post -> api!!.updateFile(pUrl, pRequestParams)
                    else -> return
                }
                JDAApplication.mInstance?.setRequestObject(request)
                makeRequest(request, tClass, pIsPaginatedCall)
            } else {
                JDAApplication.mInstance?.showToast(JDAApplication.mInstance?.getString(R.string.check_internet_connection))
                Log.i(Constants.LOG_TAG, "Network Error :" + JDAApplication.mInstance?.getString(R.string.check_internet_connection))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    protected fun doRequestMultipart(pUrl: String, pRequestParams: MultipartBody, tClass: Class<*>, requestType: Constants.RequestType, showProgress: Boolean = true, pIsPaginatedCall: Boolean = false) {
        try {
            if (NetworkUtility.isConnected()) {
                if (showProgress) {
                    pMVPView.showProgress(tClass, pIsPaginatedCall)
                }
                if (JDAApplication.mInstance?.getRequestObject() != null) {
                    JDAApplication.mInstance?.getRequestObject()?.cancel()
                }
                val request: Call<Any?>? = when (requestType) {
                    Constants.RequestType.Post -> api!!.updateFile(pUrl, pRequestParams)
                    else -> return
                }
                JDAApplication.mInstance?.setRequestObject(request)
                makeRequest(request, tClass, pIsPaginatedCall)
            } else {
                JDAApplication.mInstance?.showToast(JDAApplication.mInstance?.getString(R.string.check_internet_connection))
                Log.i(Constants.LOG_TAG, "Network Error :" + JDAApplication.mInstance?.getString(R.string.check_internet_connection))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    fun doFacebookProfilePicRequest(
            pUrl: String,
            pRequestParams: Any,
            tClass: Class<*>,
            requestType: Constants.RequestType,
            showProgress: Boolean = true,
            pIsPaginatedCall: Boolean = false
    ) {
        try {
            if (NetworkUtility.isConnected()) {
                if (showProgress) {
                    pMVPView.showProgress(tClass, pIsPaginatedCall)
                }
                if (JDAApplication.mInstance?.getRequestObject() != null) {
                    JDAApplication.mInstance?.getRequestObject()?.cancel()
                }
                val requestParams = if (pRequestParams is Map<*, *>) {
                    pRequestParams
                } else {
                    HashMap<String, Any>()
                }
                val request: Call<Any?>? = when (requestType) {
                    Constants.RequestType.Get -> {
                        facebookProfilePicApi!!.getRequest(pUrl, requestParams as Map<String, Any>)
                    }
                    else -> return
                }
                JDAApplication.mInstance?.setRequestObject(request)
                makeRequest(request, tClass, false)
            } else {
                JDAApplication.mInstance?.showToast(JDAApplication.mInstance?.getString(R.string.check_internet_connection))
                Log.i(Constants.LOG_TAG, "Network Error :" + JDAApplication.mInstance?.getString(R.string.check_internet_connection))

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    protected fun doDeleteGalleryRequest(pUrl: String, tClass: Class<*>) {
        try {
            if (NetworkUtility.isConnected()) {
                val request: Call<Any?>? = api!!.deleteGallery(pUrl)
                makeRequest(request, tClass, false)
            } else {
                JDAApplication.mInstance?.showToast(JDAApplication.mInstance?.getString(R.string.check_internet_connection))
                Log.i(Constants.LOG_TAG, "Network Error :" + JDAApplication.mInstance?.getString(R.string.check_internet_connection))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun makeRequest(request: Call<Any?>?, tClass: Class<*>, pIsPaginatedCall: Boolean) {
        request!!.enqueue(object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                if (response.body() != null) {
                    pMVPView.hideProgress(tClass, true, pIsPaginatedCall)
                    Log.e("response", response.body().toString())
                    Log.i(Constants.LOG_TAG, "response:" + response.body().toString())
                    val params = Bundle().apply {
                        putString("response", response.body().toString())
                    }
                    FirebaseLogsUtil.setLogs(Constants.LOG_TAG, params)
                    if (response.body() is ArrayList<*>) {
                        val jsonString = Gson().toJson(response.body())
                        val jsonObject = JsonObject()
                        jsonObject.add(Constants.ResponseCode.sDATA, Gson().fromJson(jsonString, JsonArray::class.java))
                        pMVPView.onSuccess(Gson().fromJson(jsonObject, tClass), pIsPaginatedCall)
                    } else if (response.body() is LinkedTreeMap<*, *>) {
                        try {
                            pMVPView.onSuccess(Gson().fromJson(response.body().toString(), tClass), pIsPaginatedCall)
                        } catch (e: Exception) {
                            try {
                                pMVPView.onSuccess(Gson().fromJson(Gson().toJson(response.body()), tClass), pIsPaginatedCall)
                            } catch (e1: Exception) {
                                if (e1 is HttpException) {
                                    pMVPView.onError(e1.message, Constants.ErrorType.API, e1.code())
                                } else {
                                    pMVPView.onError(e1.message, Constants.ErrorType.API, Constants.ResponseCode.sSOMETHING_WRONG)
                                }
                            }
                        }
                    }
                } else {
                    pMVPView.hideProgress(tClass, false, pIsPaginatedCall)
                    var isCatchException = true
                    if (response.errorBody() != null) {
                        try {
                            val data = Gson().fromJson(response.errorBody()!!.string(), ErrorResponse::class.java)
                            data?.let {
                                Log.i(Constants.LOG_TAG, "response_error :" + it.statusCode + it.message)
                                val params = Bundle().apply {
                                    putString("ErrorMessage", it.message)
                                    putString("ErrorStatusCode", it.statusCode.toString())
                                }
                                FirebaseLogsUtil.setLogs(Constants.LOG_TAG, params)
                            }
                            data.message?.let {
                                Log.e("response_error", it)
                            }

                            if (data != null && data.message?.isNotEmpty()!!) {
                                pMVPView.onError(data.message, Constants.ErrorType.API, response.code())
                                isCatchException = false
                            }
                        } catch (e: Exception) {
                        }
                    }
                    if (isCatchException) {
                        val message: String = when (response.code()) {
                            Constants.ResponseCode.sINTERNAL_SERVER_ERROR -> JDAApplication.mInstance?.getString(R.string.internal_server_error)!!
                            Constants.ResponseCode.sUNAUTHORIZED_ACCESS -> {
                                JDAApplication.mInstance?.getString(R.string.unAuthorized_access)!!
                            }
                            else -> response.message()
                        }
                        if (response.code() != Constants.ResponseCode.sUNAUTHORIZED_ACCESS) {
                            pMVPView.onFailure(message, Constants.ErrorType.OTHER)
                        } else {
                            pMVPView.onError(message, Constants.ErrorType.API, response.code())
                        }
                    }
                }


            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {
                pMVPView.hideProgress(tClass, false, pIsPaginatedCall)
                if (!call.isCanceled) {
                    if (t is HttpException) {
                        pMVPView.onError(t.message, Constants.ErrorType.API, t.code())
                    } else {
                        pMVPView.onError(t.message, Constants.ErrorType.API, Constants.ResponseCode.sSOMETHING_WRONG)
                    }
                }
            }
        })
    }

    fun setValidationMessage(@StringRes stringID: Int) {
        pMVPView.onError(JDAApplication.mInstance.getString(stringID), Constants.ErrorType.VALIDATION, Constants.ResponseCode.sSOMETHING_WRONG)
    }
}