package com.jda.application.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.jda.application.acivities.JDAApplication

object FirebaseLogsUtil {

    fun setLogs(pTag: String, pParams: Bundle?) {
        JDAApplication.mInstance.firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.LOGIN, pParams)
    }

}