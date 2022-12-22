package com.jda.application.fragments.messagesModule

import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.jda.application.R
import com.jda.application.acivities.DashboardActivity
import com.jda.application.acivities.JDAApplication
import com.jda.application.acivities.othersProfileModule.OtherProfileActivity
import com.jda.application.adapter.ChatListAdapter
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.databinding.ChatListDeleteDialogBinding
import com.jda.application.databinding.FragmentChatListBinding
import com.jda.application.fragments.chatModule.ConversationActivity
import com.jda.application.fragments.chatModule.ConversationSuccessModel
import com.jda.application.utils.CommonUtility
import com.jda.application.utils.Constants
import com.jda.application.utils.OnItemClickListener
import com.jda.application.utils.swipe_utils.MyItemTouchHelper
import org.json.JSONObject

class ChatListFragment : BaseFragment(), OnItemClickListener, ChatListAdapter.CallBack {
    private var mBinding: FragmentChatListBinding? = null
    private var mAdapter: ChatListAdapter? = null
    private var mChatList = ArrayList<ChatListSuccessModel.Result.Chat>()
    private var mChatListData: ChatListSuccessModel.Result.Chat? = null
    private var mPresenter: ChatPresenter? = null
    private var mItemTouchHelper: MyItemTouchHelper? = null
    private var mForegroundView: View? = null
    private var mDialogConfirmDelete: Dialog? = null
    private var mPosition: Int? = null
    private var mConversationId: String? = null
    private var mUserId: String? = null
    private var mPageCount = 1
    private var isSingleHit = false
    private var mPage = Constants.PAGE
    private var mLimit = Constants.LIMIT
//    var mUserIdFromNotification: String? = null

