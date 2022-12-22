package com.jda.application.utils.socket_utils

import android.util.Log
import com.jda.application.acivities.JDAApplication
import com.jda.application.base.service.URLs
import com.jda.application.utils.Constants
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class SocketHelper {

    private var mSocket: Socket? = null
    private var callBack: CallBack? = null
    private var homeEventCallBack: HomeEventCallBack? = null
    private var matchEventCallback: MatchesEventCallBack? = null
    private var mIsConnectedFirstTime: Boolean = true

    fun socketConnect() {
        try {
            mSocket = IO.socket("${if(URLs.Retrofit.sUSE_LIVE_SERVER) URLs.Retrofit.sAPI_LIVE_BASE_URL 
            else URLs.Retrofit.sAPI_LOCAL_BASE_URL }?token=${JDAApplication.mInstance.getProfile()?.result?.authToken}")
        } catch (e: URISyntaxException) {
            Log.e("Socket", "socketConnect:  ${e.printStackTrace()} ")
            e.printStackTrace()
        }
        mSocket!!.connect()
        onListeners()
    }

    private fun onListeners(){
//        mIsConnectedFirstTime = true
        Log.d("Socket", "onListeners: mSocket ${mSocket?.connected()} + mIsConnectedFirstTime $mIsConnectedFirstTime")
        if(mSocket!=null) {
            off()
            mSocket!!.on(Socket.EVENT_CONNECT, connectedListener)
//            mSocket!!.on(Socket.EVENT_RECONNECT, connectedListener)
            mSocket!!.on(Socket.EVENT_DISCONNECT, disconnectedListener)
//            mSocket!!.on(Socket.EVENT_CONNECTING, reconnectingListener)
//            mSocket!!.on(Socket.EVENT_RECONNECTING, reconnectingListener)

//        mSocket!!.on(Constants.Socket_id.arrivalMessage, newMessageArrival)
//        mSocket!!.on(Constants.Socket_id.eventTyping, isMessageTyping)
//        mSocket!!.on(Constants.Socket_id.notTypingEvent, isMessageStoppedTyping)
            mSocket!!.on(Constants.Socket_id.lastReadEvent, isReadCallBack)
//        mSocket!!.on(Constants.Socket_id.unsendMessage, isUnsendCallBack)
            mSocket!!.on(Constants.Socket_id.receiveMessageEvent, newMessageArrival)
            mSocket!!.on(Constants.Socket_id.sendMessageEvent, sendMessageCallback)
            mSocket!!.on(Constants.Socket_id.eventTyping, isMessageTyping)
            mSocket!!.on(Constants.Socket_id.notTypingEvent, isMessageStoppedTyping)
            mSocket!!.on(Constants.Socket_id.connectEvent, connectCallBack)
            mSocket!!.on(Constants.Socket_id.disconnectEvent, disConnectCallBack)
            mSocket!!.on(Constants.Socket_id.likeEvent, likeCallBack)
            mSocket!!.on(Constants.Socket_id.dislikeEvent, disLikeCallBack)
            mSocket!!.on(Constants.Socket_id.undoRequest, undoRequestCallBack)
            mSocket!!.on(Constants.Socket_id.acceptMatch, acceptMatchCallBack)
            mSocket!!.on(Constants.Socket_id.rejectMatch, rejectMatchCallBack)
            mSocket!!.on(Constants.Socket_id.blockUserEvent, blockCallBack)
            mSocket!!.on(Constants.Socket_id.unBlockUserEvent, unBlockCallBack)
            mSocket!!.on(Constants.Socket_id.userRated, userRatedCallBack)
            mSocket!!.on(Constants.Socket_id.deleteChat, userDeletedCallBack)
            mSocket!!.on(Constants.Socket_id.deleteMatchEvent, userUnMatchCallBack)
            mSocket!!.on(Constants.Socket_id.clearChatEvent, userClearChatCallBack)
            Log.d("socket", mSocket?.id() ?: "not connected")
        }
    }

    fun socketStartListener(call: CallBack?) {
        callBack = call
    }

    fun socketStartHomeEventListener(call: HomeEventCallBack) {
        homeEventCallBack = call
    }

    fun matchesEventListener(call: MatchesEventCallBack) {
        matchEventCallback = call
    }

    fun connectEvent() {
        Log.e("socket", "connect event emitted")
        mSocket!!.emit(Constants.Socket_id.connectEvent)
        mIsConnectedFirstTime = false
    }

    fun disconnectEvent(data: JSONObject) {
        mSocket!!.emit(Constants.Socket_id.dislikeEvent, data)
    }

    fun sendMessage(data: JSONObject) {
//        onListeners()
        mSocket!!.emit(Constants.Socket_id.sendMessageEvent, data)
    }

    fun deleteMatch(data: JSONObject) {
//        onListeners()
        mSocket!!.emit(Constants.Socket_id.deleteMatchEvent, data)
    }

    fun typingEvent(event: String, data: JSONObject) {
        mSocket!!.emit(event, data)
    }

    fun clearChat(data: JSONObject) {
        mSocket!!.emit(Constants.Socket_id.clearChatEvent, data)
    }

    fun likeEvent(data: JSONObject) {
        mSocket!!.emit(Constants.Socket_id.likeEvent, data)
    }

    fun dislikeEvent(data: JSONObject) {
        mSocket!!.emit(Constants.Socket_id.dislikeEvent, data)
    }

    fun undoRequestEvent(data: JSONObject) {
        mSocket!!.emit(Constants.Socket_id.undoRequest, data)
    }

    fun acceptMatchEvent(data: JSONObject) {
        mSocket!!.emit(Constants.Socket_id.acceptMatch, data)
    }

    fun rejectMatchEvent(data: JSONObject) {
        mSocket!!.emit(Constants.Socket_id.rejectMatch, data)
    }

    fun deleteUserFromListEvent(data: JSONObject) {
//        onListeners()
        Log.e("Socket", "deleteUserFromListEvent $data")
        mSocket!!.emit(Constants.Socket_id.deleteChat, data)
    }

    fun socketDisconnect() {
//        Log.e("Socket", "Dis-Connected-call")
        mSocket!!.disconnect();
        off()
        mIsConnectedFirstTime=true
    }

    val connectedListener = Emitter.Listener {
        Log.e("Socket", "Connected")
        if (mIsConnectedFirstTime) {
            connectEvent()
        }
        callBack?.socketConnected()
    }

    val disconnectedListener = Emitter.Listener {
        Log.e("Socket", "Dis-Connected")
        callBack?.socketDisconnected()
        socketConnect()
    }

    val reconnectingListener = Emitter.Listener {
        Log.e("Socket", "re-Connecting")
        callBack?.socketReconnecting()
    }

    val newMessageArrival = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "newMessageArrival : "+data)
        callBack?.newMessageComing(data)
    }

    val sendMessageCallback = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "sendMessageCallback : "+data)
        callBack?.sendMessageCallBack(data)
    }

    val isMessageTyping = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "isMessageWritingTyping $data")
        callBack?.isTyping(true, data.getString("chatId"))
    }

    val isMessageStoppedTyping = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "isMessageStoppedTyping $data")
        callBack?.isTyping(false, data.getString("chatId"))
    }

    val isReadCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "isReadCallBack :- $data")
        callBack?.isReadCallBack(data)
    }

    val connectCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "connectData :- $data")
        callBack?.connectCallBack(data)
    }
    val disConnectCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "connectData :- $data")
        callBack?.disConnectCallBack(data)
    }
    val likeCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "connectData :- $data")
        homeEventCallBack?.likeCallBack(data)
    }
    val disLikeCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "connectData :- $data")
        homeEventCallBack?.dislikeCallBack(data)
    }
    val undoRequestCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "connectData :- $data")
        homeEventCallBack?.undoRequest(data)
    }

    val acceptMatchCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "connectData :- $data")
        matchEventCallback?.acceptMatchCallBack(data)
    }

    val rejectMatchCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "connectData :- $data")
        matchEventCallback?.rejectMatchCallBack(data)
    }

    val blockCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "block :- $data")
        callBack?.blockUser(data)
    }

    val unBlockCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "unblock :- $data")
        callBack?.unBlockUser(data)
    }

    val userRatedCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "userRatedCallBack :- $data")
        callBack?.userRated(data)
    }

    val userDeletedCallBack = Emitter.Listener {
        val data = it.get(0) as JSONObject
        Log.e("socket", "userDeletedCallBack :- $data")
//        callBack?.userDeleted(data)
    }

    val userUnMatchCallBack = Emitter.Listener {
        val data = it[0]
        Log.e("socket", "userUnMatchCallBack :- $data")
        matchEventCallback?.unMatchFromMatchesCallBack()
    }

    val userClearChatCallBack = Emitter.Listener {
        val data = it[0] as JSONObject
        Log.e("socket", "userClearChatCallBack :- $data")
        callBack?.userChatCleared(data)
    }

    interface CallBack {
        fun newMessageComing(data: JSONObject)
        fun sendMessageCallBack(data: JSONObject?)
        fun isTyping(isTyping: Boolean, conversationId: String)
        fun socketConnected()
        fun socketDisconnected()
        fun socketReconnecting()
        fun isReadCallBack(data: JSONObject)
        fun connectCallBack(data: JSONObject?)
        fun disConnectCallBack(data: JSONObject)
        fun blockUser(data: JSONObject)
        fun unBlockUser(data: JSONObject)
        fun userRated(data: JSONObject)
        fun userChatCleared(data: JSONObject)
    }

    interface HomeEventCallBack {
        fun likeCallBack(data: JSONObject?)
        fun dislikeCallBack(data: JSONObject?)
        fun undoRequest(data: JSONObject?)
    }

    interface MatchesEventCallBack {
        fun acceptMatchCallBack(data: JSONObject?)
        fun rejectMatchCallBack(data: JSONObject?)
        fun unMatchFromMatchesCallBack()
    }

    private fun off() {
        mSocket!!.off(Socket.EVENT_CONNECT, connectedListener)
//        mSocket!!.off(Socket.EVENT_RECONNECT, connectedListener)
        mSocket!!.off(Socket.EVENT_DISCONNECT, disconnectedListener)
//        mSocket!!.off(Socket.EVENT_CONNECTING, reconnectingListener)
//        mSocket!!.off(Socket.EVENT_RECONNECTING, reconnectingListener)
//        mSocket!!.off(Constants.Socket_id.receiveMessageEvent, newMessageArrival)
//        mSocket!!.off(Constants.Socket_id.eventTyping, isMessageTyping)
//        mSocket!!.off(Constants.Socket_id.notTypingEvent, isMessageStoppedTyping)
        mSocket!!.off(Constants.Socket_id.lastReadEvent, isReadCallBack)
//        mSocket!!.off(Constants.Socket_id.unsendMessage, isUnsendCallBack)
        mSocket!!.off(Constants.Socket_id.connectEvent, connectCallBack)
        mSocket!!.off(Constants.Socket_id.disconnectEvent, disConnectCallBack)
        mSocket!!.off(Constants.Socket_id.receiveMessageEvent, newMessageArrival)
        mSocket!!.off(Constants.Socket_id.sendMessageEvent, sendMessageCallback)
        mSocket!!.off(Constants.Socket_id.eventTyping, isMessageTyping)
        mSocket!!.off(Constants.Socket_id.notTypingEvent, isMessageStoppedTyping)
        mSocket!!.off(Constants.Socket_id.likeEvent, likeCallBack)
        mSocket!!.off(Constants.Socket_id.dislikeEvent, disLikeCallBack)
        mSocket!!.off(Constants.Socket_id.undoRequest, undoRequestCallBack)
        mSocket!!.off(Constants.Socket_id.acceptMatch, acceptMatchCallBack)
        mSocket!!.off(Constants.Socket_id.rejectMatch, rejectMatchCallBack)
        mSocket!!.off(Constants.Socket_id.blockUserEvent, blockCallBack)
        mSocket!!.off(Constants.Socket_id.unBlockUserEvent, unBlockCallBack)
        mSocket!!.off(Constants.Socket_id.userRated, userRatedCallBack)
        mSocket!!.off(Constants.Socket_id.deleteChat, userDeletedCallBack)
        mSocket!!.off(Constants.Socket_id.deleteMatchEvent, userUnMatchCallBack)
        mSocket!!.off(Constants.Socket_id.clearChatEvent, userClearChatCallBack)
    }
}