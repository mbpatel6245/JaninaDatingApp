package com.jda.application.utils.notificaton_service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.jda.application.acivities.DashboardActivity
import com.jda.application.acivities.JDAApplication
import com.jda.application.fragments.chatModule.ConversationSuccessModel
import com.jda.application.utils.CommonUtility
import com.jda.application.utils.Constants

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("FirebaseToken", token)
        JDAApplication.mInstance.saveDeviceToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("notification", message.data.toString())
        sendNotification(applicationContext, message)
    }

    private fun sendNotification(context: Context, notificationMessage: RemoteMessage) {
        showMessageNotification(context, notificationMessage)
    }

    private fun showMessageNotification(context: Context, notificationMessage: RemoteMessage) {
        var notifyManager: NotificationManager? = null
        var NOTIFY_ID = 1002
        val name = "SomeApp"
        val id = "kotlin_app"
        val description = "kotlin_app_first_channel"
        val pendingIntent: PendingIntent

        if (notifyManager == null) {
            notifyManager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel = notifyManager.getNotificationChannel(id)
            if (mChannel == null) {
                mChannel = NotificationChannel(id, name, importance)
                mChannel.description = description
                mChannel.enableVibration(true)
                mChannel.lightColor = Color.GREEN
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                notifyManager.createNotificationChannel(mChannel)
            }
        }

        var data: ConversationSuccessModel.Result.Message.MessageX? = null

        try {
            data = Gson().fromJson(
                    notificationMessage.data["data"], // get data from payload
                    ConversationSuccessModel.Result.Message.MessageX::class.java
            )
        } catch (e: Exception) {
            Log.d("ExceptionValue", "sendNotification: ${e.message}")
        }

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, id)
        val intent = Intent(context, DashboardActivity::class.java)
        Log.d("BroadcastReceiver", "sendNotification: ${data?.chatId ?: 0}")
        intent.putExtra(Constants.Notification.sNewChatId, data?.chatId)
        intent.putExtra(Constants.Notification.type, 3)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        pendingIntent = PendingIntent.getActivity(context, 10002, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (data != null) {
            val name = (CommonUtility.capitaliseOnlyFirstLetter(data.sender.firstName) + " " +
                    CommonUtility.capitaliseOnlyFirstLetter(data.sender.lastName))
            val userID = data.chatId
            builder.setContentTitle(name)  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(data.text)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setGroup(userID)
                    .setContentIntent(pendingIntent)

            if (JDAApplication.mInstance.getUserID() != userID) {
                val notification = builder.build()
                notifyManager.notify(data.chatId, NOTIFY_ID, notification)
//                if (Constants.sIsSubscribed)
                    sendBroadcastToChatScreen(notificationMessage.data["data"])
            }
        }
    }

    private fun sendBroadcastToChatScreen(data: String?) {
        // send broadcast
        val intent = Intent(Constants.BroadCastCodes.BROADCAST_CHAT)
        intent.putExtra(Constants.BundleParams.DATA, data)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        Log.i("Notification : ", "send Broadcast called , Data : $data")
    }

}