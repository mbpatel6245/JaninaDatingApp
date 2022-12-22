package com.jda.application.fragments.homeFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.google.gson.Gson
import com.jda.application.R
import com.jda.application.acivities.DashboardActivity
import com.jda.application.acivities.JDAApplication
import com.jda.application.acivities.othersProfileModule.OtherProfileActivity
import com.jda.application.adapter.UserListAdapter
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.databinding.FragmentHomeBinding
import com.jda.application.fragments.chatModule.BlockResponse
import com.jda.application.fragments.edit_profile.EditProfileActivity
import com.jda.application.fragments.subscriptions.SubscriptionRequest
import com.jda.application.fragments.subscriptions.SubscriptionSuccessResponse
import com.jda.application.utils.CommonUtility
import com.jda.application.utils.Constants
import com.jda.application.utils.UserAlertUtility
import com.jda.application.utils.UserAlertUtility.Companion.showToast
import com.jda.application.utils.socket_utils.SocketHelper
import com.jda.application.utils.subscription.BillingManager
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class HomeFragment : BaseFragment(), UserListAdapter.UserClickListener,
    CardStackListener, SocketHelper.CallBack {

    private var mBinding: FragmentHomeBinding? = null
    private var mUserAdapter: UserListAdapter? = null
    private var mPresenter: HomePresenter? = null
    private var mUserDataList = ArrayList<HomeSuccessResponse.User>()
    private var mPageTotal = Constants.PAGE
    private var mPageCount = 1
    private var isSingleHit = false
    private var mLimit = Constants.LIMIT
    private var isOnline = false
    private var mCardStackLayoutManager: CardStackLayoutManager? = null
    private var mUserId: String? = null
    private var mPosition: Int = -1
    private var isBlockCalled: Boolean = false
    private lateinit var mBillingManager: BillingManager

    companion object {
        val TAG: String = HomeFragment::class.java.simpleName
        fun newInstance() = HomeFragment()
        var mIsLikedFromProfile: Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        mCardStackLayoutManager = CardStackLayoutManager(mActivity, this).apply {
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
            setStackFrom(StackFrom.Bottom)
            setVisibleCount(3)
            setScaleInterval(0.90f)
            setTranslationInterval(12.0f)
        }
        mBinding?.userStackView?.layoutManager = mCardStackLayoutManager
        hitGetProfileApiFirstTime()

    }

    private fun initialise() {
        mPresenter = HomePresenterImp(this)
        mBillingManager = BillingManager(requireActivity())
    }

    private fun hitGetProfileApiFirstTime() {
        if (mUserDataList.isEmpty()) {
            isSingleHit = false
            isOnline = false
            mPageTotal = Constants.PAGE
            mUserDataList.clear()
            if (!isSingleHit) {
                isSingleHit = true
                val param = HashMap<String, Any>()
                /*val userid: String? = JDAApplication.mInstance.getProfile()?.result?._id
                param[Constants.HashMapParamKeys.sUSER_ID] = userid.toString()*/
                param[Constants.HashMapParamKeys.sPAGE] = mPageCount
                param[Constants.HashMapParamKeys.sLIMIT] = mLimit
                mPresenter?.apiHitGetProfile(param, false)
            }
        }
    }

    private fun initAdapter() {
        mUserAdapter = UserListAdapter(this, mUserDataList)
        mBinding?.userStackView?.adapter = mUserAdapter
        mBinding?.userStackView?.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        super.onSuccess(pResponse, pIsPaginatedCall)

        when (pResponse) {
            is HomeSuccessResponse -> {
                if (pResponse.result.users.isNullOrEmpty() && !pIsPaginatedCall!!) {
                    mBinding?.noUserFoundTV?.visibility = View.VISIBLE
                    mBinding?.userStackView?.visibility = View.GONE
                    mUserAdapter?.notifyDataSetChanged()
                    Log.i(
                        "MySubscription",
                        "Home Api Gives Active Status : " + pResponse.result.isActive
                    )

                    Constants.sIsSubscribed = pResponse.result.isActive
                    if (!pResponse.result.isActive) {
                        setUpBillingManager()
                    } else {
                        val profile = JDAApplication.mInstance.getProfile()
                        profile?.result?.isActive = pResponse.result?.isActive ?: false
                        profile?.result?.productId = pResponse.result?.productId
                        profile?.result?.expiryDate = pResponse.result?.expiryDate
                        if (JDAApplication.mInstance.getProfile()?.result?.status == Constants.ScreenStatus.sPREFERENCES_SET && Constants.sDialogShowingFirstTime) {
                            showFullProfileSetDialog()
                        }
                    }

                    if (activity != null)
                        (activity as DashboardActivity).loadBannerAd(requireActivity().ad_view)

                } else if (!pResponse.result.users.isNullOrEmpty() && !pIsPaginatedCall!!) {
                    mPageTotal = pResponse.result.pageCount
                    isSingleHit = mPageCount <= mPageTotal
                    mBinding?.noUserFoundTV?.visibility = View.GONE
                    mBinding?.userStackView?.visibility = View.VISIBLE
                    mUserDataList.addAll(pResponse.result.users as ArrayList<HomeSuccessResponse.User>)
                    initAdapter()
                    Log.i(
                        "MySubscription",
                        "Home Api Gives Active Status : " + pResponse.result.isActive
                    )
                    Constants.sIsSubscribed = pResponse.result.isActive
//                    Constants.sIsSubscribed = true
                    if (!pResponse.result.isActive) {
                        setUpBillingManager()
                    } else {
                        val profile = JDAApplication.mInstance.getProfile()
                        profile?.result?.isActive = pResponse.result?.isActive ?: false
                        profile?.result?.productId = pResponse.result?.productId
                        profile?.result?.expiryDate = pResponse.result?.expiryDate
                        if (JDAApplication.mInstance.getProfile()?.result?.status == Constants.ScreenStatus.sPREFERENCES_SET && Constants.sDialogShowingFirstTime) {
                            showFullProfileSetDialog()
                        }
                    }
                    if (activity != null)
                        (activity as DashboardActivity).loadBannerAd(requireActivity().ad_view)
                } else if (!pResponse.result.users.isNullOrEmpty() && pIsPaginatedCall!!) {
                    mPageTotal = pResponse.result.pageCount
                    isSingleHit = mPageCount <= mPageTotal
                    mUserDataList.addAll(pResponse.result?.users as ArrayList<HomeSuccessResponse.User>)
                    mUserAdapter?.notifyDataSetChanged()
                }
            }
            is SubscriptionSuccessResponse -> {
                val profile = JDAApplication.mInstance.getProfile()
                profile?.result?.isActive = pResponse.result?.isActive ?: false
                profile?.result?.productId = pResponse.result?.productId
                profile?.result?.expiryDate = pResponse.result?.expiryDate
                JDAApplication.mInstance.setProfile(profile)
                setSubscriptionKey(pResponse.result?.productId)
                if (activity != null)
                    (activity as DashboardActivity).loadBannerAd(requireActivity().ad_view)
                Log.i("MySubscription", "Subscribed Successfully")
                Log.i("MySubscription", "Subscribed Active Status : " + pResponse.result.isActive)
            }
        }
    }

    override fun onAdapterItemClick(pId: Int, pPosition: Int, pData: HomeSuccessResponse.User?) {
        mUserId = pData?._id
        mPosition = pPosition
        when (pId) {
            R.id.likeIV -> {
                val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Slow.duration)
                    .setInterpolator(LinearInterpolator())
                    .build()
                mCardStackLayoutManager?.setSwipeAnimationSetting(setting)
                mBinding?.userStackView?.swipe()
            }
            R.id.dislikeIV -> {
                val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Slow.duration)
                    .setInterpolator(LinearInterpolator())
                    .build()
                mCardStackLayoutManager?.setSwipeAnimationSetting(setting)
                mBinding?.userStackView?.swipe()
            }
            R.id.profileIV -> {
                if (Constants.sIsSubscribed) {
                    val intent = Intent(mActivity, OtherProfileActivity::class.java)
                    intent.putExtra(Constants.BundleParams.sUserId, pData?._id)
                    startActivity(intent)
                } else {
                    openProfilePic(pData?.image)
                }
            }
            R.id.blockIV -> {
                UserAlertUtility.openCustomDialog(
                    requireContext(), getString(R.string.block),
                    getString(R.string.block_user_confirmation_msg), null, null,
                    object : UserAlertUtility.CustomDialogClickListener {
                        override fun onYesClick() {
                            isBlockCalled = true
                            val setting = SwipeAnimationSetting.Builder()
                                .setDirection(Direction.Left)
                                .setDuration(Duration.Slow.duration)
                                .setInterpolator(LinearInterpolator())
                                .build()
                            mCardStackLayoutManager?.setSwipeAnimationSetting(setting)
                            mBinding?.userStackView?.swipe()
                        }

                        override fun onNoClick() {
                            isBlockCalled = false
                        }

                    }, true
                )
            }
        }
    }

    private fun openProfilePic(pUrl: String?) {
        CommonUtility.openFullImage(
            mActivity!!,
            pUrl, false, null
        )
    }

    private fun apiHitPagination() {
        if (!isSingleHit) {
            isSingleHit = true
            val param = HashMap<String, Any>()
           /* val userid: String? = JDAApplication.mInstance.getProfile()?.result?._id
            param[Constants.HashMapParamKeys.sUSER_ID] = userid.toString()*/
            param[Constants.HashMapParamKeys.sPAGE] = ++mPageCount
            param[Constants.HashMapParamKeys.sLIMIT] = mLimit
            mPresenter?.apiHitGetProfile(param, true)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mPosition != -1 && mIsLikedFromProfile) {
            mUserDataList.removeAt(mPosition)
            mUserAdapter?.notifyItemRemoved(mPosition)
        }
        JDAApplication.mInstance.socketHelperObject!!.socketStartListener(this)
        Log.e("Socket", "Connected-call")
        JDAApplication.mInstance.socketHelperObject?.socketConnect()
        JDAApplication.mInstance.setUserID(JDAApplication.mInstance.getProfile()?.result?._id)
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {
        when (direction) {
            Direction.Right -> {
                mUserId = mUserAdapter!!.data[mCardStackLayoutManager?.topPosition!! - 1]._id
                likeEvent()
            }
            Direction.Left -> {
                mUserId = mUserAdapter!!.data[mCardStackLayoutManager?.topPosition!! - 1]._id
                if (isBlockCalled) {
                    isBlockCalled = false
                    val param = JSONObject()
                    param.put("userId", mUserId!!)
                    JDAApplication.mInstance.socketHelperObject!!.typingEvent(
                        Constants.Socket_id.blockUserEvent,
                        param
                    )
                }
                dislikeEvent()
            }
            else -> return
        }

        if (mUserAdapter!!.itemCount > 5) {
            if (mCardStackLayoutManager?.topPosition == mUserAdapter!!.itemCount - 5) {
                apiHitPagination()
            }
        }

        if (mCardStackLayoutManager?.topPosition == mUserAdapter!!.itemCount) {
            mBinding?.noUserFoundTV?.visibility = View.VISIBLE
            mBinding?.userStackView?.visibility = View.GONE
        }
    }

    override fun onCardRewound() {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
        Log.e("swipe", position.toString())
    }

    override fun onCardDisappeared(view: View?, position: Int) {
    }

    private fun likeEvent() {
        mUserId?.let {
            val param = JSONObject()
            param.put(Constants.HashMapParamKeys.sUSER_ID, mUserId)
            Log.e("likeEvent", param.toString())
            JDAApplication.mInstance.socketHelperObject!!.likeEvent(param)
        }
    }

    private fun dislikeEvent() {
        mUserId?.let {
            val param = JSONObject()
            param.put(Constants.HashMapParamKeys.sUSER_ID, mUserId)
            Log.e("likeEvent", param.toString())
            JDAApplication.mInstance.socketHelperObject!!.dislikeEvent(param)
        }
    }

    private fun undoRequestEvent() {
        mUserId?.let {
            val param = JSONObject()
            param.put(Constants.HashMapParamKeys.sUSER_ID, mUserId)
            Log.e("likeEvent", param.toString())
            JDAApplication.mInstance.socketHelperObject!!.dislikeEvent(param)
        }
    }

    override fun onError(pErrorMessage: String?, pType: Constants.ErrorType, pErrorCode: Int?) {
        super.onError(pErrorMessage, pType, pErrorCode)
    }

    override fun onFailure(pErrorMessage: String?, pType: Constants.ErrorType) {
        super.onFailure(pErrorMessage, pType)
    }

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {
        when (tClass) {
            HomeSuccessResponse::class.java -> {
                mBinding?.shimmerFrameLayout?.visibility = View.VISIBLE
                mBinding?.userStackView?.visibility = View.GONE
                mBinding?.shimmerFrameLayout?.startShimmerAnimation()
            }
            SubscriptionSuccessResponse::class.java -> {
                UserAlertUtility.showProgressDialog(R.layout.progress_dialog, activity, false)
            }
        }
    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {
        when (tClass) {
            HomeSuccessResponse::class.java -> {
                mBinding?.userStackView?.visibility = View.VISIBLE
                mBinding?.shimmerFrameLayout?.visibility = View.GONE
                mBinding?.shimmerFrameLayout?.stopShimmerAnimation()
            }
            SubscriptionSuccessResponse::class.java -> {
                UserAlertUtility.hideProgressDialog()
            }
        }
    }

    private fun setUpBillingManager() {
        mBillingManager.startConnection(object : BillingManager.BillingClientListener {
            override fun isBillingClientReady(isReady: Boolean) {
                if (isReady) {
                    mBillingManager.queryPurchase(object :
                        BillingManager.SubscriptionPurchaseListener {
                        override fun queryPurchaseResponse(purchase: Purchase?) {
                            if (purchase != null) {
                                var productId: String? = null
                                purchase.skus?.forEach {
                                    productId = it.toString()
                                }
                                if (productId != null) {
                                    Log.e("QueryPurchaseData", "$purchase")
                                    activity?.runOnUiThread {
                                        Log.i("MySubscription", "Subscription Api Hit on Home")
                                        hitVerifySubApi(productId!!, purchase.purchaseToken)
                                    }
                                }
                            } else {
                                Log.i("MySubscription", "Query Purchase Null")
                            }
                        }

                        override fun purchaseResponse(purchase: Purchase?) {

                        }

                        override fun purchaseError(pMessage: String?) {
                            Log.i(
                                "TAG_SUBSCRIPTION",
                                "BillingManager: purchaseError Error $pMessage"
                            )
                        }

                        override fun skuDetailsSubs(skuList: MutableList<SkuDetails>?) {
                        }

                    })
                } else {
                    Log.i("TAG_SUBSCRIPTION", "BillingManager: getQueryPurchase isReady : $isReady")

                }

            }

            override fun error(error: String) {
                Log.i("TAG_SUBSCRIPTION", "BillingManager: getQueryPurchase isReady : $error")
            }

        })
    }

    private fun hitVerifySubApi(productId: String, purchaseToken: String) {
        val param = SubscriptionRequest(
            productId,
            purchaseToken,
            Constants.sPlatform
        )
        mPresenter?.apiVerifySub(param)
    }

    private fun setSubscriptionKey(productId: String?) {
        when (productId) {
            Constants.SubscriptionType.SubscriptionOneMonth.productId -> {
                Constants.sIsSubscribed = true
                Constants.sSubscription = Constants.SubscriptionType.SubscriptionOneMonth
            }
            Constants.SubscriptionType.SubscriptionThreeMonths.productId -> {
                Constants.sIsSubscribed = true
                Constants.sSubscription = Constants.SubscriptionType.SubscriptionThreeMonths
            }
            Constants.SubscriptionType.SubscriptionSixMonths.productId -> {
                Constants.sIsSubscribed = true
                Constants.sSubscription = Constants.SubscriptionType.SubscriptionSixMonths
            }
            else -> {
//                Constants.sIsSubscribed = false
                Constants.sIsSubscribed = false
                Constants.sSubscription = Constants.SubscriptionType.NoSubscription
            }
        }
        if (activity != null)
            (activity as DashboardActivity).loadBannerAd(requireActivity().ad_view)
    }

    private fun showFullProfileSetDialog() {
        Constants.sDialogShowingFirstTime = false
        UserAlertUtility.openCustomDialog(
            activity,
            getString(R.string.complete_your_profile),
            getString(R.string.set_full_profile_msg),
            getString(R.string.proceed),
            getString(R.string.skip),
            object : UserAlertUtility.CustomDialogClickListener {
                override fun onYesClick() {
                    val intent = Intent(mActivity, EditProfileActivity::class.java)
                    intent.putExtra(Constants.BundleParams.sFromSubscription, true)
                    startActivity(intent)
                }

                override fun onNoClick() {

                }
            },
            true
        )
    }

    override fun newMessageComing(data: JSONObject) {
    }

    override fun sendMessageCallBack(data: JSONObject?) {
    }

    override fun isTyping(isTyping: Boolean, conversationId: String) {
    }

    override fun socketConnected() {
    }

    override fun socketDisconnected() {
    }

    override fun socketReconnecting() {
    }

    override fun isReadCallBack(data: JSONObject) {
    }

    override fun connectCallBack(data: JSONObject?) {
    }

    override fun disConnectCallBack(data: JSONObject) {
    }

    override fun blockUser(data: JSONObject) {
        activity?.runOnUiThread {
            val data = Gson().fromJson<BlockResponse>(data.toString(), BlockResponse::class.java)
            if (data.data?.blockedBy == JDAApplication.mInstance.getProfile()!!.result?._id) {
                showToast("User blocked successfully", requireActivity())
            }
        }
    }

    override fun unBlockUser(data: JSONObject) {
        activity?.runOnUiThread {
            val data = Gson().fromJson<BlockResponse>(data.toString(), BlockResponse::class.java)
            if (data.data?.unBlockedBy == JDAApplication.mInstance.getProfile()!!.result?._id) {
                showToast("User UnBlocked successfully", requireActivity())
            }
        }
    }

    override fun userRated(data: JSONObject) {
    }

    override fun userChatCleared(data: JSONObject) {
    }

    override fun onPause() {
        super.onPause()
        JDAApplication.mInstance.socketHelperObject!!.socketStartListener(null)
        Log.e("Socket", "Dis-Connected-call")
        JDAApplication.mInstance.socketHelperObject?.socketDisconnect()
        JDAApplication.mInstance.setUserID(null)
    }
}