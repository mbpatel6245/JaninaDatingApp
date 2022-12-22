package com.jda.application.acivities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.jda.application.R
import com.jda.application.utils.Constants
import com.jda.application.utils.SharedPreferencesUtility
import com.jda.application.utils.UserAlertUtility
import kotlinx.android.synthetic.main.activity_web_view.*


class WebViewActivity : AppCompatActivity() {

    var mIsBeforeLogin = false
    var mUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        UserAlertUtility.showProgressDialog(R.layout.progress_dialog, this, false)
        getDataFromIntent()
        backBt.setOnClickListener {
            onBackPressed()
        }
        setListener()
    }

    fun setListener() {
        terms_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                accept_button.isEnabled = true
                accept_button.alpha = 1.0f
            } else {
                accept_button.isEnabled = false
                accept_button.alpha = 0.5f
            }

        }
        accept_button.setOnClickListener {
            SharedPreferencesUtility.setUserTermsAccepted(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    fun getDataFromIntent() {
        mIsBeforeLogin = intent.getBooleanExtra(Constants.BundleParams.sBeforeLogIn, false)
        bottom_accept_button_view.visibility = if (mIsBeforeLogin) View.VISIBLE else View.GONE
        if (intent.hasExtra(Constants.BundleParams.sURl)) {
            mUrl = intent.getStringExtra(Constants.BundleParams.sURl).toString()
            setUrlToWebView()
        } else {
            finishActivity()
        }
    }

    fun setUrlToWebView() {
        web_View.clearCache(true)
        web_View.clearHistory()
        web_View.loadUrl(mUrl)
        web_View.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                UserAlertUtility.hideProgressDialog()
                // do your stuff here
            }
        })
//
    }

    fun finishActivity() {
        if (mIsBeforeLogin) {
            finishAffinity()
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        finishActivity()
    }

}