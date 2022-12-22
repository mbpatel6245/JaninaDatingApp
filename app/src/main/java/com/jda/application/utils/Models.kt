/**
 * @author Joyce Hong
 * @email soja0524@gmail.com
 * @created 2019-09-03
 * @desc
 */

package com.jda.application.utils

import androidx.annotation.Keep

@Keep
data class Message (val userName : String, val messageContent : String, val roomName: String,var viewType : Int)
@Keep
data class initialData (val userName : String, val roomName : String)
@Keep
data class SendMessage(val userName : String, val messageContent: String, val roomName: String)