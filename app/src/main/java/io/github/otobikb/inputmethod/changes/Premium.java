package io.github.otobikb.inputmethod.changes;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import io.github.otobikb.inputmethod.latin.R;

public class Premium extends LocalizationActivity {

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.MY_PREF),MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.is_purchsed),true).apply();
//                 removeAllAds();
//                for (Purchase purchase : purchases) {
//                    handlePurchase(purchase);
//                }
                startActivity(new Intent(Premium.this,Main_Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.go_premium).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAddFree();
            }
        });

    }


    public void goAddFree() {
        // Log.e(TAG,"add free metyhod called");
        BillingClient billingClient = BillingClient.newBuilder(Premium.this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.e(TAG,"ready to purchess");
                    List<String> skuList = new ArrayList<>();
                    skuList.add("premium_upgrade");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    //Log.e(TAG,"sku details "+skuDetailsList.size());
                                    // Process the result.
                                    //Log.e(TAG,"skuDetailsList.get(0).getTitle() "+ skuDetailsList.get(0).getTitle());
                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetailsList.get(0))
                                            .build();
                                    int responseCode = billingClient.launchBillingFlow(Premium.this, billingFlowParams).getResponseCode();
                                     Log.e(TAG,"responseCode "+responseCode);
                                }
                            });
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                // Log.e(TAG,"service disconnected");
            }
        });

    }
}