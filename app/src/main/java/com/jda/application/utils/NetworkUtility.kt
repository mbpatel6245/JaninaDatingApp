package com.jda.application.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.utils.UserAlertUtility.Companion.showToast

class NetworkUtility {
    companion object {

        fun isConnected(pShowToast: Boolean = true): Boolean {
            var result = false
            val connectivityManager = JDAApplication.mInstance?.getCurrentActivity()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return result
                val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return result
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> {
                        if (pShowToast) {
                            showToastMsg()
                        }
                        false
                    }
                }
            } else {
                result = try {
                    if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected) {
                        true
                    } else {
                        if (pShowToast) {
                            showToastMsg()
                        }
                        false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            return result
        }

        private fun showToastMsg() {
            JDAApplication.mInstance?.showToast(JDAApplication.mInstance?.getString(R.string.check_internet_connection))
        }
    }
}