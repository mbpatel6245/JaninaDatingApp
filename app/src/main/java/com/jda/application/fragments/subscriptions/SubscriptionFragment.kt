package com.jda.application.fragments.subscriptions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.SkuDetails
import com.jda.application.BuildConfig
import com.jda.application.R
import com.jda.application.acivities.DashboardActivity
import com.jda.application.acivities.JDAApplication
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.databinding.FragmentSubscriptionBinding
import com.jda.application.utils.Constants
import com.jda.application.utils.OnItemClickListener
import com.jda.application.utils.UserAlertUtility


class SubscriptionFragment : BaseFragment(), OnItemClickListener {
    private lateinit var binding: FragmentSubscriptionBinding
    private var skuList = ArrayList<SkuDetails>()
    private var presenter: SubscriptionPresenter? = null

    companion object {
        fun newInstance() = SubscriptionFragment()
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onScheduleEvent(type: String) {
//        skuList.forEach { it ->
//            if (type == it.sku) {
////                InAppPurchase.startPurchase(it, mActivity!!)
//            }
//        }
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //register Event bas
//        EventBus.getDefault().register(this)

//        InAppPurchase.startConnection(context, this)
    }

    override fun onDetach() {
        super.onDetach()
        //unregister Event bas
//        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subscription, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickHandle = this
        presenter = SubscriptionPresenterImp(this)
        setViewPager()
    }

    private fun setViewPager() {
        val viewPager = binding.subsViewPager
        viewPager.apply {
            clipToPadding = false
            setPadding(100, 0, 100, 0)
            pageMargin = 30
        }
//        val adapter = ViewPagerAdapter(childFragmentManager)
//        viewPager.adapter = adapter
    }


    override fun onItemClick(item: View) {
        when (item.id) {
            R.id.backBt -> {
                (activity as DashboardActivity).onBackPressed()
            }
        }
    }


    private fun apiSendSub(sku: String, purchaseJson: String) {
//        val param = SubscriptionRequest(
//                Constants.DEVICE_TYPE_ANDROID,
//                sku,
//                purchaseJson
//        )
//        presenter?.apiSendSub(param)
    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        when (pResponse) {
            is SubscriptionSuccessResponse -> {
                /*pResponse.result?.isActive?.let {
                    mActivity!!.showToast(pResponse.msg ?: "Subscribed successfully")
                    val data = JDAApplication.mInstance.getProfile()
                    data?.result?.isSubscribed = it
                    JDAApplication.mInstance.setProfile(data)
                    JDAApplication.mInstance.setLoginStatus(data?.result?.status)
                    (mActivity as DashboardActivity).onBackPressed()
                } ?: mActivity?.showToast("Response is eempty")*/
            }
        }
    }

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {
        if (BuildConfig.DEBUG)
            Log.e("called", "yes")
        UserAlertUtility.showProgressDialog(R.layout.progress_dialog, mActivity!!, false)
    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {
        UserAlertUtility.hideProgressDialog()
    }

}

