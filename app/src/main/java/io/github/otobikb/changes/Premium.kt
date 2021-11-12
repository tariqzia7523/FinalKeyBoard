package io.github.otobikb.changes

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.android.billingclient.api.*
import io.github.otobikb.inputmethod.latin.R
import java.util.ArrayList

class Premium : AppCompatActivity() {

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
                && purchases != null
            ) {
                val sharedPreferences =
                    getSharedPreferences(getString(R.string.MY_PREF), MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean(getString(R.string.is_purchsed), true).apply()
                //                 removeAllAds();
                //                for (Purchase purchase : purchases) {
                //                    handlePurchase(purchase);
                //                }
                startActivity(
                    Intent(
                        this@Premium,
                        MainActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)


        findViewById<View>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<View>(R.id.go_premium).setOnClickListener { goAddFree() }
    }


    fun removeAllAds() {}

    fun goAddFree() {
        // Log.e(TAG,"add free metyhod called");
        val billingClient = BillingClient.newBuilder(this@Premium)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.e(ContentValues.TAG, "ready to purchess")
                    val skuList: MutableList<String> = ArrayList()
                    skuList.add("premium_upgrade")
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(
                        params.build()
                    ) { billingResult, skuDetailsList -> //Log.e(TAG,"sku details "+skuDetailsList.size());
                        // Process the result.
                        //Log.e(TAG,"skuDetailsList.get(0).getTitle() "+ skuDetailsList.get(0).getTitle());
                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList!![0])
                            .build()
                        val responseCode = billingClient.launchBillingFlow(
                            this@Premium,
                            billingFlowParams
                        ).responseCode
                        Log.e(ContentValues.TAG, "responseCode $responseCode")
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                // Log.e(TAG,"service disconnected");
            }
        })
    }
}