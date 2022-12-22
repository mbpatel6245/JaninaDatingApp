package com.jda.application.fragments.subscriptions

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.google.gson.Gson
import com.jda.application.R
import com.jda.application.acivities.JDAApplication
import com.jda.application.base.fragment.BaseFragment
import com.jda.application.databinding.FragmentGoldSubscriptionBinding
import com.jda.application.fragments.edit_profile.EditProfileActivity
import com.jda.application.fragments.signInModule.preferencesFragment.MutipleChoice
import com.jda.application.utils.Constants
import com.jda.application.utils.CustomDialogue
import com.jda.application.utils.UserAlertUtility
import com.jda.application.utils.UserAlertUtility.Companion.showToast
import com.jda.application.utils.subscription.BillingManager


class PlusSubscriptionFragment : BaseFragment() {

    private lateinit var binding: FragmentGoldSubscriptionBinding
    private var mSkuSelectedPosition: Int? = 0
    private lateinit var mBillingManager: BillingManager
    private var mSubscriptionPlansList: ArrayList<PlansModel> = ArrayList()
    private var presenter: SubscriptionPresenter? = null
    val arrayPrices = ArrayList<String>()
    var oldSku: SkuDetails? = null
    var oldToken: String? = ""
    var isPriceFetched: Boolean = false

    companion object {
        val TAG: String = PlusSubscriptionFragment::class.java.simpleName

        @JvmStatic
        fun newInstance() = PlusSubscriptionFragment().apply {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_gold_subscription, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBillingManager = BillingManager(requireActivity())
        presenter = SubscriptionPresenterImp(this)
        setVisibility()
        binding?.skuSpinner?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
//            mGender = (newIndex + 1).toString()
        }
        clickListeners()
    }

    override fun onResume() {
        super.onResume()
        oldSku = null
        oldToken = ""
        setUpBillingManager()
    }

    private fun setVisibility() {
        if (Constants.sIsSubscribed && JDAApplication.mInstance.getProfile()?.result?.isActive == true) {
            val profile = JDAApplication.mInstance.getProfile()
            updateUiAfterSubscription(profile?.result?.productId, profile?.result?.expiryDate)
        } else {
            binding.layoutUpdateCancel.visibility = View.GONE
            binding.tvPlanDuration.visibility = View.GONE
            binding.tvPlanExpiration.visibility = View.GONE
            binding.tvSubscribed.visibility = View.GONE
            binding.skuSpinner.visibility = View.VISIBLE
            binding.buyBtn.visibility = View.VISIBLE
        }
    }

    private fun clickListeners() {
        binding.buyBtn.setOnClickListener {
            if (oldSku != null) {
                when (mSkuSelectedPosition ?: 0) {
                    0 -> {
                        updateSubscriptionFlow(Constants.SubscriptionType.SubscriptionOneMonth.productId)
                    }
                    1 -> {
                        updateSubscriptionFlow(Constants.SubscriptionType.SubscriptionThreeMonths.productId)
                    }
                    2 -> {
                        updateSubscriptionFlow(Constants.SubscriptionType.SubscriptionSixMonths.productId)
                    }
                }
            } else {
                when (mSkuSelectedPosition ?: 0) {
                    0 -> {
                        launchSubscriptionFlow(Constants.SubscriptionType.SubscriptionOneMonth.productId)
                    }
                    1 -> {
                        launchSubscriptionFlow(Constants.SubscriptionType.SubscriptionThreeMonths.productId)
                    }
                    2 -> {
                        launchSubscriptionFlow(Constants.SubscriptionType.SubscriptionSixMonths.productId)
                    }
                }
            }
        }

        binding.skuSpinner.setOnClickListener {
            mActivity?.closeKeyBoard()
            val stringArray = resources.getStringArray(R.array.subscriptionList)
            val arrayList = ArrayList<MutipleChoice>()
            for (element in stringArray) {
                arrayList.add(MutipleChoice(element, false))
            }
            arrayList[mSkuSelectedPosition ?: 0].isSelected = true
            CustomDialogue.createDialogue(
                mActivity!!, "Select subscription period", arrayList,
                false, object : CustomDialogue.DialoguMapCallback {
                    override fun onAdapterItemClick(map: HashMap<Int, String>) {
                        if (map.size != 0) {
//                            mSkuSelectedPosition = binding.skuSpinner.selectedIndex
                            binding.skuSpinner.text = CustomDialogue.getTextFromList(map)
//                            binding.price.text = resources.getStringArray(R.array.subscriptionPrice)[mSkuSelectedPosition?:0]
                            for (item in map) {
                                if (arrayList[item.key].isSelected) {
                                    //   binding.price.text = resources.getStringArray(R.array.subscriptionPrice)[item.key]
                                    binding.price.text = arrayPrices[item.key]
                                    mSkuSelectedPosition = item.key
                                    break
                                }
                            }
                        } else {
//                        mGender = null
                            binding.skuSpinner.text = ""
                        }
                    }
                }, false
            )
            binding.skuSpinner.dismiss()
        }

        binding.cancelBtn.setOnClickListener {
            showCancelDialog("Are sure you want to cancel current subscription?")
        }

        binding.upgradeBtn.setOnClickListener {
            showUpdateDialog(
                "Are sure you want to update subscription?\n" +
                        "Note: Once you update your subscription your current subscription will get cancelled and new subscription will start."
            )
        }
    }

