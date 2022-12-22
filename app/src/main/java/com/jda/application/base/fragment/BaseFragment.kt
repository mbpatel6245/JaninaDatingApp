package com.jda.application.base.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.jda.application.BuildConfig
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.base.activity.BaseActivity
import com.jda.application.base.presenter.MVPView
import com.jda.application.utils.Constants
import com.jda.application.utils.UserAlertUtility


abstract class BaseFragment : Fragment(), MVPView {
    var mActivity: BaseActivity? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        JDAApplication.mInstance?.setCurrentFragment(this)
        if (context is BaseActivity) {
            this.mActivity = context
        }
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getActivityInstance(): BaseActivity? {
        return this.mActivity
    }

    override fun onError(pErrorMessage: String?, pType: Constants.ErrorType, pErrorCode: Int?) {
        if (JDAApplication.mInstance.getCurrentFragment()?.isDetached == false) {
            when {
                pType == Constants.ErrorType.VALIDATION -> {
                    /*if (getActivityInstance()?.currentFocus != null) {
                    UserAlertUtility.showSnackBar(pErrorMessage, getActivityInstance()?.currentFocus, getActivityInstance())
                } else {*/
                    getActivityInstance()?.showToast(pErrorMessage!!)
                    //}
                }
                pType == Constants.ErrorType.API && pErrorCode == Constants.HTTP_UNAUTHORIZED_ACCESS -> {
                    Log.i(Constants.LOG_TAG, "API ERROR : $pErrorCode $pErrorMessage")
                    UserAlertUtility.showAlertDialog(
                            getString(R.string.error_text),
                            pErrorMessage,
                            mActivity!!,
                            null,
                            null,
                            Constants.HTTP_UNAUTHORIZED_ACCESS
                    )

                }
                pType == Constants.ErrorType.OTHER -> {
                    Log.i(Constants.LOG_TAG, "API ERROR : $pErrorCode $pErrorMessage")
                    UserAlertUtility.showAlertDialog(
                            getString(R.string.error_text),
                            pErrorMessage,
                            getActivityInstance(),
                            null
                    )
                }
                pType == Constants.ErrorType.API -> {
                    Log.i(Constants.LOG_TAG, "API ERROR : $pErrorCode $pErrorMessage")
                    activity?.let {

                    }
                    try {
                        UserAlertUtility.showAlertDialog(
                            getString(R.string.error_text),
                            pErrorMessage,
                            getActivityInstance(),
                            null
                        )
                    } catch (e : Exception){
                        if (BuildConfig.DEBUG){
                            e.printStackTrace()
                        }
                    }

                }
            }
        }
    }

//    override fun showProgress() {
//        UserAlertUtility.showProgressDialog(R.layout.progress_dialog, getActivityInstance(), false)
//    }
//
//    override fun hideProgress() {
//        UserAlertUtility.hideProgressDialog()
//    }

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {

    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {

    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
    }

    override fun onFailure(pErrorMessage: String?, pType: Constants.ErrorType) {
        Log.i(Constants.LOG_TAG, "On Failure : $pErrorMessage")
        onError(pErrorMessage, pType, 0)
    }
}