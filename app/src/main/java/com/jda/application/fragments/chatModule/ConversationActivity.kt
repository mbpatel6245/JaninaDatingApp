package com.jda.application.fragments.chatModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.acivities.othersProfileModule.OtherProfileActivity
import com.jda.application.adapter.ConversationAdapterNew
import com.jda.application.base.activity.BaseActivity
import com.jda.application.databinding.DialogAfterMeetDesignBinding
import com.jda.application.databinding.DialogMeetDesignBinding
import com.jda.application.databinding.WriteReviewDesignBinding
import com.jda.application.fragments.messagesModule.ChatListSuccessModel
import com.jda.application.fragments.profileModule.ProfileFetchResponse
import com.jda.application.utils.CommonUtility
import com.jda.application.utils.CommonUtility.capitaliseOnlyFirstLetter
import com.jda.application.utils.Constants
import com.jda.application.utils.MyEditText
import com.jda.application.utils.UserAlertUtility
import com.jda.application.utils.socket_utils.SocketHelper
import kotlinx.android.synthetic.main.fragment_conversation.*
import org.json.JSONObject
import java.io.File

class ConversationActivity : BaseActivity(), View.OnClickListener, SocketHelper.CallBack,
        ConversationAdapterNew.CallBack {

    private var adapter: ConversationAdapterNew? = null
    private var data: ChatListSuccessModel.Result.Chat? = null
    private var dataProfile: ProfileFetchResponse? = null
    private var receiverID: String? = null
    private var mLastMessageId: String? = null
    private var presenter: ConversationPresenter? = null
    private var mMessageModel: ConversationSuccessModel.Result.Message.MessageX? = null
    private var arrayList: MutableList<ConversationSuccessModel.Result.Message.MessageX> =
            ArrayList()
    private var selectedMessageArrayList =
            ArrayList<ConversationResponse.Message.AllConversation1>()
    private var unsendNotPossibleArrayList =
            ArrayList<ConversationResponse.Message.AllConversation1>()
    private var mPopupWindow: PopupWindow? = null
    private var mBlockUnBlockTv: TextView? = null
    private var mClearChatTv: TextView? = null
    private var mAddToFavourite: Int = -1
    private var mIsFavourite: Boolean = false
    private var mChatId: String? = null
    private var mOtherUserId: String? = null
    private var mUserName: String? = null

    private var mIsBlockedByMe: Boolean = false

    private var mIsBlockedByOther: Boolean = false
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var pageCount = 1
    private var isSingleHit = false
    private var page = Constants.PAGE
    private var limit = Constants.LIMIT
    private var mDialog: AlertDialog? = null
    private var isChatRatingDone = false

    override fun onPermissionGranted(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_conversation)
        loadBannerAd(ad_view)
        initialise()
        getIntentData()
//        hitChatApiFirstTime()
        adapter = ConversationAdapterNew(this, arrayList)
        chatRV.adapter = adapter
    }

    private fun initialise() {
        presenter = ConversationPresenterImp(this)
        chatRV.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        initListeners()
        setPopUpWindow()
        textWatcher()
    }

    private fun initListeners() {
        backIV.setOnClickListener(this)
        moreIV.setOnClickListener(this)
        profileImageCL.setOnClickListener(this)
        sendIV.setOnClickListener(this)
        dateReviewTv.setOnClickListener(this)
    }

    private fun getIntentData() {
        data = intent?.extras?.getParcelable(Constants.BundleParams.sProfileData)
        if (data != null) {
            data.let {
                CommonUtility.setGlideImage(profile_image.context, it?.user?.image, profile_image)
                mUserName =
                        (capitaliseOnlyFirstLetter(it?.user?.firstName) + " " + capitaliseOnlyFirstLetter(
                                it?.user?.lastName
                        ))
                otherUserNameTV.text = mUserName
                mChatId = it?._id
                receiverID = it?.user?._id
            }
        }
//        intent?.extras?.getString(Constants.BundleParams.sOtherUserId).let {
//           mOtherUserId = it
//        }

        if (intent?.extras?.containsKey(Constants.BundleParams.sUserImage) == true) {
            intent?.extras?.getString(Constants.BundleParams.sUserImage).let {
                CommonUtility.setGlideImage(profile_image.context, it, profile_image)
            }
        }

        if (intent?.extras?.containsKey(Constants.BundleParams.sUserName) == true) {
            intent?.extras?.getString(Constants.BundleParams.sUserName).let {
                otherUserNameTV.text = it
                mUserName = it
            }
        }

        if (intent?.extras?.containsKey(Constants.BundleParams.sUserId) == true) {
            intent?.extras?.getString(Constants.BundleParams.sUserId).let {
                mOtherUserId = it
                receiverID = it
            }
        }

        if (mUserName != null) {
            dateReviewTv.text =
                    "If you and $mUserName have gone on a date, click here to rate your date."
        }

        JDAApplication.mInstance.setUserID(data?._id)
    }

    private fun hitChatApiFirstTime() {
        if (arrayList.isEmpty()) {
            if (!isSingleHit) {
                isSingleHit = true
                page = Constants.PAGE
                limit = Constants.LIMIT
                arrayList.clear()
                val param = HashMap<String, Any>()
                param[Constants.HashMapParamKeys.sLIMIT] = limit
                param[Constants.HashMapParamKeys.sPAGE] = pageCount
                if (mChatId != null) {
                    param[Constants.HashMapParamKeys.sCHAT_ID] = mChatId!!
                } else if (mOtherUserId != null) {
                    param[Constants.HashMapParamKeys.sOTHER_USER_ID] = mOtherUserId!!
                }
                presenter!!.apiGetChat(param, false)
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.profileImageCL -> {
                val intent = Intent(this, OtherProfileActivity::class.java)
                intent.putExtra(Constants.BundleParams.sUserId, receiverID)
                intent.putExtra(Constants.BundleParams.sIsMatch, true)
                startActivity(intent)
            }
            R.id.backIV -> {
                onBackPressed()
            }

            R.id.sendIV -> {
                if (enterMessageET.text.toString().trim().isEmpty()) {
                    showToast(getString(R.string.please_enter_message))
                } else
                    attemptSend(enterMessageET.text.toString().trim(), Constants.Socket_id.TEXT_ID)
            }
            R.id.moreIV -> {
                mPopupWindow?.showAsDropDown(p0, -300, -40, Gravity.NO_GRAVITY)
            }
            R.id.dateReviewTv -> {
                openDialogMeet()
            }
        }
    }

    private fun attemptSend(message: String, type: Int) {
        receiverID?.let {
            var size = 0
            arrayList?.let {
                size = it.size
            }
            val myGender = JDAApplication.mInstance.getProfile()?.result?.myGender
            if (data?.isFirst == true && myGender != null && myGender != 2 && data?.user?.gender == 2 && size == 0) {
                UserAlertUtility.openCustomDialog(
                        this, getString(R.string.chat),
                        getString(R.string.women_initiate), getString(R.string.ok), "",
                        object : UserAlertUtility.CustomDialogClickListener {
                            override fun onYesClick() {

                            }

                            override fun onNoClick() {
                            }
                        }, false)
                return
            }

            Log.e("Socket", "sendMessage : $mChatId")
            val param = JSONObject()
            param.put(Constants.HashMapParamKeys.sCHAT_ID, mChatId!!)
            param.put(Constants.HashMapParamKeys.sUSER_ID, receiverID!!)
            param.put(Constants.HashMapParamKeys.sTYPE, type)
            param.put(Constants.HashMapParamKeys.sTEXT, message)
            JDAApplication.mInstance.socketHelperObject!!.sendMessage(param)
//            lastReadEvent(Constants.Socket_id.lastReadEvent)
            enterMessageET.setText("")
            recreate()
        }
    }

    private fun typeEvent(event: String, isTyping: Boolean) {
        receiverID?.let {
            val param = JSONObject()
            param.put("chatId", mChatId!!)
            param.put("userId", receiverID!!)
            //  param.put("typing", isTyping)
            Log.e("typeEvent", param.toString())
            JDAApplication.mInstance.socketHelperObject?.typingEvent(event, param)
        }
    }

    private fun lastReadEvent(messageID: String) {
        receiverID?.let {
            val param = JSONObject()
            param.put("chatId", mChatId!!)
            param.put("messageId", messageID)
            Log.e("lastReadEvent", param.toString())
            JDAApplication.mInstance.socketHelperObject!!.typingEvent(
                    Constants.Socket_id.lastReadEvent,
                    param
            )
        }
    }

    override fun isReadCallBack(data: JSONObject) {
        Log.e("jsonObject123", data.toString())
        runOnUiThread {
            data?.let {
                if (data.has("data")) {
                    val chatID = data.getJSONObject("data").getString("chatId")
                    if (mChatId == chatID) {
                        for (i in 0 until arrayList.size) {
                            arrayList[i].isReaded = true
                        }
                        adapter?.notifyDataSetChanged()
                    }
                } else {
                    val localChat = data.getString("chatId")
                    if (mChatId == localChat) {
                        for (i in 0 until arrayList.size) {
                            arrayList[i].isReaded = true
                        }
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun connectCallBack(data: JSONObject?) {

    }

    override fun disConnectCallBack(data: JSONObject) {
    }

    override fun newMessageComing(data: JSONObject) {
        Log.e("SocketComing", "sendMessage : $data")

        runOnUiThread {

            val data11: JSONObject? = data?.getJSONObject("data")
            if (mChatId == data11?.getString("chatId")) {
                data11?.let {
                    // val listType = object : TypeToken<ConversationSuccessModel.Result.Message.MessageX>() {}.getType()
                    val data1 = Gson().fromJson(
                            it.toString(),
                            ConversationSuccessModel.Result.Message.MessageX::class.java
                    )
                    lastReadEvent(data1._id)
                    arrayList.add(0, data1)
                    Log.e(
                            "SocketComing",
                            JDAApplication.mInstance.getProfile()?.result?._id.toString() + " " + data1.reciever._id
                    )
                    setClearChatVisibilty()
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun sendMessageCallBack(dataMessage: JSONObject?) {
        runOnUiThread {
            val data11: JSONObject? = dataMessage?.getJSONObject("data")
            data11?.let {
                // val listType = object : TypeToken<ConversationSuccessModel.Result.Message.MessageX>() {}.getType()
                val data1 = Gson().fromJson(
                        it.toString(),
                        ConversationSuccessModel.Result.Message.MessageX::class.java
                )
                val unMatched: Boolean = data11.get("isUnMatched") as Boolean
                if (unMatched) {
//                    Toast.makeText(this, data.get("message").toString(),Toast.LENGTH_SHORT).show()
                    CommonUtility.closeKeyBoard(this)

                    val name = if (data != null) {
                        "${capitaliseOnlyFirstLetter(data?.user?.firstName)} ${
                            capitaliseOnlyFirstLetter(
                                    data?.user?.lastName
                            )
                        }"
                    } else {
                        mUserName
                    }
//                    UserAlertUtility.showAlertDialog(
//                            getString(R.string.error_text),
//                            "$name unmatched you.\n" +
//                                    "You can't send messages in this chat now.",
//                            this,
//                            null
//                    )

                    UserAlertUtility.openCustomDialog(
                            this, getString(R.string.error_text),
                            "$name  unmatched you.\n" +
                                    "You can't send messages in this chat now.",
                            getString(R.string.ok), null,
                            object : UserAlertUtility.CustomDialogClickListener {
                                override fun onYesClick() {

                                }

                                override fun onNoClick() {

                                }

                            }, false)
                } else if (data1.chatId == mChatId) {
                    arrayList.add(0, data1)
                    adapter?.notifyDataSetChanged()
                }

                setClearChatVisibilty()
            }
        }
    }

    override fun socketConnected() {
        runOnUiThread {
            socketStatusTV.visibility = View.GONE
            socketStatusTV.text = getString(R.string.connected)
            socketStatusTV.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGrey))
            isConnectedSocket()
        }
    }

    private fun isConnectedSocket() {
        if (handler == null) {
            handler = Handler()
            runnable = Runnable {
                socketStatusTV.visibility = View.GONE
                handler!!.removeCallbacks(runnable!!)
                handler = null
            }
            handler?.postAtTime(runnable!!, 2000)
        } else {
            handler!!.removeCallbacks(runnable!!)
            handler = null
            isConnectedSocket()
        }
    }

    override fun socketDisconnected() {
        runOnUiThread {
            socketStatusTV.visibility = View.GONE
            socketStatusTV.text = getString(R.string.disconnected)
            socketStatusTV.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed))
        }
    }

    override fun socketReconnecting() {
        runOnUiThread {
            socketStatusTV.visibility = View.GONE
            socketStatusTV.text = getString(R.string.reconnected)
            socketStatusTV.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGrey))
        }
    }

    override fun isTyping(isType: Boolean, conversationId: String) {
        Log.e("isTyping", "yes : $isType mChatID: $mChatId conversationId $conversationId")
        runOnUiThread {
            if (mChatId == conversationId) {
                if (isType) {
                    if (typingIndicatorIV.visibility != View.VISIBLE)
                        typingIndicatorIV.visibility = View.VISIBLE
                } else {
                    if (typingIndicatorIV.visibility != View.GONE)
                        typingIndicatorIV.visibility = View.GONE
                }
            }
        }
    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        when (pResponse) {
            is ChatRatingRejectResponse -> {
                chatDialog?.dismiss()
//                dateReviewTv.visibility= View.GONE
                Log.d("onSuccess", "ChatRatingRejectResponse: Not Now Pressed")
            }
            is MeetRatingResponse -> {
                meetDialog?.dismiss()
                dateReviewTv.visibility = View.GONE
                showToast(getString(R.string.review_saved_successfully))
            }
            is ChatRatingResponse -> {
                chatDialog?.dismiss()
                isChatRatingDone = true
                dateReviewTv.visibility = View.VISIBLE
                showToast(getString(R.string.review_saved_successfully))
            }
            is ConversationSuccessModel -> {

                mChatId = pResponse.result?.chatId
                mIsBlockedByMe = pResponse.result?.blockedByMe ?: false
                mIsBlockedByOther = pResponse.result?.blockedByOther ?: false
                markBlockUnBlock()

                if (pResponse.result?.isChatReviewed == true) {
                    dateReviewTv.visibility = View.GONE
                    openDialog()
                }

                isChatRatingDone = pResponse.result?.isChatRatingDone ?: false

//                else

                if (pResponse.result?.isMeetReviewed == true) {
//                    if(pResponse.result.isChatReviewed) {
//                        dateReviewTv.visibility= View.GONE
//                        mIsShowChatRate=true
//                        openDialog()
//                    }else{
                    dateReviewTv.visibility = View.VISIBLE
//                    }
                }

                when {
                    !pIsPaginatedCall!! && pResponse.result?.messages?.isNotEmpty() ?: false -> {
                        arrayList.clear()
                        pageCount++
                        page = pResponse.result?.pageCount ?: 0
                        isSingleHit = pageCount > page
                        limit = Constants.LIMIT
                        pResponse.result?.messages?.reversed()?.forEach {
                            arrayList.addAll(it.messages)
                        }
                        adapter?.notifyDataSetChanged()
                    }
                    pResponse.result?.messages?.isNotEmpty() ?: false && pIsPaginatedCall -> {
                        pageCount++
                        page = pResponse.result?.pageCount ?: 0
                        isSingleHit = pageCount > page
                        limit = Constants.LIMIT
                        pResponse.result?.messages?.reversed()?.forEach {
                            arrayList.addAll(it.messages)
                        }
                        adapter?.notifyDataSetChanged()
                    }
                }

                setClearChatVisibilty()
            }
        }
    }

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {
        when (tClass) {
            ChatRatingRejectResponse::class.java, ChatRatingResponse::class.java, MeetRatingResponse::class.java -> {
                UserAlertUtility.showProgressDialog(R.layout.progress_dialog, this, false)
            }
        }
    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {
        when (tClass) {
            ChatRatingRejectResponse::class.java, ChatRatingResponse::class.java, MeetRatingResponse::class.java -> {
                UserAlertUtility.hideProgressDialog()
            }
        }
    }

    private fun setClearChatVisibilty() {
        if (mClearChatTv != null)
            mClearChatTv?.alpha = if (arrayList.isEmpty()) 0.5f else 1f
    }

    override fun onResume() {
        super.onResume()
        arrayList.clear()
        pageCount = 1
        hitChatApiFirstTime()
        JDAApplication.mInstance.socketHelperObject!!.socketStartListener(this)
        Log.e("Socket", "Connected-call")
        JDAApplication.mInstance.socketHelperObject?.socketConnect()
        JDAApplication.mInstance.setUserID(data?._id)
    }

    override fun longPressed(pos: Int, isFirstTime: Boolean) {
    }

    override fun apiHitForPagination() {
        if (!isSingleHit) {
            isSingleHit = true
            val param = HashMap<String, Any>()
            param[Constants.HashMapParamKeys.sLIMIT] = limit
            param[Constants.HashMapParamKeys.sPAGE] = pageCount
            param[Constants.HashMapParamKeys.sCHAT_ID] = mChatId!!
            presenter?.apiGetChat(param, true)
        }
    }

    override fun setChatRating(pView: TextView) {
        pView.text = "Click here to rate your chat with $mUserName"
        pView.visibility = View.VISIBLE
        pView.setOnClickListener {
            if (!isChatRatingDone) {
                openDialog()
            } else {
//                UserAlertUtility.showAlertDialog(
//                        getString(R.string.alert),
//                        "You have already rated your chat with $mUserName",
//                        this,
//                        null
//                )


                UserAlertUtility.openCustomDialog(
                        this, getString(R.string.alert),
                        "You have already rated your chat with $mUserName",
                        getString(R.string.ok), null,
                        object : UserAlertUtility.CustomDialogClickListener {
                            override fun onYesClick() {

                            }

                            override fun onNoClick() {

                            }

                        }, false)

            }
        }
    }

    private fun markBlockUnBlock() {
        when {
            mIsBlockedByMe -> {
                mBlockUnBlockTv?.text = getString(R.string.un_block)
                blockedMessageTV?.visibility = View.VISIBLE
                bottomCL?.visibility = View.INVISIBLE
            }
            mIsBlockedByOther -> {
                blockedMessageTV?.visibility = View.VISIBLE
                blockedMessageTV?.text = getString(R.string.blocked_by_other_msg)
                bottomCL?.visibility = View.INVISIBLE
            }
            else -> {
                mBlockUnBlockTv?.text = getString(R.string.block)
                blockedMessageTV?.visibility = View.GONE
                bottomCL?.visibility = View.VISIBLE
            }
        }
    }

    private fun textWatcher() {
        enterMessageET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (enterMessageET.text.toString().trim().isNotEmpty()) {
                    if (enterMessageET.text.toString().trim().length == 1)
                        typeEvent(Constants.Socket_id.eventTyping, true)
                } else {
                    typeEvent(Constants.Socket_id.notTypingEvent, false)
                }
            }
        })

        enterMessageET.setKeyBoardInputCallbackListener(MyEditText.KeyBoardInputCallbackListener { inputContentInfo, flags, opts ->
            //you will get your gif/png/jpg here in opts bundle
            runOnUiThread {
                if (inputContentInfo.linkUri != null) {
                    Log.e("data1", inputContentInfo.linkUri.toString())
                    Log.e("data2", File(inputContentInfo.contentUri.path!!).absolutePath)

                    //gotImage(File(inputContentInfo.contentUri.path!!))
                    receiverID?.let {
//                        val param = JSONObject()
//                        param.put("toId", mChatId!!)
//                        param.put("conversationId", receiverID!!)
//                        param.put("type", Constants.Socket_id.GIF_ID)
//                        param.put("url", inputContentInfo.linkUri.toString())
//                        Log.e("json_gif", param.toString())
//                        JDAApplication.mInstance.socketHelperObject!!.sendMessage(param)
                        attemptSend(inputContentInfo.linkUri.toString(), Constants.Socket_id.GIF_ID)
                    }
                } else {
                    showToast(getString(R.string.link_not_avaiable))
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        JDAApplication.mInstance.socketHelperObject!!.socketStartListener(null)
        Log.e("Socket", "Dis-Connected-call")
        JDAApplication.mInstance.socketHelperObject?.socketDisconnect()
        JDAApplication.mInstance.setUserID(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        JDAApplication.mInstance.setUserID(null)
    }

    private fun openAlertDialog(
            pTitle: String?, pMessage: String
            ?, pType: Int
            ?
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(pTitle)
        builder.setMessage(pMessage)
        builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, which ->
            when (pType) {
                Constants.ApiName.BlockUser.ordinal -> {
                    val param = JSONObject()
                    param.put("userId", receiverID!!)
                    JDAApplication.mInstance.socketHelperObject!!.typingEvent(
                            Constants.Socket_id.blockUserEvent,
                            param
                    )
//                        presenter!!.apiBlockUnBlockUser(MarkFavouriteUnFavRequestModel(true, userToID!!))
                }
                Constants.ApiName.ReportUser.ordinal -> {
//                        presenter!!.apiReportUser(ReportRequestModel(userToID))
                }
                Constants.ApiName.ClearChat.ordinal -> {
                    if (arrayList.size > 0) {
                        mLastMessageId = arrayList[0]._id
                        val param = JSONObject()
                        param.put(Constants.HashMapParamKeys.sLAST_MESSAGE_ID, mLastMessageId)
                        param.put(Constants.HashMapParamKeys.sCHAT_ID, mChatId!!)
                        JDAApplication.mInstance.socketHelperObject!!.clearChat(
                                param
                        )
                    }
                }
            }
        }
        builder.setNegativeButton(getString(R.string.no)) { dialogInterface, which ->
            mDialog?.dismiss()
        }
        mDialog = builder.create()
        mDialog?.setCancelable(false)
        mDialog?.show()
    }

    override fun blockUser(data: JSONObject) {
        this.runOnUiThread {
            val data = Gson().fromJson<BlockResponse>(data.toString(), BlockResponse::class.java)
            if (data.data?.blockedBy == JDAApplication.mInstance.getProfile()!!.result?._id) {
                mIsBlockedByMe = true
            } else if (data.data?.blockedBy == receiverID) {
                mIsBlockedByOther = true
            }
            markBlockUnBlock()
        }
    }

    override fun unBlockUser(data: JSONObject) {
        this.runOnUiThread {
            val data = Gson().fromJson<BlockResponse>(data.toString(), BlockResponse::class.java)
            if (data.data?.unBlockedBy == JDAApplication.mInstance.getProfile()!!.result?._id) {
                mIsBlockedByMe = false
            } else if (data.data?.unBlockedBy == receiverID) {
                mIsBlockedByOther = false
            }
            markBlockUnBlock()
        }
    }

    override fun userRated(data: JSONObject) {
        TODO("Not yet implemented")
    }

    override fun userChatCleared(data: JSONObject) {
        this.runOnUiThread {
            if (data.get("status") == 200) {
                arrayList.clear()
                setClearChatVisibilty()
                adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun setPopUpWindow() {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.chat_screen_menu_layout, null)
        mClearChatTv = view.findViewById(R.id.clearChatTV) as TextView
        mBlockUnBlockTv = view.findViewById(R.id.blockTV) as TextView
        mPopupWindow = PopupWindow(
                view,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                true
        )
        mBlockUnBlockTv?.setOnClickListener {
            mPopupWindow!!.dismiss()
            if (!mIsBlockedByMe) {
//                openAlertDialog(
//                        getString(R.string.block),
//                        getString(R.string.block_user_confirmation_msg),
//                        Constants.ApiName.BlockUser.ordinal
//                )
                CommonUtility.closeKeyBoard(this)
                UserAlertUtility.openCustomDialog(
                        this, getString(R.string.block),
                        getString(R.string.block_user_confirmation_msg), null, null,
                        object : UserAlertUtility.CustomDialogClickListener {
                            override fun onYesClick() {
                                val param = JSONObject()
                                param.put("userId", receiverID!!)
                                JDAApplication.mInstance.socketHelperObject!!.typingEvent(
                                        Constants.Socket_id.blockUserEvent,
                                        param
                                )
                            }

                            override fun onNoClick() {

                            }

                        }, true)

            } else {
                CommonUtility.closeKeyBoard(this)
                UserAlertUtility.openCustomDialog(
                        this, getString(R.string.unblock),
                        getString(R.string.unblock_user_confirmation_msg), null, null,
                        object : UserAlertUtility.CustomDialogClickListener {
                            override fun onYesClick() {
                                val param = JSONObject()
                                param.put("userId", receiverID!!)
                                JDAApplication.mInstance.socketHelperObject!!.typingEvent(
                                        Constants.Socket_id.unBlockUserEvent,
                                        param
                                )
                            }

                            override fun onNoClick() {

                            }

                        }, true)


//                val param = JSONObject()
//                param.put("userId", receiverID!!)
//                JDAApplication.mInstance.socketHelperObject!!.typingEvent(
//                        Constants.Socket_id.unBlockUserEvent,
//                        param
//                )
            }

            /*if (userToID != null) {
                if (!mIsBlockedByMe) {
                    mPopupWindow!!.dismiss()
                    mAddToFavourite = -1
                    openAlertDialog(getString(R.string.block), getString(R.string.block_user_confirmation_msg), Constants.ApiName.BlockUser.ordinal)
                } else {
                    mPopupWindow!!.dismiss()
                    mAddToFavourite = 1
                    presenter!!.apiBlockUnBlockUser(MarkFavouriteUnFavRequestModel(false, userToID!!))
                }
            }*/
        }
        mClearChatTv?.setOnClickListener {
//            this.showToast("Coming soon")
//                if (userToID != null && conversationID != null) {
            mPopupWindow!!.dismiss()
            if (arrayList.size > 0) {
//                openAlertDialog(
//                        getString(R.string.clear_chat),
//                        getString(R.string.clear_chat_confirmation_msg),
//                        Constants.ApiName.ClearChat.ordinal
//                )
                CommonUtility.closeKeyBoard(this)
                UserAlertUtility.openCustomDialog(
                        this, getString(R.string.clear_chat),
                        getString(R.string.clear_chat_confirmation_msg), null, null,
                        object : UserAlertUtility.CustomDialogClickListener {
                            override fun onYesClick() {
                                if (arrayList.size > 0) {
                                    mLastMessageId = arrayList[0]._id
                                    val param = JSONObject()
                                    param.put(Constants.HashMapParamKeys.sLAST_MESSAGE_ID, mLastMessageId)
                                    param.put(Constants.HashMapParamKeys.sCHAT_ID, mChatId!!)
                                    JDAApplication.mInstance.socketHelperObject!!.clearChat(
                                            param
                                    )
                                }
                            }

                            override fun onNoClick() {

                            }

                        }, true)
            }
//                }
        }
    }


    private var binding: WriteReviewDesignBinding? = null
    private var chatDialog: Dialog? = null
    private fun openDialog() {
        val dialog = AlertDialog.Builder(this)
        binding = DataBindingUtil.inflate<WriteReviewDesignBinding>(
                LayoutInflater.from(this),
                R.layout.write_review_design,
                null,
                false
        )
        dialog.setView(binding!!.root)
        binding!!.subHeadingTV.text =
                getString(R.string.review_your_chat_with) + " " + capitaliseOnlyFirstLetter(data?.user?.firstName) + " " + capitaliseOnlyFirstLetter(
                        data?.user?.lastName
                )

        param = ChatRatingRequest()
        setHeightLight(binding!!.firstTV)
        setHeightLight(binding!!.secondTV)
        setHeightLight(binding!!.thirdTV)
        setHeightLight(binding!!.fourthTV)
        setHeightLight(binding!!.fivthTV)
        binding!!.firstTV.setOnClickListener(clickDialog)
        binding!!.secondTV.setOnClickListener(clickDialog)
        binding!!.thirdTV.setOnClickListener(clickDialog)
        binding!!.fourthTV.setOnClickListener(clickDialog)
        binding!!.fivthTV.setOnClickListener(clickDialog)

        setHeightLight(binding!!.first1TV)
        setHeightLight(binding!!.second1TV)
        setHeightLight(binding!!.third1TV)
        setHeightLight(binding!!.fourth1TV)
        setHeightLight(binding!!.fivth1TV)
        binding!!.first1TV.setOnClickListener(clickDialog1)
        binding!!.second1TV.setOnClickListener(clickDialog1)
        binding!!.third1TV.setOnClickListener(clickDialog1)
        binding!!.fourth1TV.setOnClickListener(clickDialog1)
        binding!!.fivth1TV.setOnClickListener(clickDialog1)

        setHeightLight(binding!!.first2TV)
        setHeightLight(binding!!.second2TV)
        setHeightLight(binding!!.third2TV)
        setHeightLight(binding!!.fourth2TV)
        setHeightLight(binding!!.fivth2TV)
        binding!!.first2TV.setOnClickListener(clickDialog2)
        binding!!.second2TV.setOnClickListener(clickDialog2)
        binding!!.third2TV.setOnClickListener(clickDialog2)
        binding!!.fourth2TV.setOnClickListener(clickDialog2)
        binding!!.fivth2TV.setOnClickListener(clickDialog2)
        chatDialog = dialog.create()
        binding!!.chatCancelBT.setOnClickListener {
//            chatDialog!!.dismiss()
            apiHitRejectChatRating()
        }
        binding!!.chatSubmitBT.setOnClickListener {
            if (param!!.behavior != null && param!!.responseTime != null && param!!.goodListener != null) {
                apiHitChatRating()
            } else {
                showToast(getString(R.string.please_give_rating))
            }
        }
        binding?.chatCancelBT?.text = getString(R.string.not_now)
        chatDialog!!.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        chatDialog?.setCancelable(false)
        chatDialog!!.show()
    }

    val clickDialog = View.OnClickListener {
        when (it.id) {
            R.id.firstTV -> {
                param!!.goodListener = 1
                setRedLight(binding!!.firstTV)
                setHeightLight(binding!!.secondTV)
                setHeightLight(binding!!.thirdTV)
                setHeightLight(binding!!.fourthTV)
                setHeightLight(binding!!.fivthTV)
            }
            R.id.secondTV -> {
                param!!.goodListener = 2
                setRedLight(binding!!.firstTV)
                setRedLight(binding!!.secondTV)
                setHeightLight(binding!!.thirdTV)
                setHeightLight(binding!!.fourthTV)
                setHeightLight(binding!!.fivthTV)
            }
            R.id.thirdTV -> {
                param!!.goodListener = 3
                setRedLight(binding!!.firstTV)
                setRedLight(binding!!.secondTV)
                setRedLight(binding!!.thirdTV)
                setHeightLight(binding!!.fourthTV)
                setHeightLight(binding!!.fivthTV)
            }
            R.id.fourthTV -> {
                param!!.goodListener = 4
                setRedLight(binding!!.firstTV)
                setRedLight(binding!!.secondTV)
                setRedLight(binding!!.thirdTV)
                setRedLight(binding!!.fourthTV)
                setHeightLight(binding!!.fivthTV)
            }
            R.id.fivthTV -> {
                param!!.goodListener = 5
                setRedLight(binding!!.firstTV)
                setRedLight(binding!!.secondTV)
                setRedLight(binding!!.thirdTV)
                setRedLight(binding!!.fourthTV)
                setRedLight(binding!!.fivthTV)
            }
        }
    }

    val clickDialog1 = View.OnClickListener {
        when (it.id) {
            R.id.first1TV -> {
                param!!.responseTime = 1

                setRedLight(binding!!.first1TV)
                setHeightLight(binding!!.second1TV)
                setHeightLight(binding!!.third1TV)
                setHeightLight(binding!!.fourth1TV)
                setHeightLight(binding!!.fivth1TV)
            }
            R.id.second1TV -> {
                param!!.responseTime = 2
                setRedLight(binding!!.first1TV)
                setRedLight(binding!!.second1TV)
                setHeightLight(binding!!.third1TV)
                setHeightLight(binding!!.fourth1TV)
                setHeightLight(binding!!.fivth1TV)
            }
            R.id.third1TV -> {
                param!!.responseTime = 3
                setRedLight(binding!!.first1TV)
                setRedLight(binding!!.second1TV)
                setRedLight(binding!!.third1TV)
                setHeightLight(binding!!.fourth1TV)
                setHeightLight(binding!!.fivth1TV)
            }
            R.id.fourth1TV -> {
                param!!.responseTime = 4
                setRedLight(binding!!.first1TV)
                setRedLight(binding!!.second1TV)
                setRedLight(binding!!.third1TV)
                setRedLight(binding!!.fourth1TV)
                setHeightLight(binding!!.fivth1TV)
            }
            R.id.fivth1TV -> {
                param!!.responseTime = 5
                setRedLight(binding!!.first1TV)
                setRedLight(binding!!.second1TV)
                setRedLight(binding!!.third1TV)
                setRedLight(binding!!.fourth1TV)
                setRedLight(binding!!.fivth1TV)
            }
        }
    }

    val clickDialog2 = View.OnClickListener {
        when (it.id) {
            R.id.first2TV -> {
                param!!.behavior = 1
                setRedLight(binding!!.first2TV)
                setHeightLight(binding!!.second2TV)
                setHeightLight(binding!!.third2TV)
                setHeightLight(binding!!.fourth2TV)
                setHeightLight(binding!!.fivth2TV)
            }
            R.id.second2TV -> {
                param!!.behavior = 2
                setRedLight(binding!!.first2TV)
                setRedLight(binding!!.second2TV)
                setHeightLight(binding!!.third2TV)
                setHeightLight(binding!!.fourth2TV)
                setHeightLight(binding!!.fivth2TV)
            }
            R.id.third2TV -> {
                param!!.behavior = 3
                setRedLight(binding!!.first2TV)
                setRedLight(binding!!.second2TV)
                setRedLight(binding!!.third2TV)
                setHeightLight(binding!!.fourth2TV)
                setHeightLight(binding!!.fivth2TV)
            }
            R.id.fourth2TV -> {
                param!!.behavior = 4
                setRedLight(binding!!.first2TV)
                setRedLight(binding!!.second2TV)
                setRedLight(binding!!.third2TV)
                setRedLight(binding!!.fourth2TV)
                setHeightLight(binding!!.fivth2TV)
            }
            R.id.fivth2TV -> {
                param!!.behavior = 5
                setRedLight(binding!!.first2TV)
                setRedLight(binding!!.second2TV)
                setRedLight(binding!!.third2TV)
                setRedLight(binding!!.fourth2TV)
                setRedLight(binding!!.fivth2TV)
            }
        }
    }

    private fun setHeightLight(view: TextView) {
        val top = resources.getDrawable(R.drawable.ic_baseline_star_grey_24)
        view.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null)
    }

    private fun setRedLight(view: TextView) {
        val top = resources.getDrawable(R.drawable.ic_baseline_star_red_24)
        view.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null)
    }

    /* meet dialog */

    private fun openDialogIsMeet() {
        val dialog = AlertDialog.Builder(this)
        val binding = DataBindingUtil.inflate<DialogMeetDesignBinding>(
                LayoutInflater.from(this),
                R.layout.dialog_meet_design,
                null,
                false
        )
        dialog.setView(binding!!.root)
        binding.headingTagTV.setText(
                "Did you and ${capitaliseOnlyFirstLetter(data?.user?.firstName)} ${
                    capitaliseOnlyFirstLetter(
                            data?.user?.lastName
                    )
                } meet?"
        )
//                "\nClick yes to rate your date")
        val ddd = dialog.create()
        binding.meetYesBT.setOnClickListener {
            ddd.dismiss()
            openDialogMeet()
        }
        binding.meetNoBT.setOnClickListener { ddd.dismiss() }
        ddd.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ddd.setCancelable(false)
        ddd.show()
    }


    /*
    after meet dialog
    */

    private var meetBinding: DialogAfterMeetDesignBinding? = null
    private var meetDialog: Dialog? = null

    private fun openDialogMeet() {
        val dialog = AlertDialog.Builder(this)
        meetBinding = DataBindingUtil.inflate<DialogAfterMeetDesignBinding>(
                LayoutInflater.from(this),
                R.layout.dialog_after_meet_design,
                null,
                false
        )
        dialog.setView(meetBinding!!.root)
        meetBinding!!.subHeadingTV.text =
                getString(R.string.review_your_date_with) + " " + (capitaliseOnlyFirstLetter(data?.user?.firstName) + " " + capitaliseOnlyFirstLetter(
                        data?.user?.lastName
                ))


        paramMeet = MeetRatingRequest()
        setHeightLight(meetBinding!!.firstTV)
        setHeightLight(meetBinding!!.secondTV)
        setHeightLight(meetBinding!!.thirdTV)
        setHeightLight(meetBinding!!.fourthTV)
        setHeightLight(meetBinding!!.fivthTV)
        meetBinding!!.firstTV.setOnClickListener(clickDialog10)
        meetBinding!!.secondTV.setOnClickListener(clickDialog10)
        meetBinding!!.thirdTV.setOnClickListener(clickDialog10)
        meetBinding!!.fourthTV.setOnClickListener(clickDialog10)
        meetBinding!!.fivthTV.setOnClickListener(clickDialog10)

        setHeightLight(meetBinding!!.first1TV)
        setHeightLight(meetBinding!!.second1TV)
        setHeightLight(meetBinding!!.third1TV)
        setHeightLight(meetBinding!!.fourth1TV)
        setHeightLight(meetBinding!!.fivth1TV)
        meetBinding!!.first1TV.setOnClickListener(clickDialog11)
        meetBinding!!.second1TV.setOnClickListener(clickDialog11)
        meetBinding!!.third1TV.setOnClickListener(clickDialog11)
        meetBinding!!.fourth1TV.setOnClickListener(clickDialog11)
        meetBinding!!.fivth1TV.setOnClickListener(clickDialog11)

        setHeightLight(meetBinding!!.first2TV)
        setHeightLight(meetBinding!!.second2TV)
        setHeightLight(meetBinding!!.third2TV)
        setHeightLight(meetBinding!!.fourth2TV)
        setHeightLight(meetBinding!!.fivth2TV)
        meetBinding!!.first2TV.setOnClickListener(clickDialog12)
        meetBinding!!.second2TV.setOnClickListener(clickDialog12)
        meetBinding!!.third2TV.setOnClickListener(clickDialog12)
        meetBinding!!.fourth2TV.setOnClickListener(clickDialog12)
        meetBinding!!.fivth2TV.setOnClickListener(clickDialog12)

        setHeightLight(meetBinding!!.first3TV)
        setHeightLight(meetBinding!!.second3TV)
        setHeightLight(meetBinding!!.third3TV)
        setHeightLight(meetBinding!!.fourth3TV)
        setHeightLight(meetBinding!!.fivth3TV)
        meetBinding!!.first3TV.setOnClickListener(clickDialog13)
        meetBinding!!.second3TV.setOnClickListener(clickDialog13)
        meetBinding!!.third3TV.setOnClickListener(clickDialog13)
        meetBinding!!.fourth3TV.setOnClickListener(clickDialog13)
        meetBinding!!.fivth3TV.setOnClickListener(clickDialog13)

        setHeightLight(meetBinding!!.first4TV)
        setHeightLight(meetBinding!!.second4TV)
        setHeightLight(meetBinding!!.third4TV)
        setHeightLight(meetBinding!!.fourth4TV)
        setHeightLight(meetBinding!!.fivth4TV)
        meetBinding!!.first4TV.setOnClickListener(clickDialog14)
        meetBinding!!.second4TV.setOnClickListener(clickDialog14)
        meetBinding!!.third4TV.setOnClickListener(clickDialog14)
        meetBinding!!.fourth4TV.setOnClickListener(clickDialog14)
        meetBinding!!.fivth4TV.setOnClickListener(clickDialog14)

        meetBinding?.meetRatingCancelBT?.text = getString(R.string.not_now)

        meetDialog = dialog.create()
        meetBinding!!.meetRatingSubmitBT.setOnClickListener {
            if (paramMeet!!.behavior != null && paramMeet!!.communication != null && paramMeet!!.punctuality != null) {
                apiHitForMeetrating()
            } else {
                showToast(getString(R.string.please_give_rating))
            }
        }
        meetBinding!!.meetRatingCancelBT.setOnClickListener { meetDialog!!.dismiss() }
        meetDialog?.setCancelable(false)
        meetDialog!!.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        meetDialog!!.show()
    }

    val clickDialog10 = View.OnClickListener {
        when (it.id) {
            R.id.firstTV -> {
                paramMeet!!.punctuality = 1
                setRedLight(meetBinding!!.firstTV)
                setHeightLight(meetBinding!!.secondTV)
                setHeightLight(meetBinding!!.thirdTV)
                setHeightLight(meetBinding!!.fourthTV)
                setHeightLight(meetBinding!!.fivthTV)
            }
            R.id.secondTV -> {
                paramMeet!!.punctuality = 2
                setRedLight(meetBinding!!.firstTV)
                setRedLight(meetBinding!!.secondTV)
                setHeightLight(meetBinding!!.thirdTV)
                setHeightLight(meetBinding!!.fourthTV)
                setHeightLight(meetBinding!!.fivthTV)
            }
            R.id.thirdTV -> {
                paramMeet!!.punctuality = 3
                setRedLight(meetBinding!!.firstTV)
                setRedLight(meetBinding!!.secondTV)
                setRedLight(meetBinding!!.thirdTV)
                setHeightLight(meetBinding!!.fourthTV)
                setHeightLight(meetBinding!!.fivthTV)
            }
            R.id.fourthTV -> {
                paramMeet!!.punctuality = 4
                setRedLight(meetBinding!!.firstTV)
                setRedLight(meetBinding!!.secondTV)
                setRedLight(meetBinding!!.thirdTV)
                setRedLight(meetBinding!!.fourthTV)
                setHeightLight(meetBinding!!.fivthTV)
            }
            R.id.fivthTV -> {
                paramMeet!!.punctuality = 5
                setRedLight(meetBinding!!.firstTV)
                setRedLight(meetBinding!!.secondTV)
                setRedLight(meetBinding!!.thirdTV)
                setRedLight(meetBinding!!.fourthTV)
                setRedLight(meetBinding!!.fivthTV)
            }
        }
    }

    val clickDialog11 = View.OnClickListener {
        when (it.id) {
            R.id.first1TV -> {
                paramMeet!!.communication = 1
                setRedLight(meetBinding!!.first1TV)
                setHeightLight(meetBinding!!.second1TV)
                setHeightLight(meetBinding!!.third1TV)
                setHeightLight(meetBinding!!.fourth1TV)
                setHeightLight(meetBinding!!.fivth1TV)
            }
            R.id.second1TV -> {
                paramMeet!!.communication = 2
                setRedLight(meetBinding!!.first1TV)
                setRedLight(meetBinding!!.second1TV)
                setHeightLight(meetBinding!!.third1TV)
                setHeightLight(meetBinding!!.fourth1TV)
                setHeightLight(meetBinding!!.fivth1TV)
            }
            R.id.third1TV -> {
                paramMeet!!.communication = 3
                setRedLight(meetBinding!!.first1TV)
                setRedLight(meetBinding!!.second1TV)
                setRedLight(meetBinding!!.third1TV)
                setHeightLight(meetBinding!!.fourth1TV)
                setHeightLight(meetBinding!!.fivth1TV)
            }
            R.id.fourth1TV -> {
                paramMeet!!.communication = 4
                setRedLight(meetBinding!!.first1TV)
                setRedLight(meetBinding!!.second1TV)
                setRedLight(meetBinding!!.third1TV)
                setRedLight(meetBinding!!.fourth1TV)
                setHeightLight(meetBinding!!.fivth1TV)
            }
            R.id.fivth1TV -> {
                paramMeet!!.communication = 5
                setRedLight(meetBinding!!.first1TV)
                setRedLight(meetBinding!!.second1TV)
                setRedLight(meetBinding!!.third1TV)
                setRedLight(meetBinding!!.fourth1TV)
                setRedLight(meetBinding!!.fivth1TV)
            }
        }
    }

    val clickDialog12 = View.OnClickListener {
        when (it.id) {
            R.id.first2TV -> {
                paramMeet!!.behavior = 1
                setRedLight(meetBinding!!.first2TV)
                setHeightLight(meetBinding!!.second2TV)
                setHeightLight(meetBinding!!.third2TV)
                setHeightLight(meetBinding!!.fourth2TV)
                setHeightLight(meetBinding!!.fivth2TV)
            }
            R.id.second2TV -> {
                paramMeet!!.behavior = 2
                setRedLight(meetBinding!!.first2TV)
                setRedLight(meetBinding!!.second2TV)
                setHeightLight(meetBinding!!.third2TV)
                setHeightLight(meetBinding!!.fourth2TV)
                setHeightLight(meetBinding!!.fivth2TV)
            }
            R.id.third2TV -> {
                paramMeet!!.behavior = 3
                setRedLight(meetBinding!!.first2TV)
                setRedLight(meetBinding!!.second2TV)
                setRedLight(meetBinding!!.third2TV)
                setHeightLight(meetBinding!!.fourth2TV)
                setHeightLight(meetBinding!!.fivth2TV)
            }
            R.id.fourth2TV -> {
                paramMeet!!.behavior = 4
                setRedLight(meetBinding!!.first2TV)
                setRedLight(meetBinding!!.second2TV)
                setRedLight(meetBinding!!.third2TV)
                setRedLight(meetBinding!!.fourth2TV)
                setHeightLight(meetBinding!!.fivth2TV)
            }
            R.id.fivth2TV -> {
                paramMeet!!.behavior = 5
                setRedLight(meetBinding!!.first2TV)
                setRedLight(meetBinding!!.second2TV)
                setRedLight(meetBinding!!.third2TV)
                setRedLight(meetBinding!!.fourth2TV)
                setRedLight(meetBinding!!.fivth2TV)
            }
        }
    }

    val clickDialog13 = View.OnClickListener {
        when (it.id) {
            R.id.first3TV -> {
                paramMeet!!.pics = 1
                setRedLight(meetBinding!!.first3TV)
                setHeightLight(meetBinding!!.second3TV)
                setHeightLight(meetBinding!!.third3TV)
                setHeightLight(meetBinding!!.fourth3TV)
                setHeightLight(meetBinding!!.fivth3TV)
            }
            R.id.second3TV -> {
                paramMeet!!.pics = 2
                setRedLight(meetBinding!!.first3TV)
                setRedLight(meetBinding!!.second3TV)
                setHeightLight(meetBinding!!.third3TV)
                setHeightLight(meetBinding!!.fourth3TV)
                setHeightLight(meetBinding!!.fivth3TV)
            }
            R.id.third3TV -> {
                paramMeet!!.pics = 3
                setRedLight(meetBinding!!.first3TV)
                setRedLight(meetBinding!!.second3TV)
                setRedLight(meetBinding!!.third3TV)
                setHeightLight(meetBinding!!.fourth3TV)
                setHeightLight(meetBinding!!.fivth3TV)
            }
            R.id.fourth3TV -> {
                paramMeet!!.pics = 4
                setRedLight(meetBinding!!.first3TV)
                setRedLight(meetBinding!!.second3TV)
                setRedLight(meetBinding!!.third3TV)
                setRedLight(meetBinding!!.fourth3TV)
                setHeightLight(meetBinding!!.fivth3TV)
            }
            R.id.fivth3TV -> {
                paramMeet!!.pics = 5
                setRedLight(meetBinding!!.first3TV)
                setRedLight(meetBinding!!.second3TV)
                setRedLight(meetBinding!!.third3TV)
                setRedLight(meetBinding!!.fourth3TV)
                setRedLight(meetBinding!!.fivth3TV)
            }
        }
    }

    val clickDialog14 = View.OnClickListener {
        when (it.id) {
            R.id.first4TV -> {
                paramMeet!!.praiseExpression = 1
                setRedLight(meetBinding!!.first4TV)
                setHeightLight(meetBinding!!.second4TV)
                setHeightLight(meetBinding!!.third4TV)
                setHeightLight(meetBinding!!.fourth4TV)
                setHeightLight(meetBinding!!.fivth4TV)
            }
            R.id.second4TV -> {
                paramMeet!!.praiseExpression = 2
                setRedLight(meetBinding!!.first4TV)
                setRedLight(meetBinding!!.second4TV)
                setHeightLight(meetBinding!!.third4TV)
                setHeightLight(meetBinding!!.fourth4TV)
                setHeightLight(meetBinding!!.fivth4TV)
            }
            R.id.third4TV -> {
                paramMeet!!.praiseExpression = 3
                setRedLight(meetBinding!!.first4TV)
                setRedLight(meetBinding!!.second4TV)
                setRedLight(meetBinding!!.third4TV)
                setHeightLight(meetBinding!!.fourth4TV)
                setHeightLight(meetBinding!!.fivth4TV)
            }
            R.id.fourth4TV -> {
                paramMeet!!.praiseExpression = 4
                setRedLight(meetBinding!!.first4TV)
                setRedLight(meetBinding!!.second4TV)
                setRedLight(meetBinding!!.third4TV)
                setRedLight(meetBinding!!.fourth4TV)
                setHeightLight(meetBinding!!.fivth4TV)
            }
            R.id.fivth4TV -> {
                paramMeet!!.praiseExpression = 5
                setRedLight(meetBinding!!.first4TV)
                setRedLight(meetBinding!!.second4TV)
                setRedLight(meetBinding!!.third4TV)
                setRedLight(meetBinding!!.fourth4TV)
                setRedLight(meetBinding!!.fivth4TV)
            }
        }
    }

    var param: ChatRatingRequest? = null
    var paramMeet: MeetRatingRequest? = null

    private fun apiHitChatRating() {
        param!!.rated = receiverID
        param!!.chatId = mChatId.toString()
        param!!.comment = binding!!.commentET.text.toString()
        presenter!!.apiChatRating(param!!)
    }

    private fun apiHitRejectChatRating() {
        val param = ChatRatingRejectRequest()
        param.chatId = mChatId.toString()
        presenter!!.apiRejectChatRating(param)
    }

    private fun apiHitForMeetrating() {
        paramMeet!!.rated = receiverID
        paramMeet!!.chatId = mChatId.toString()
        paramMeet!!.comment = meetBinding!!.commentET.text.toString()
        presenter!!.apiMeetRating(paramMeet!!)
    }
}