    private fun launchSubscriptionFlow(plan: String) {
        mSubscriptionPlansList.forEach { data ->
            if (plan == data.skuDetails?.sku) {
                mBillingManager.startPurchase(data.skuDetails)
            }
        }
    }

    private fun updateSubscriptionFlow(plan: String) {
        mSubscriptionPlansList.forEach { data ->
            if (plan == data.skuDetails?.sku) {
                mBillingManager.updatePurchase(oldToken, data.skuDetails)
            }
        }
    }

    private fun updateUiAfterSubscription(pPlanDuration: String?, pExpiryDate: String?) {
        binding.skuSpinner.visibility = View.GONE
        binding.layoutUpdateCancel.visibility = View.VISIBLE
        binding.tvPlanDuration.visibility = View.VISIBLE
        binding.tvPlanExpiration.visibility = View.VISIBLE
        binding.tvSubscribed.visibility = View.VISIBLE
        val subscription = resources.getStringArray(R.array.subscriptionList)
        val subscriptionPrice = resources.getStringArray(R.array.subscriptionPrice)
        when (pPlanDuration) {
            Constants.SubscriptionType.SubscriptionOneMonth.productId -> {
                binding.tvPlanDuration.text = subscription[0]
                binding.price.text = subscriptionPrice[0]
            }
            Constants.SubscriptionType.SubscriptionThreeMonths.productId -> {
                binding.tvPlanDuration.text = subscription[1]
                binding.price.text = subscriptionPrice[1]
            }
            Constants.SubscriptionType.SubscriptionSixMonths.productId -> {
                binding.tvPlanDuration.text = subscription[2]
                binding.price.text = subscriptionPrice[2]
            }
        }
        binding.tvPlanExpiration.text =
            String.format(getString(R.string.expiration_date), pExpiryDate?.substring(0, 10))
        binding.buyBtn.visibility = View.GONE
    }

