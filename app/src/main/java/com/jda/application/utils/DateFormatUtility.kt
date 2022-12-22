package com.jda.application.utils

import java.util.*

object DateFormatUtility {
    fun getTimeStampInMilliseconds(): Long {
        val calendar = Calendar.getInstance()
        return calendar.time.time
    }
}