package com.jda.application.utils.subscription

import android.content.Context
import com.android.billingclient.api.*
import com.jda.application.base.activity.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InAppPurchase {
    private var billingClient: BillingClient? = null
    private var billingClientStateListener: BillingClientStateListener? = null
    private var context: Context? = null
    private var callback: CallBack? = null
    private var myPurchase: Purchase? = null

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        // To be implemented in a later section.
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.

            callback?.error(billingResult.debugMessage)

        } else {
            // Handle any other error codes.
            callback?.error(billingResult.debugMessage)
        }
    }

    val acknowledgePurchaseResponseListener: AcknowledgePurchaseResponseListener = AcknowledgePurchaseResponseListener {
        if (it.responseCode == BillingClient.BillingResponseCode.OK) {
            callback?.subPurchase(myPurchase!!)
        } else {
            callback?.error(it.debugMessage)
        }
    }


    fun startConnection(pContext: Context, pCallback: CallBack?) {
        context = pContext
        callback = pCallback

        billingClient = if (context != null) BillingClient.newBuilder(context!!)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build() else null

        billingClientStateListener = object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                billingClient?.startConnection(billingClientStateListener!!)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    querySkuDetails()
                } else {
                    callback?.error(billingResult.debugMessage)
                }
            }

        }

        billingClient?.startConnection(billingClientStateListener!!)
    }


    fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.add("0002_goodsub")
        skuList.add("0003_amazingsub")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        GlobalScope.launch(Dispatchers.IO) {
            billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
                // Process the result.
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    callback?.skuDetails(skuDetailsList!!)
                }
            }
        }
    }

    fun startPurchase(skuDetails: SkuDetails, activity: BaseActivity) {
        val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()
        val billingResult = billingClient?.launchBillingFlow(activity, flowParams)
        if (billingResult?.responseCode != BillingClient.BillingResponseCode.OK) {
            //startPurchase(skuDetails, activity)
            callback?.error(billingResult?.debugMessage ?: "error")
        }
    }

    // Purchase retrieved from BillingClient#queryPurchases or your PurchasesUpdatedListener.
    /* fun handlePurchase(purchase: Purchase) {

         // Verify the purchase.
         // Ensure entitlement was not already granted for this purchaseToken.
         // Grant entitlement to the user.

         val consumeParams =
                 ConsumeParams.newBuilder()
                         .setPurchaseToken(purchase.getPurchaseToken())
                         .build()

         billingClient?.consumeAsync(consumeParams, { billingResult, outToken ->
             if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                 // Handle the success of the consume operation.

             }
         })
     }*/

    fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            myPurchase = purchase
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = GlobalScope.launch(Dispatchers.IO) {
                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams.build(), acknowledgePurchaseResponseListener)
                }
            }
        }
    }


    interface CallBack {
        fun skuDetails(skuList: MutableList<SkuDetails>)
        fun subPurchase(myPurchase: Purchase)
        fun error(error: String)
    }


}