    private fun setUpBillingManager() {
        mBillingManager.startConnection(object : BillingManager.BillingClientListener {
            override fun isBillingClientReady(isReady: Boolean) {
                if (isReady) {
                    mBillingManager.queryPurchase(object :
                        BillingManager.SubscriptionPurchaseListener {
                        override fun queryPurchaseResponse(purchase: Purchase?) {
                            if (purchase != null) {
                                //Already Subscribed
                                Log.i(
                                    "TAG_SUBSCRIPTION",
                                    "BillingManager:" + "queryPurchaseResponse Success ${
                                        Gson().toJson(purchase)
                                    }--${purchase.purchaseToken}"
                                )
                                mSubscriptionPlansList.forEach { data ->
                                    if (purchase.skus[0] == data.skuDetails?.sku) {
                                        if (Constants.sIsSubscribed && JDAApplication.mInstance.getProfile()?.result?.isActive == true) {
                                            oldSku = data.skuDetails!!
                                            oldToken = purchase.purchaseToken
                                            binding.price.text = oldSku!!.price
                                        }
                                        Log.e("TAG_SUBSCRIPTION", oldSku!!.sku)
                                    }
                                }
                            }
                        }

                        override fun purchaseResponse(purchase: Purchase?) {
                            if (purchase?.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                Log.i("TAG_SUBSCRIPTION", "Subscription purchased success response")
                                var productId: String? = null
                                purchase.skus.forEach {
                                    productId = it.toString()
                                }
                                if (productId != null) {
                                    apiSendSub(productId!!, purchase.purchaseToken)
                                }
                            }
                        }

                        override fun purchaseError(pMessage: String?) {
                            Log.i(
                                "TAG_SUBSCRIPTION",
                                "BillingManager: purchaseError Error $pMessage"
                            )
                            if (pMessage == "No Subscription" && !Constants.sIsSubscribed) {
                                setVisibility()
                            }
                        }

                        override fun skuDetailsSubs(skuList: MutableList<SkuDetails>?) {
                            if (!skuList.isNullOrEmpty()) {
                                mSubscriptionPlansList.clear()
                                for (data in skuList) {
                                    mSubscriptionPlansList.add(
                                        PlansModel(
                                            data.sku,
                                            data.title,
                                            data.description,
                                            data.subscriptionPeriod,
                                            data.price,
                                            data
                                        )
                                    )
                                }
                                if (!isPriceFetched)
                                    fetchPricesFromStore()
                            }
                        }
                    })
                    mBillingManager.queryAvailableProducts()
                } else {
                    Log.i("TAG_SUBSCRIPTION", "BillingManager: getQueryPurchase isReady : $isReady")
                }
            }

            override fun error(error: String) {
                Log.i("TAG_SUBSCRIPTION", "BillingManager: getQueryPurchase isReady : $error")
            }

        })
    }

    private fun apiSendSub(productId: String, purchaseToken: String) {
        val param = SubscriptionRequest(
            productId,
            purchaseToken,
            Constants.sPlatform
        )
        presenter?.apiSendSub(param)
    }

    override fun onSuccess(pResponse: Any?, pIsPaginatedCall: Boolean?) {
        when (pResponse) {
            is SubscriptionSuccessResponse -> {
                pResponse?.result?.let {
                    activity?.showToast("Subscribed successfully")
                    val data = JDAApplication.mInstance.getProfile()
                    data?.result?.isActive = it.isActive
                    data?.result?.productId = it.productId
                    data?.result?.expiryDate = it.expiryDate
                    JDAApplication.mInstance.setProfile(data)
                    Constants.sIsSubscribed = it.isActive
//                    Constants.sIsSubscribed = true
                    updateUiAfterSubscription(it.productId, it.expiryDate)
                    if (JDAApplication.mInstance.getProfile()?.result?.areQuestionsSet == false) {
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
                }
            }
        }
    }

    override fun showProgress(tClass: Class<*>, pIsPaginatedCall: Boolean) {
        when (tClass) {
            SubscriptionSuccessResponse::class.java -> {
                UserAlertUtility.showProgressDialog(R.layout.progress_dialog, activity, false)
            }
        }
    }

    override fun hideProgress(tClass: Class<*>, isResult: Boolean, pIsPaginatedCall: Boolean) {
        when (tClass) {
            SubscriptionSuccessResponse::class.java -> {
                UserAlertUtility.hideProgressDialog()
            }
        }
    }

    override fun onError(pErrorMessage: String?, pType: Constants.ErrorType, pErrorCode: Int?) {
        super.onError(pErrorMessage, pType, pErrorCode)
    }

    private fun showCancelDialog(msg: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Alert!")
        builder.setMessage(msg)
            .setNegativeButton(
                "Yes"
            ) { dialog: DialogInterface?, which: Int ->
                dialog?.dismiss()
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/account/subscriptions")
                        )
                    );
                } catch (e: ActivityNotFoundException) {
                    showToast("Cant open the browser", requireActivity())
                    e.printStackTrace()
                }

            }.setPositiveButton(
                "No"
            ) { dialog: DialogInterface?, which: Int ->
                dialog?.dismiss()
            }.create().show()
    }

    private fun showUpdateDialog(msg: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Alert!")
        builder.setMessage(msg)
            .setNegativeButton(
                "Yes"
            ) { dialog: DialogInterface?, which: Int ->
                dialog?.dismiss()
                binding.layoutUpdateCancel.visibility = View.GONE
                binding.skuSpinner.visibility = View.VISIBLE
                binding.buyBtn.visibility = View.VISIBLE
                binding.price.text = mSubscriptionPlansList[mSkuSelectedPosition!!].price
            }.setPositiveButton(
                "No"
            ) { dialog: DialogInterface?, which: Int ->
                dialog?.dismiss()
            }.create().show()
    }

    private fun fetchPricesFromStore() {
        isPriceFetched = true
        arrayPrices.clear()
        mSubscriptionPlansList.forEach { data ->
            val skuclass = data.skuDetails
            val gson = Gson()
            val jsonString = gson.toJson(skuclass)
            if (Constants.SubscriptionType.SubscriptionOneMonth.productId == data.skuDetails?.sku) {
                val price = gson.fromJson(jsonString, SkuDetails::class.java).price
                arrayPrices.add(price)
                if (!Constants.sIsSubscribed) {
                    binding.price.text = price
                }
                /*if (oldSku != null) {
                    binding.price.text = oldSku!!.price
                } else {
                    binding.price.text = price
                }*/
            }
            if (Constants.SubscriptionType.SubscriptionThreeMonths.productId == data.skuDetails?.sku) {
                val price = gson.fromJson(jsonString, SkuDetails::class.java).price
                arrayPrices.add(price)
            }
            if (Constants.SubscriptionType.SubscriptionSixMonths.productId == data.skuDetails?.sku) {
                val price = gson.fromJson(jsonString, SkuDetails::class.java).price
                arrayPrices.add(price)
            }
        }
    }
}