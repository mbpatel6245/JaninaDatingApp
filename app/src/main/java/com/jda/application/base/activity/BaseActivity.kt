package com.jda.application.base.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.jda.application.BuildConfig
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.base.presenter.MVPView
import com.jda.application.utils.Constants
import com.jda.application.utils.OnItemClickListener
import com.jda.application.utils.PermissionsUtility
import com.jda.application.utils.UserAlertUtility
import java.util.*
import kotlin.collections.ArrayList


abstract class BaseActivity : AppCompatActivity(), MVPView, OnItemClickListener {

    var videoThumnailServerURL: String? = null
    var mBlurVideoUrl: String? = null
    var mOriginalVideoUrl: String? = null

    //    private var mReachability: Reachability? = null
    private var dialog: Dialog? = null
    private var dialogReward: Dialog? = null
    private var imm: InputMethodManager? = null
    var token: String? = null
    private var count = 0
    private var mTextClicked = -1

    //    private var mDialogBinding: DialogDesignMenuBinding? = null
    val android_id: String
        @SuppressLint("HardwareIds")
        get() = Secure.getString(getContentResolver(), Secure.ANDROID_ID)


    abstract fun onPermissionGranted(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBannerAd(resources.getString(R.string.banner_app_id))
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        JDAApplication.mInstance?.setCurrentActivity(this)
        getFirebaseToken()
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    fun doRunTimPermission(permission: ArrayList<String>, requestCode: Int) {
        PermissionsUtility.requestPermissions(this, permission, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onPermissionGranted(requestCode, permissions, grantResults)
    }

    override fun onError(pErrorMessage: String?, pType: Constants.ErrorType, pErrorCode: Int?) {
        when (pType) {
            Constants.ErrorType.VALIDATION -> {
                if (currentFocus != null) {
                    UserAlertUtility.showSnackBar(pErrorMessage, currentFocus, this)
                } else {
                    showToast(pErrorMessage!!)
                }
            }
            Constants.ErrorType.API, Constants.ErrorType.OTHER -> {
                UserAlertUtility.showAlertDialog(
                    getString(R.string.error_text),
                    pErrorMessage,
                    this,
                    null
                )
                Log.i(Constants.LOG_TAG, "Api Error or Other Error :$pErrorMessage")

            }
        }
    }
/*
    override fun showProgress() {
        UserAlertUtility.showProgressDialog(R.layout.progress_dialog, this, false)
    }

    override fun hideProgress() {
        UserAlertUtility.hideProgressDialog()
    }*/

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {

    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {

    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
    }

    override fun onFailure(pErrorMessage: String?, pType: Constants.ErrorType) {
        onError(pErrorMessage, pType, 0)
    }

    fun showToast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    override fun onItemClick(item: View) {
    }

    fun closeKeyBoard(): Boolean {
        return imm!!.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                if (token == null) {
                    if (count < 5) {
                        getFirebaseToken()
                        count++
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    }
                }
                return@OnCompleteListener
            } else {
                // Get new FCM registration token
                token = task.result
                JDAApplication.mInstance.saveDeviceToken(token)
                Log.i("Device Token : ", task.result!!)
            }
        })
    }

    fun loadBannerAd(ad_view: AdView) {
        if (Constants.sIsSubscribed) {
            ad_view.visibility = View.GONE
        } else {
            ad_view.visibility = View.VISIBLE
//            initializeBannerAd(resources.getString(R.string.banner_app_id))
            val adRequest = AdRequest.Builder()
                .build()
            ad_view.loadAd(adRequest)
        }
    }

    private fun initializeBannerAd(appUnitId: String) {
        MobileAds.initialize(this)
    }
}