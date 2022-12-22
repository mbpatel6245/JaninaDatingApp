package com.jda.application.utils.subscription

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import com.jda.application.utils.Constants


class BillingManager(var context: Activity) : PurchasesUpdatedListener {
    private var billingClient: BillingClient? = null
    private var callback: SubscriptionPurchaseListener? = null
    private var billingClientStateListener: BillingClientStateListener? = null
    private var myPurchase: Purchase? = null

    init {
        billingClient = BillingClient.newBuilder(context.applicationContext)
            .setListener(this)
            .enablePendingPurchases()
            .build()
    }

    fun startConnection(pListener: BillingClientListener?) {

        billingClientStateListener = object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.i("TAG_SUBSCRIPTION", "BillingManager: onBillingServiceDisconnected")
                billingClientStateListener?.let { billingClient?.startConnection(it) }
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.i("TAG_SUBSCRIPTION", "BillingManager: onBillingSetupFinished")
                    pListener?.isBillingClientReady(billingClient?.isReady ?: false)
                } else {
                    Log.i("TAG_SUBSCRIPTION", "BillingManager: onBillingSetupFinished onError")
                    pListener?.error(billingResult.debugMessage)
                }
            }
        }

        billingClient?.startConnection(billingClientStateListener!!)
    }

    fun startPurchase(skuDetails: SkuDetails?) {
        skuDetails?.let {
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()
            billingClient?.launchBillingFlow(context, billingFlowParams)?.responseCode
        } ?: callback?.purchaseError("Start Purchase Failed")

    }

    fun updatePurchase(oldToken: String?, newSkuDetails: SkuDetails?) {
        newSkuDetails?.let {
            val flowParams = BillingFlowParams.newBuilder()
                .setSubscriptionUpdateParams(
                    BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                        .setOldPurchaseToken(oldToken!!)
                        .setReplaceProrationMode(BillingFlowParams.ProrationMode.IMMEDIATE_AND_CHARGE_FULL_PRICE)
                        .build() // <-- MISSING BUILD
                )
                .setSkuDetails(newSkuDetails!!)
                .build()
            billingClient?.launchBillingFlow(context, flowParams)?.responseCode
        } ?: callback?.purchaseError("Update Purchase Failed")
    }

    fun queryPurchase(listener: SubscriptionPurchaseListener) {
        this.callback = listener
        billingClient?.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, list ->
            if (list != null && list!!.size > 0) {
                val purchaseData = list!![0]
                val data =
                    ("PurchaseToken: " + purchaseData.purchaseToken + " PurchaseTime: " + purchaseData.purchaseTime
                            + " Json: " + purchaseData.originalJson + " productId: " + purchaseData.skus)
                Log.i("TAG_SUBSCRIPTION", "BillingManager: getQueryPurchase: $data")
                var productId: String? = null
                purchaseData?.skus?.forEach {
                    productId = it.toString()
                }
//                setSubscription(productId ?: "")
                callback?.queryPurchaseResponse(purchaseData)
            } else {
                Constants.sSubscription = Constants.SubscriptionType.NoSubscription
//                Constants.sIsSubscribed = false
                Constants.sIsSubscribed = false
                callback?.purchaseError("No Subscription")
            }
        }
    }

//    private fun setSubscription(productId: String) {
//        when (productId) {
//            Constants.SubscriptionType.SubscriptionOneMonth.productId -> {
//                Constants.sIsSubscribed = true
//                Constants.sSubscription = Constants.SubscriptionType.SubscriptionOneMonth
//            }
//            Constants.SubscriptionType.SubscriptionThreeMonths.productId -> {
//                Constants.sIsSubscribed = true
//                Constants.sSubscription = Constants.SubscriptionType.SubscriptionThreeMonths
//            }
//            Constants.SubscriptionType.SubscriptionSixMonths.productId -> {
//                Constants.sIsSubscribed = true
//                Constants.sSubscription = Constants.SubscriptionType.SubscriptionSixMonths
//            }
//            else -> {
//                Constants.sIsSubscribed = false
//                Constants.sSubscription = Constants.SubscriptionType.NoSubscription
//            }
//        }
//    }

    fun queryAvailableProducts() {
//        this.callback = listener
        val skuList = ArrayList<String>()
        skuList.add(Constants.SubscriptionType.SubscriptionOneMonth.productId)
        skuList.add(Constants.SubscriptionType.SubscriptionThreeMonths.productId)
        skuList.add(Constants.SubscriptionType.SubscriptionSixMonths.productId)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {
                for (skuDetails in skuDetailsList) {
                    Log.i("TAG_SUBSCRIPTION", "skuDetailsList : ${skuDetailsList}")
                    //This list should contain the products added above
                    /* skuDetails?.let {
                         this.skuDetails = it
                     }*/
                }
                callback?.skuDetailsSubs(skuDetailsList)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        Log.i("TAG_SUBSCRIPTION", "handlePurchase : ${purchase}")
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build()
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    val billingResponseCode = billingResult.responseCode
                    val billingDebugMessage = billingResult.debugMessage
                    Log.i("TAG_SUBSCRIPTION", "response code: $billingResponseCode")
                    Log.i("TAG_SUBSCRIPTION", "debugMessage : $billingDebugMessage")
                }
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
                callback?.purchaseResponse(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            callback?.purchaseError(billingResult.debugMessage)
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
            callback?.purchaseError(billingResult.debugMessage)
        }
    }


    interface BillingClientListener {
        fun isBillingClientReady(isReady: Boolean)
        fun error(error: String)
    }

    interface SubscriptionPurchaseListener {
        fun queryPurchaseResponse(purchase: Purchase?)
        fun purchaseResponse(purchase: Purchase?)
        fun purchaseError(pMessage: String?)
        fun skuDetailsSubs(skuList: MutableList<SkuDetails>?)
    }
}
