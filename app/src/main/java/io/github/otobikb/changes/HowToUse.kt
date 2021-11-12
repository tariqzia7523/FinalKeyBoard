package io.github.otobikb.changes

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import io.github.otobikb.inputmethod.latin.R

class HowToUse : AppCompatActivity() {


    var adLoader: AdLoader? = null
    var TAG = "***HOW"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        findViewById<View>(R.id.backBtn).setOnClickListener { finish() }
        adInitializer()
    }


    private fun adInitializer() {
        val sharedPreferences = getSharedPreferences(getString(R.string.MY_PREF), MODE_PRIVATE)
        val isPurchesed = sharedPreferences.getBoolean(getString(R.string.is_purchsed), false)
        if (!isPurchesed) {
            adLoader = AdLoader.Builder(this, AddIds.getNativeId())
                .withAdListener(object : AdListener() {
                    override fun onAdClosed() {
                        super.onAdClosed()
                        Log.e(TAG, "onAdClosed")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        Log.e(TAG, "onAdFailedToLoad " + loadAdError.message)
                        Log.e(TAG, "onAdFailedToLoad $loadAdError")
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        Log.e(TAG, "onAdOpened")
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Log.e(TAG, "onAdLoaded")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        Log.e(TAG, "onAdClicked")
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        Log.e(TAG, "onAdImpression")
                    }
                })
                .forNativeAd(object : NativeAd.OnNativeAdLoadedListener {
                    private val background: ColorDrawable? = null
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        // Assumes you have a placeholder FrameLayout in your View layout
                        // (with id fl_adplaceholder) where the ad is to be placed.
                        val styles =
                            NativeTemplateStyle.Builder().withMainBackgroundColor(background)
                                .build()
                        val template = findViewById<TemplateView>(R.id.nativeTemplateView)
                        template.setStyles(styles)
                        template.setNativeAd(nativeAd)
                        template.visibility = View.VISIBLE
                        // findViewById(R.id.temp_add_text).setVisibility(View.GONE);
                    }
                }).build()
            val adRequest = AdRequest.Builder().build()
            adLoader!!.loadAd(adRequest)
            Log.e(TAG, "add is loading")
            Log.e(TAG, "build called")
        }
    }
}