    companion object {
        val TAG: String = ChatListFragment::class.java.simpleName
        fun newInstance() = ChatListFragment()
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra(Constants.BundleParams.DATA) &&
                    intent.getStringExtra(Constants.BundleParams.DATA) != null) {
                try {
                    val data = Gson().fromJson(
                            intent.getStringExtra(Constants.BundleParams.DATA), // get data from payload
                            ConversationSuccessModel.Result.Message.MessageX::class.java
                    )
                    updateAdapter(data)
                } catch (e: Exception) {
                    Log.d("ExceptionValue", "sendNotification: ${e.message}")
                }
            }
            Log.d("BroadcastReceiver", "sendNotification: ")
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_list, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter = ChatPresenterImpl(this)
        swipeRecyclerViewHandle()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
                mBroadcastReceiver,
                IntentFilter(Constants.BroadCastCodes.BROADCAST_CHAT)
        )
    }

    private fun swipeRecyclerViewHandle() {
        val itemTouchHelperCallback =
                object : MyItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.bindingAdapterPosition
                        Log.e("direction", direction.toString())
                        when (direction) {
                            MyItemTouchHelper.LEFT -> {
                                mPosition = position
                                mChatListData = mChatList[position]
                                openDeleteConfirmDialog()
                            }
                        }
                    }

                    override fun onChildDrawOver(
                            c: Canvas,
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder?,
                            dX: Float,
                            dY: Float,
                            actionState: Int,
                            isCurrentlyActive: Boolean
                    ) {
                        val foregroundView: View =
                                (viewHolder as ChatListAdapter.MyViewHolder).binding.foregroundView
                        getDefaultUIUtil().onDrawOver(
                                c,
                                recyclerView,
                                foregroundView,
                                dX,
                                dY,
                                actionState,
                                isCurrentlyActive
                        )
//                if (dX >= 0)
//                    foregroundView.setBackgroundColor(ContextCompat.getColor(mActivity!!, R.color.colorGrey))
                    }

                    override fun clearView(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder
                    ) {
                        val foregroundView: View =
                                (viewHolder as ChatListAdapter.MyViewHolder).binding.foregroundView
                        getDefaultUIUtil().clearView(foregroundView)
                    }

                    override fun onChildDraw(
                            c: Canvas,
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            dX: Float,
                            dY: Float,
                            actionState: Int,
                            isCurrentlyActive: Boolean
                    ) {
                        mForegroundView =
                                (viewHolder as ChatListAdapter.MyViewHolder).binding.foregroundView
                        getDefaultUIUtil().onDraw(
                                c,
                                recyclerView,
                                mForegroundView,
                                dX / 5,
                                dY,
                                actionState,
                                isCurrentlyActive
                        )
//                mForegroundView?.background = ContextCompat.getDrawable(mActivity!!, R.drawable.circle_ten_round_dark_grey_bg)
                    }

                    override fun onClicked(Position: Int) {
                        Log.e("forground", "yes")
                    }
                }
        mItemTouchHelper = MyItemTouchHelper(itemTouchHelperCallback)
        mItemTouchHelper?.attachToRecyclerView(mBinding?.chatListRV)

    }

    override fun onItemClicked(
            @IdRes id: Int,
            position: Int,
            data: ChatListSuccessModel.Result.Chat?
    ) {
        when (id) {
            R.id.backgroundView -> {
                Log.e("delete", "yes")
            }
            R.id.foregroundView -> {
                val intent = Intent(mActivity, ConversationActivity::class.java)
                intent.putExtra(Constants.BundleParams.sProfileData, data)
                startActivity(intent)
            }
            R.id.profileCV -> {
                val intent = Intent(mActivity, OtherProfileActivity::class.java)
                intent.putExtra(Constants.BundleParams.sUserId, data?.user?._id)
                intent.putExtra(Constants.BundleParams.sIsMatch, true)
                startActivity(intent)
            }
        }
    }

    override fun pagination() {
        if (!isSingleHit) {
            isSingleHit = true
            val param = HashMap<String, Any>()
            param[Constants.HashMapParamKeys.sPAGE] = mPageCount
            param[Constants.HashMapParamKeys.sLIMIT] = mLimit
            mPresenter?.apiGetChatList(param, false)
        }
    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        super.onSuccess(pResponse, pIsPaginatedCall)
        when (pResponse) {
            is ChatListSuccessModel -> {
                if (pResponse.result.chats.isNullOrEmpty() && !pIsPaginatedCall!!) {
                    mBinding?.emptyChatCL?.visibility = View.VISIBLE
                    mBinding?.chatListRV?.visibility = View.GONE
                    mAdapter?.notifyDataSetChanged()
                } else if (!pResponse.result.chats.isNullOrEmpty() && !pIsPaginatedCall!!) {
                    mPage = pResponse.result.pageCount
                    mLimit = Constants.LIMIT
                    mPageCount++
                    isSingleHit = mPageCount > mPage
                    mBinding?.emptyChatCL?.visibility = View.GONE
                    mBinding?.chatListRV?.visibility = View.VISIBLE
                    mChatList.clear()
                    mChatList.addAll(pResponse.result.chats as ArrayList<ChatListSuccessModel.Result.Chat>)
                    mAdapter = ChatListAdapter(this, mChatList)
                    mBinding?.chatListRV?.adapter = mAdapter
                    openConversationScreen()
                } else if (!pResponse.result.chats.isNullOrEmpty() && pIsPaginatedCall!!) {
                    mPage = pResponse.result.pageCount
                    mPageCount++
                    isSingleHit = mPageCount > mPage
                    mLimit = Constants.LIMIT
                    mChatList.addAll(pResponse.result.chats as ArrayList<ChatListSuccessModel.Result.Chat>)
                    mAdapter?.notifyDataSetChanged()
                    openConversationScreen()
                }
            }
        }
    }

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {
        when (tClass) {
            ChatListSuccessModel::class.java -> {
                mBinding?.shimmerFrameLayout?.visibility = View.VISIBLE
                mBinding?.chatListRV?.visibility = View.GONE
                mBinding?.shimmerFrameLayout?.startShimmer()
            }
        }
    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {
        when (tClass) {
            ChatListSuccessModel::class.java -> {
                mBinding?.shimmerFrameLayout?.visibility = View.GONE
                mBinding?.chatListRV?.visibility = View.VISIBLE
                mBinding?.shimmerFrameLayout?.stopShimmer()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isSingleHit = false
        mPageCount = 1
        if (!isSingleHit) {
            isSingleHit = true
            val param = HashMap<String, Any>()
            param[Constants.HashMapParamKeys.sPAGE] = mPageCount
            param[Constants.HashMapParamKeys.sLIMIT] = mLimit
            mPresenter?.apiGetChatList(param, false)
        }
    }

    private fun openDeleteConfirmDialog() {
        val binding = DataBindingUtil.inflate<ChatListDeleteDialogBinding>(
                LayoutInflater.from(mActivity),
                R.layout.chat_list_delete_dialog,
                null,
                false
        )
        binding.clickHandler = this
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setView(binding!!.root)
        mDialogConfirmDelete = builder.create()
        mDialogConfirmDelete!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        mDialogConfirmDelete!!.show()

        mDialogConfirmDelete?.setOnDismissListener {
            mItemTouchHelper?.stopDetection()
            MyItemTouchHelper.SimpleCallback.getDefaultUIUtil().clearView(mForegroundView)
            mItemTouchHelper?.attachToRecyclerView(this.mBinding?.chatListRV)
        }
    }

    private fun openDeleteMatchConfirmDialog(name: String?, chatId: String?, userId: String?, pLastMessageID: String?) {
        var dialog: AlertDialog? = null
        val binding = DataBindingUtil.inflate<ChatListDeleteDialogBinding>(
                LayoutInflater.from(mActivity),
                R.layout.chat_list_delete_dialog,
                null,
                false
        )
        binding.messageTV.text = "Do you also want to delete match with $name?"
        binding.headingTagTV.text = mActivity?.getString(R.string.delete_match)
        binding.clickHandler = object : OnItemClickListener {
            override fun onItemClick(item: View) {
                when (item.id) {
                    R.id.yesBT -> {
                        dialog?.dismiss()
                        deleteMatchEvent(chatId, userId, pLastMessageID)
                        removeItem()
                    }
                    R.id.noBT -> {
                        dialog?.dismiss()
                        removeItem()
                    }
                }

            }
        }
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setView(binding?.root)
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        dialog?.setOnDismissListener {
            mItemTouchHelper?.stopDetection()
            MyItemTouchHelper.SimpleCallback.getDefaultUIUtil().clearView(mForegroundView)
            mItemTouchHelper?.attachToRecyclerView(this.mBinding?.chatListRV)
        }
    }

    private fun removeItem() {
        mChatList.removeAt(mPosition!!)
        mAdapter?.notifyItemRemoved(mPosition!!)
        if (mChatList.isEmpty()) {
            mBinding?.emptyChatCL?.visibility = View.VISIBLE
            mBinding?.chatListRV?.visibility = View.GONE
        }
    }

    override fun onItemClick(item: View) {
        when (item.id) {
            R.id.yesBT -> {
                mDialogConfirmDelete?.dismiss()
                val name = "${CommonUtility.capitaliseOnlyFirstLetter(mChatList[mPosition!!].user.firstName)} ${CommonUtility.capitaliseOnlyFirstLetter(mChatList[mPosition!!].user.lastName)}"
                val chatId = mChatList[mPosition!!]._id
                val userId = mChatList[mPosition!!].user._id
                val lastMessageId = mChatList[mPosition!!].lastMessage?._id
                val unMatch = mChatList[mPosition!!].isUnmatchedByOtherUser
                deleteUserFromListEvent(chatId, lastMessageId)
//                mChatList.removeAt(mPosition!!)
//                mAdapter?.notifyItemRemoved(mPosition!!)
//                mActivity?.showToast(getString(R.string.user_delete_success_msg))
                if (!unMatch) {
                    openDeleteMatchConfirmDialog(name, chatId, userId, lastMessageId)
                } else {
                    removeItem()
                }
            }
            R.id.noBT -> {
                mDialogConfirmDelete?.dismiss()
            }
        }
    }

    private fun deleteUserFromListEvent(pChatId: String?, pLastMessageID: String?) {
        pChatId?.let {
            val param = JSONObject()
            param.put(Constants.HashMapParamKeys.sCHAT_ID, pChatId)
            param.put(Constants.HashMapParamKeys.sLAST_MESSAGE_ID, pLastMessageID)
            Log.e("socket", param.toString())
            JDAApplication.mInstance.socketHelperObject!!.deleteUserFromListEvent(param)
        }
    }

    private fun deleteMatchEvent(pChatId: String?, pUserId: String?, pLastMessageID: String?) {
        pChatId?.let {
            val param = JSONObject()
            param.put(Constants.HashMapParamKeys.sCHAT_ID, pChatId)
            param.put(Constants.HashMapParamKeys.sUSER_ID, pUserId)
            param.put(Constants.HashMapParamKeys.sLAST_MESSAGE_ID, pLastMessageID)
            Log.e("socket", param.toString())
            JDAApplication.mInstance.socketHelperObject!!.deleteMatch(param)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
                mBroadcastReceiver,
                IntentFilter(Constants.BroadCastCodes.BROADCAST_CHAT)
        )
    }

    /*
   * update count when new message arrives
   * */
    private fun updateAdapter(message: ConversationSuccessModel.Result.Message.MessageX) {
        for (data in mChatList) {
            if (message.chatId == data._id) {
                val count = data.unReadCount
                data.unReadCount = count + 1
                data.lastMessage?.text = message.text
                data.lastMessage?.createdAt = message.createdAt
                mAdapter?.notifyDataSetChanged()
                break
            }
        }
    }

    /*
    * open conversation screen only when we tap notification
    * */
    private fun openConversationScreen() {
        if (mActivity != null && (mActivity as DashboardActivity).mUserIdFromNotification != null) {
            Log.d("BroadcastReceiver", "onSuccess: ${(mActivity as DashboardActivity).mUserIdFromNotification}" +
                    " ${mChatList.size}")
            val userId = (mActivity as DashboardActivity).mUserIdFromNotification
            for (data in mChatList) {
                Log.d("BroadcastReceiver", "id: ${data._id} chatid: ${data.user._id}")
                if (userId == data._id) {
                    Log.d("BroadcastReceiver", "updateAdapter: ${isAdded}")
                    if (isAdded && mActivity != null) {
                        val intent = Intent(mActivity, ConversationActivity::class.java)
                        intent.putExtra(Constants.BundleParams.sProfileData, data)
                        startActivity(intent)
                    }
                    break
                }
            }

            (mActivity as DashboardActivity).mUserIdFromNotification = null
        }
    }

}