package com.jda.application.fragments.matchesModule

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.adapter.MatchesListAdapter
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.databinding.ChatListDeleteDialogBinding
import com.jda.application.databinding.FragmentMatchesRequestBinding
import com.jda.application.fragments.chatModule.ConversationActivity
import com.jda.application.utils.CommonUtility
import com.jda.application.utils.Constants
import com.jda.application.utils.OnItemClickListener
import com.jda.application.utils.socket_utils.SocketHelper
import com.jda.application.utils.swipe_utils.MyItemTouchHelper
import org.json.JSONObject

class MatchesRequestFragment : BaseFragment(), MatchesListAdapter.MatchesRequestItemListener,
        SocketHelper.MatchesEventCallBack {

    private var mBinding: FragmentMatchesRequestBinding? = null
    private var mAdapter: MatchesListAdapter? = null
    private var mPresenter: MatchesPresenter? = null
    private var mMatchesRequestList: MutableList<MatchesListSuccessModel.Result.User>? = null
    private var mPosition: Int? = null
    private var mDialog: Dialog? = null
    private var mPage = Constants.PAGE
    private var mLimit = Constants.LIMIT
    private var mPageCount = 1
    private var isSingleHit = false

    companion object {
        val TAG: String = MatchesRequestFragment::class.java.simpleName
        fun newInstance() = MatchesRequestFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_matches_request, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter = MatchesPresenterImpl(this)
        mMatchesRequestList = ArrayList()
    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        super.onSuccess(pResponse, pIsPaginatedCall)
        when (pResponse) {
            is MatchesListSuccessModel -> {
                if (pResponse.result.users.isNullOrEmpty() && !pIsPaginatedCall!!) {
                    mBinding?.emptyMatchesCL?.visibility = View.VISIBLE
                    mBinding?.matchesListRV?.visibility = View.GONE
                    mAdapter?.notifyDataSetChanged()
                } else if (!pResponse.result.users.isNullOrEmpty() && !pIsPaginatedCall!!) {
                    mPage = pResponse.result.pageCount
                    mLimit = Constants.LIMIT
                    mPageCount++
                    isSingleHit = mPageCount > mPage
                    mBinding?.emptyMatchesCL?.visibility = View.GONE
                    mBinding?.matchesListRV?.visibility = View.VISIBLE
                    mMatchesRequestList?.clear()
                    mMatchesRequestList?.addAll(pResponse.result.users as ArrayList<MatchesListSuccessModel.Result.User>)
                    mAdapter = MatchesListAdapter(this, mMatchesRequestList)
                    mBinding?.matchesListRV?.adapter = mAdapter
                } else if (!pResponse.result.users.isNullOrEmpty() && pIsPaginatedCall!!) {
                    mPage = pResponse.result.pageCount
                    mLimit = Constants.LIMIT
                    mPageCount++
                    isSingleHit = mPageCount > mPage
                    mMatchesRequestList?.addAll(pResponse.result.users as ArrayList<MatchesListSuccessModel.Result.User>)
                    mAdapter?.notifyDataSetChanged()
                }
            }
        }

    }

    override fun onItemClicked(
            id: Int,
            position: Int,
            pModel: MatchesListSuccessModel.Result.User
    ) {
        when (id) {
            R.id.acceptIV -> {
                mPosition = position

                openDialog(
                        getString(R.string.accept),
                        getString(R.string.request_accept_msg),
                        Constants.ApiName.MatchesRequestAccept.ordinal,
                        pModel
                )

//                openAlertDialog(
//                        getString(R.string.accept),
//                        getString(R.string.request_accept_msg),
//                        Constants.ApiName.MatchesRequestAccept.ordinal,
//                        pModel
//                )
            }
            R.id.rejectIV -> {
                mPosition = position
                if (pModel.status == Constants.MatchListStatus.Match.values) {

                    var name: String = if (pModel.user != null) {
                        "${CommonUtility.capitaliseOnlyFirstLetter(pModel.user.firstName)} ${
                            CommonUtility.capitaliseOnlyFirstLetter(
                                    pModel.user.lastName
                            )
                        } "
                    } else {
                        ""
                    }
//                    openDeleteMatchConfirmDialog(
//                            name,
//                            pModel
//                    )

                    openDialog(
                        getString(R.string.delete_match),
                        "Do you want to delete match with $name?",
                        Constants.ApiName.MatchDelete.ordinal,
                        pModel
                    )

//                    openAlertDialog(
//                        getString(R.string.delete),
//                        "Do you want to delete match with $name?",
//                        Constants.ApiName.MatchDelete.ordinal,
//                        pModel
//                    )
                } else {
                    openDialog(
                            getString(R.string.reject),
                            getString(R.string.request_reject_msg),
                            Constants.ApiName.MatchesRequestDecline.ordinal,
                            pModel
                    )

//                    openAlertDialog(
//                            getString(R.string.reject),
//                            getString(R.string.request_reject_msg),
//                            Constants.ApiName.MatchesRequestDecline.ordinal,
//                            pModel
//                    )
                }

            }
            R.id.foregroundView, R.id.profileCV -> {
//                val intent = Intent(mActivity, OtherProfileActivity::class.java)
//                if (pModel.user != null)
//                    intent.putExtra(Constants.BundleParams.sUserId, pModel.user._id)
//                intent.putExtra(Constants.BundleParams.sIsMatch, true)
//                startActivity(intent)

                if (pModel.status == Constants.MatchListStatus.Match.values) {
                    val intent =
                            Intent(mActivity, ConversationActivity::class.java)
                    val name =
                            "${CommonUtility.capitaliseOnlyFirstLetter(pModel.user.firstName)} ${
                                CommonUtility.capitaliseOnlyFirstLetter(pModel.user.lastName)
                            }"
                    intent.putExtra(Constants.BundleParams.sUserName, name)
                    intent.putExtra(
                            Constants.BundleParams.sUserImage,
                            pModel.user.image
                    )
                    intent.putExtra(
                            Constants.BundleParams.sUserId,
                            pModel.user._id
                    )
                    startActivity(intent)
                }
            }
        }
    }

    private fun openDialog(pTitle: String?,
                           pMessage: String?,
                           pType: Int?,
                           pModel: MatchesListSuccessModel.Result.User) {
        var dialog: android.app.AlertDialog? = null
        val binding = DataBindingUtil.inflate<ChatListDeleteDialogBinding>(
                LayoutInflater.from(mActivity),
                R.layout.chat_list_delete_dialog,
                null,
                false
        )
        binding.messageTV.text = pMessage
        binding.headingTagTV.text = pTitle
        binding.clickHandler = object : OnItemClickListener {
            override fun onItemClick(item: View) {
                when (item.id) {
                    R.id.yesBT -> {
                        when (pType) {
                            Constants.ApiName.MatchesRequestAccept.ordinal -> {
                                acceptEvent(pModel._id)
                            }
                            Constants.ApiName.MatchesRequestDecline.ordinal -> {
                                rejectEvent(pModel._id)
                            }
                            Constants.ApiName.MatchDelete.ordinal -> {
                                deleteMatchEvent(pModel.user._id)
                            }
                        }
                        dialog?.dismiss()
                    }
                    R.id.noBT -> {
                        dialog?.dismiss()
                    }
                }

            }
        }
        val builder = android.app.AlertDialog.Builder(mActivity!!)
        builder.setView(binding?.root)
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }

    override fun pagination() {
        if (!isSingleHit) {
            isSingleHit = true
            val param = HashMap<String, Any>()
            param[Constants.HashMapParamKeys.sPAGE] = mPageCount
            param[Constants.HashMapParamKeys.sLIMIT] = mLimit
            mPresenter?.apiGetMatchesRequestList(param, false)
        }
    }

    private fun openAlertDialog(
            pTitle: String?,
            pMessage: String?,
            pType: Int?,
            pModel: MatchesListSuccessModel.Result.User
    ) {
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setTitle(pTitle)
        builder.setMessage(pMessage)
        builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, which ->
            when (pType) {
                Constants.ApiName.MatchesRequestAccept.ordinal -> {
                    acceptEvent(pModel._id)
                }
                Constants.ApiName.MatchesRequestDecline.ordinal -> {
                    rejectEvent(pModel._id)
                }
                Constants.ApiName.MatchDelete.ordinal -> {
                    deleteMatchEvent(pModel.user._id)
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

    private fun acceptEvent(pMatchId: String?) {
        pMatchId?.let {
            val param = JSONObject()
            param.put(Constants.HashMapParamKeys.sMATCH_ID, pMatchId)
            Log.e("acceptEvent", param.toString())
            JDAApplication.mInstance.socketHelperObject!!.acceptMatchEvent(param)
        }
    }

    private fun rejectEvent(pMatchId: String?) {
        pMatchId?.let {
            val param = JSONObject()
            param.put(Constants.HashMapParamKeys.sMATCH_ID, pMatchId)
            Log.e("rejectEvent", param.toString())
            JDAApplication.mInstance.socketHelperObject!!.rejectMatchEvent(param)
        }
    }

    private fun deleteMatchEvent(pMatchId: String?) {
        pMatchId?.let {
            val param = JSONObject()
            param.put(Constants.HashMapParamKeys.sOTHER_USER_ID, pMatchId)
            Log.e("socket", param.toString())
            JDAApplication.mInstance.socketHelperObject!!.deleteMatch(param)
//            mActivity?.showToast("User unmatched")
            mMatchesRequestList?.removeAt(mPosition!!)
            mAdapter?.notifyItemRemoved(mPosition!!)
        }
    }

    override fun onResume() {
        super.onResume()
        JDAApplication.mInstance.socketHelperObject?.matchesEventListener(this)
        isSingleHit = false
        mPageCount = 1
        if (!isSingleHit) {
            isSingleHit = true
            val param = HashMap<String, Any>()
            param[Constants.HashMapParamKeys.sPAGE] = mPageCount
            param[Constants.HashMapParamKeys.sLIMIT] = mLimit
            mPresenter?.apiGetMatchesRequestList(param, false)
        }
    }

    override fun acceptMatchCallBack(data: JSONObject?) {
        mActivity?.runOnUiThread {
            val data11: JSONObject? = data?.getJSONObject("data")
            data11?.let {
//                mActivity?.showToast("Accepted")
                mMatchesRequestList?.get(mPosition!!)?.status =
                        Constants.MatchListStatus.Match.values
                mAdapter?.notifyItemChanged(mPosition!!)
//                mMatchesRequestList?.removeAt(mPosition!!)
//                mAdapter?.notifyItemRemoved(mPosition!!)
            }
        }
    }

    override fun rejectMatchCallBack(data: JSONObject?) {
        mActivity?.runOnUiThread {
            val data11: JSONObject? = data?.getJSONObject("data")
            data11?.let {
//                mActivity?.showToast("Rejected")
                mMatchesRequestList?.removeAt(mPosition!!)
                mAdapter?.notifyItemRemoved(mPosition!!)
            }
        }
    }

    override fun unMatchFromMatchesCallBack() {
//        mActivity?.runOnUiThread {
//                mActivity?.showToast("User unMatched")
//                mMatchesRequestList?.removeAt(mPosition!!)
//                mAdapter?.notifyItemRemoved(mPosition!!)
//        }
    }


    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {
        when (tClass) {
            MatchesListSuccessModel::class.java -> {
                mBinding?.shimmerFrameLayout?.visibility = View.VISIBLE
                mBinding?.matchesListRV?.visibility = View.GONE
                mBinding?.shimmerFrameLayout?.startShimmer()
            }
        }
    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {
        when (tClass) {
            MatchesListSuccessModel::class.java -> {
                mBinding?.shimmerFrameLayout?.visibility = View.GONE
                mBinding?.matchesListRV?.visibility = View.VISIBLE
                mBinding?.shimmerFrameLayout?.stopShimmer()
            }
        }
    }
}