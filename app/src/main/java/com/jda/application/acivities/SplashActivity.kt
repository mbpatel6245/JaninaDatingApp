package com.jda.application.acivities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jda.application.R
import com.jda.application.base.service.URLs
import com.jda.application.utils.Constants
import com.jda.application.utils.SharedPreferencesUtility
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Log.i(Constants.LOG_TAG, "onCreate: In SplashActivity")

        printHashKey(this)
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
//            val profile = JDAApplication.mInstance.getProfile()
//            profile?.result?.arePreferencesSet = true
//            profile?.result?.areQuestionsSet = true
//            JDAApplication.mInstance.setProfile(profile)

            //            // Log.e("token", profile?.token ?: "jkhj")
            if (JDAApplication.mInstance.getProfile() != null && JDAApplication.mInstance.getProfile()!!.result?.arePreferencesSet!!
            ) {
                if (JDAApplication.mInstance.getProfile()!!.result?.areQuestionsSet!!){
                    Log.i(Constants.LOG_TAG, "onCreate: moving to dashboard activity")
                    startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                    finish()
                }else{
                    Log.i(Constants.LOG_TAG, "onCreate: moving to LoginActivity activity")
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
//                Log.i(Constants.LOG_TAG, "onCreate: moving to dashboard activity")
//                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
//                finish()
            } else if(!SharedPreferencesUtility.getUserTermsAccepted(this)) {
                //start web view activity as user not accepted terms and conditions yet
                Log.i(Constants.LOG_TAG, "onCreate: moving to login activity")

                startActivity(Intent(this@SplashActivity, WebViewActivity::class.java).apply {
                    putExtra(Constants.BundleParams.sURl,URLs.APIs.sEnDUserAgrrementUrl)
                    putExtra(Constants.BundleParams.sBeforeLogIn,true)
                })
                finish()
            }
            else{
                Log.i(Constants.LOG_TAG, "onCreate: moving to login activity")
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }, Constants.sSplashTime)

        getDeviceSuperInfo()
    }

    private fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i("HASHKEY", "printHashKey() Hash Key: $hashKey")
                Log.i(Constants.LOG_TAG, "printHashKey: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("HASHKEY", "printHashKey()", e)
            Log.e(Constants.LOG_TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("HASHKEY", "printHashKey()", e)
            Log.e(Constants.LOG_TAG, "printHashKey()", e)
        }
    }

    private fun getDeviceSuperInfo() {
        Log.i(Constants.LOG_TAG, "getDeviceSuperInfo")
        try {
            val deviceInfo = "Debug-infos:\n" +
                    "OS Version : ${System.getProperty("os.version")} ${android.os.Build.VERSION.INCREMENTAL}\n" +
                    "OS API Level: ${Build.VERSION.SDK_INT}\n" +
                    "Device: ${Build.DEVICE}\n " +
                    "Model (and Product): ${Build.MODEL} (${Build.PRODUCT})\n " +
                    "RELEASE: ${Build.VERSION.RELEASE}\n " +
                    "BRAND: ${Build.BRAND}\n " +
                    "DISPLAY: ${Build.DISPLAY}\n " +
                    "UNKNOWN: ${Build.UNKNOWN}\n " +
                    "HARDWARE: ${Build.HARDWARE}\n " +
                    "Build ID: ${Build.ID}\n " +
                    "MANUFACTURER: ${Build.MANUFACTURER}\n " +
                    "USER: ${Build.USER}\n " +
                    "HOST: ${Build.HOST}\n "

            Log.i(Constants.LOG_TAG, deviceInfo)
        } catch (e: java.lang.Exception) {
            Log.e(Constants.LOG_TAG, "Error getting Device INFO")
        }
    } //end getDeviceSuperInfo

}