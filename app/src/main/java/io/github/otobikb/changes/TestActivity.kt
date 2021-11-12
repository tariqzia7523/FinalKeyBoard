package io.github.otobikb.changes
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.*
import io.github.otobikb.inputmethod.latin.R
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }
       backBtn.setOnClickListener { view -> super@TestActivity.onBackPressed() }
        loadBanner()
    }

    fun loadBanner() {
        val sharedPreferences = getSharedPreferences(getString(R.string.MY_PREF), MODE_PRIVATE)
        val isPurchesed = sharedPreferences.getBoolean(getString(R.string.is_purchsed), false)
        if (!isPurchesed) {
            val bannerContainer = findViewById<LinearLayout>(R.id.banner_container)
            val adView = AdView(this)
            adView.adUnitId = AddIds.getBannerID()
            adView.adListener = object : AdListener() {
                override fun onAdClosed() {
                    super.onAdClosed()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    bannerContainer.visibility = View.GONE
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }
            }
            val adRequest = AdRequest.Builder().build()
            adView.adSize = getAdSize()
            adView.loadAd(adRequest)
            bannerContainer.addView(adView)
        }
    }

    private fun getAdSize(): AdSize? {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }
}