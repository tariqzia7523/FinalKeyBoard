package io.github.otobikb.changes

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.*
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.OnCompleteListener
import com.google.android.play.core.tasks.Task
import io.github.otobikb.drawer.Advance3DDrawerLayout
import io.github.otobikb.inputmethod.latin.R
import io.github.otobikb.inputmethod.latin.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {

    var prefManager: PrefManager? = null
    var isInputDeviceEnabled = false
    var adView: AdView? = null
    var adLoader: AdLoader? = null
    var adLoaded = false
    private val MY_REQUEST_CODE = 999 // set globally

    private var appUpdateManager: AppUpdateManager? = null
    var animation: Animation? = null
    var alertView: View? = null
    val RecordAudioRequestCode = 1
    var mInterstitialAd: InterstitialAd? = null
    var count = 0
    var nativeAd: NativeAd? = null
    var isPurchesed = false
    private var mAppUpdateManager: AppUpdateManager? = null
    private val RC_APP_UPDATE = 11
    lateinit var drawerLayout:Advance3DDrawerLayout

    val THEME_KEY = "theme_key"


    fun furtherCall() {
        if (count == 1) {
            enableKeyboard_Func()
        } else if (count == 2) {
            setKeyboard_Func()
        } else if (count == 3) {
            startActivity(Intent(this@MainActivity, ThemeTabBar::class.java))
        } else if (count == 4) {
            //changeLanguage()
        } else if (count == 5) {
            startActivity(Intent(this@MainActivity, HowToUse::class.java))
        }
    }

    private fun loadIntersitialAdd() {
        if (!isPurchesed) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                this,
                AddIds.getInterstialId(),
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd
                        Log.e(ContentValues.TAG, "onAdLoaded")
                        mInterstitialAd!!.setFullScreenContentCallback(object :
                            FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.e(ContentValues.TAG, "The ad was dismissed.")
                                furtherCall()
                                loadIntersitialAdd()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when fullscreen content failed to show.
                                Log.e(ContentValues.TAG, "The ad failed to show.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null
                                Log.e(ContentValues.TAG, "The ad was shown.")
                            }
                        })
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Handle the error
                        Log.i(ContentValues.TAG, loadAdError.message)
                        mInterstitialAd = null
                    }
                })
        }
    }

    fun loadBanner() {
        val bannerContainer = findViewById<LinearLayout>(R.id.banner_container)
        if (!isPurchesed) {
            val adView = AdView(this)
            adView.adUnitId = AddIds.getBannerID()
            adView.adListener = object : AdListener() {
                override fun onAdClosed() {
                    super.onAdClosed()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    try {
                        bannerContainer.visibility = View.GONE
                    } catch (ignored: Exception) {
                    }
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
        } else {
            bannerContainer.visibility = View.GONE
        }
    }

    var installStateUpdatedListener: InstallStateUpdatedListener =
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(state: InstallState) {
                when (state.installStatus()) {
                    InstallStatus.DOWNLOADED -> {
                    }
                    InstallStatus.INSTALLED -> mAppUpdateManager?.unregisterListener(this)
                    InstallStatus.CANCELED -> {
                    }
                    InstallStatus.DOWNLOADING -> {
                    }
                    InstallStatus.FAILED -> {
                    }
                    InstallStatus.INSTALLING -> {
                    }
                    InstallStatus.PENDING -> {
                    }
                    InstallStatus.REQUIRES_UI_INTENT -> {
                    }
                    InstallStatus.UNKNOWN -> {
                    }
                    else -> Log.i(
                        ContentValues.TAG,
                        "InstallStateUpdatedListener: state: " + state.installStatus()
                    )
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        val sharedPreferences = getSharedPreferences(getString(R.string.MY_PREF), MODE_PRIVATE)
        isPurchesed = sharedPreferences.getBoolean(getString(R.string.is_purchsed), false)

        val progressDialog = ProgressDialog(this@MainActivity)
        progressDialog.setMessage(getString(R.string.loading_ad))

        if (supportActionBar != null) supportActionBar!!.hide()
        MobileAds.initialize(
            this
        ) { initializationStatus: InitializationStatus? -> }


        loadBanner()
        drawerLayout = findViewById(R.id.drawer_layout)

        setting.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        })

        val actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        drawerLayout.setViewScale(GravityCompat.START, 0.96f)
        drawerLayout.setRadius(GravityCompat.START, 20f)
        drawerLayout.setViewElevation(GravityCompat.START, 8f)
        drawerLayout.setViewRotation(GravityCompat.START, 15f)

        nav_view.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_test_keyboard -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this@MainActivity, TestActivity::class.java))
                }
                R.id.nav_enable_keyboard -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    count = 1
                    if (mInterstitialAd != null) {
                        progressDialog.show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            progressDialog.dismiss()
                            mInterstitialAd!!.show(this)
                        }, 900)
                    } else {
                        loadIntersitialAdd()
                        furtherCall()
                    }
                }
                R.id.nav_select_keyboard -> {
                   drawerLayout.closeDrawer(GravityCompat.START)
                    count = 2
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        checkPermission()
                    } else if (mInterstitialAd != null) {
                        progressDialog.show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            progressDialog.dismiss()
                            mInterstitialAd!!.show(this)
                        }, 900)
                    } else {
                        loadIntersitialAdd()
                        furtherCall()
                    }
                }
                R.id.nav_themes -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    count = 3
                    if (mInterstitialAd != null) {
                        progressDialog.show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            progressDialog.dismiss()
                            mInterstitialAd!!.show(this)
                        }, 900)
                    } else {
                        loadIntersitialAdd()
                        furtherCall()
                    }
                }
                R.id.nav_language -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    count = 4
                    if (mInterstitialAd != null) {
                        progressDialog.show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            progressDialog.dismiss()
                            mInterstitialAd!!.show(this)
                        }, 900)
                    } else {
                        loadIntersitialAdd()
                        furtherCall()
                    }
                }
                R.id.nav_rate -> {
                    rateus()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_feedback -> {
                    feedback()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_more_apps -> {
                    moreApps()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_app_policy -> {
                    startActivity(Intent(this@MainActivity, PrivacyPolicy::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_share -> {
                    share()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
            }
            true
        }

        findViewById<View>(R.id.drawerBtn).setOnClickListener { view: View? ->
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        findViewById<View>(R.id.testBtn).setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    TestActivity::class.java
                )
            )
        }
        val inflater =
            LayoutInflater.from(ContextThemeWrapper(this@MainActivity, R.style.my_dialog))
        alertView = inflater.inflate(R.layout.exit, findViewById(android.R.id.content), false)

        val wmbPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true)
        if (isFirstRun) {
            // Code to run once
            val editor = wmbPreference.edit()
            editor.putInt(THEME_KEY, 4)
            editor.commit()
        }

        // verifyPermissions();

        // verifyPermissions();
        CheckAppUpdate()
        animation = AnimationUtils.loadAnimation(applicationContext, R.anim.anim)
        prefManager = PrefManager(this)

        // check if our keyboard is enabled as input method

        // check if our keyboard is enabled as input method
        findViewById<View>(R.id.btn_EnableKeyBoard).startAnimation(animation)
        findViewById<View>(R.id.btn_changeTheme).setOnClickListener { v: View? ->
            count = 3
            if (mInterstitialAd != null) {
                progressDialog.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    mInterstitialAd!!.show(this)
                }, 900)
            } else {
                loadIntersitialAdd()
                furtherCall()
            }
        }

        findViewById<View>(R.id.btn_EnableKeyBoard).setOnClickListener { v: View? ->
            count = 1
            if (mInterstitialAd != null) {
                progressDialog.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    mInterstitialAd!!.show(this)
                }, 900)
            } else {
                loadIntersitialAdd()
                furtherCall()
            }
        }

        findViewById<View>(R.id.btn_SetKeyboard).setOnClickListener { v: View? ->
            count = 2
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                checkPermission()
            } else if (mInterstitialAd != null) {
                progressDialog.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    mInterstitialAd!!.show(this)
                }, 900)
            } else {
                loadIntersitialAdd()
                furtherCall()
            }
        }
        findViewById<View>(R.id.btn_setting).setOnClickListener { view: View? ->
            Log.e("***cgange", "clicked")
            count = 4
            if (mInterstitialAd != null) {
                progressDialog.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    mInterstitialAd!!.show(this)
                }, 900)
            } else {
                loadIntersitialAdd()
                furtherCall()
            }
        }

        findViewById<View>(R.id.how_to_use).setOnClickListener { view: View? ->
            count = 5
            if (mInterstitialAd != null) {
                progressDialog.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    mInterstitialAd!!.show(this)
                }, 900)
            } else {
                loadIntersitialAdd()
                furtherCall()
            }
        }

        findViewById<View>(R.id.premium).setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@MainActivity,
                    Premium::class.java
                )
            )
        }
        adInitializer()
        loadIntersitialAdd()

    }

    private fun moreApps() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id=Multi+Themes+Keyboard+%26+Emoji+Keyboard")
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun feedback() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "multithemeskeyboard@gmail.com", null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body")
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RecordAudioRequestCode
            )
        }
    }

    fun enableKeyboard_Func() {
        if (isInputDeviceEnabled) {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(
                parentLayout,
                getString(R.string.select_the_keyboard),
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            val enableIntent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            enableIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(enableIntent)
            prefManager!!.enabledKeyboard(true)
            check_Keyboard()
        }
    }

    fun setKeyboard_Func() {
        //main work
        if (isInputDeviceEnabled) {
            val imeManager =
                applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imeManager.showInputMethodPicker()
            findViewById<View>(R.id.btn_SetKeyboard).clearAnimation()
        } else {
            AlertDialog.Builder(this@MainActivity)
                .setTitle(R.string.title_enable_keyboard)
                .setMessage(R.string.please_first_enable_keyboard) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(
                    R.string.enable_dialog
                ) { dialog, which ->
                    val enableIntent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                    enableIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(enableIntent)
                    prefManager!!.enabledKeyboard(true)
                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string.cancel_dialog) { dialog, which -> dialog.dismiss() }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

//    fun changeLanguage() {
//        val locale: Locale = getCurrentLanguage()
//        val language = locale.language
//        if (language.endsWith("my")) setLanguage("en") else setLanguage("my")
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this@MainActivity)
        if (alertView!!.parent != null) {
            (alertView!!.parent as ViewGroup).removeView(alertView)
        }
        builder.setView(alertView)
        val btn_positive = alertView!!.findViewById<TextView>(R.id.rateBtn)
        val btn_negative = alertView!!.findViewById<TextView>(R.id.exitBtn)
        val template: TemplateView = alertView!!.findViewById(R.id.nativeTemplateView)
        if (nativeAd != null) {
            val styles =
                NativeTemplateStyle.Builder() //                    .withMainBackgroundColor(background)
                    .build()
            template.setStyles(styles)
            template.setNativeAd(nativeAd)
            adLoaded = true
            template.visibility = View.VISIBLE
        } else {
            template.visibility = View.GONE
        }
        val dialog = builder.create()
        btn_positive.setOnClickListener({ v: View? -> rateus() })
        btn_negative.setOnClickListener({ view: View? ->
            finish()
            finishAffinity()
            System.exit(0)
        })
        dialog.show()
    }

    fun share() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
            var shareMessage = getString(R.string.recomendation)
            shareMessage =
                shareMessage + "https://play.google.com/store/apps/details?id=" + packageName + "\n\n"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            //e.toString();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun rateus() {
        val uri = Uri.parse("market://details?id=" + applicationContext.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + applicationContext.packageName)
                )
            )
        }
    }

    fun check_Keyboard() {
        val packageLocal = packageName
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val list = inputMethodManager.enabledInputMethodList
        for (inputMethod: InputMethodInfo in list) {
            val packageName = inputMethod.packageName
            if ((packageName == packageLocal)) {
                isInputDeviceEnabled = true
                prefManager!!.enabledKeyboard(true)
            }
        }
        if (isInputDeviceEnabled) {
            findViewById<View>(R.id.btn_SetKeyboard).startAnimation(animation)
            findViewById<View>(R.id.btn_EnableKeyBoard).clearAnimation()
        }
    }


    override fun onStart() {
        super.onStart()
        check_Keyboard()
        mAppUpdateManager = AppUpdateManagerFactory.create(this)
        mAppUpdateManager!!.registerListener(installStateUpdatedListener)
        mAppUpdateManager!!.getAppUpdateInfo().addOnSuccessListener({ appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE /*AppUpdateType.FLEXIBLE*/
                )
            ) {
                try {
                    mAppUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE /*AppUpdateType.FLEXIBLE*/,
                        this@MainActivity,
                        RC_APP_UPDATE
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
//                popupSnackbarForCompleteUpdate();
            } else {
                Log.e(ContentValues.TAG, "checkForAppUpdateAvailability: something else")
            }
        })
        if (MySharedPref(this@MainActivity).getUserReview()) {
            val manager = ReviewManagerFactory.create(this@MainActivity)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener({ task: Task<ReviewInfo?> ->
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    val reviewInfo: ReviewInfo = task.getResult()
                    val flow: Task<Void?> =
                        manager.launchReviewFlow(this@MainActivity, reviewInfo)
                    flow.addOnCompleteListener(OnCompleteListener { task2: Task<Void?>? ->
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                        MySharedPref(this@MainActivity).setUserReviwed(true)
                    })
                } else {
                    // There was some problem, log or handle the error code.
                    task.getException()!!.printStackTrace()
                }
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.size == 0) {
            return
        }
        var allPermissionsGranted = true
        if (grantResults.size > 0) {
            for (grantResult: Int in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
        }
        if (!allPermissionsGranted) {
            var somePermissionsForeverDenied = false
            for (permission: String? in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, (permission)!!)) {
                    //denied
                    Log.e("denied", (permission))
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle(R.string.title_permission_required_dialog)
                        .setMessage(
                            (getString(R.string.you_must_allow_permission) +
                                    getString(R.string.otherwise_application_will_not_work) +
                                    getString(R.string.go_to_settings_and_allow) +
                                    getString(R.string.if_you_cancel_application_cloase))
                        )
                        .setPositiveButton(R.string.go_to_settings) { dialog, which ->
                            val intent: Intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .setNegativeButton(R.string.cancel_dialog) { dialog, which -> finish() }
                        .setCancelable(false)
                        .create()
                        .show()
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            (permission)
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //allowed
                        Log.e("allowed", (permission))
                    } else {
                        //set to never ask again
                        Log.e("set to never ask again", (permission))
                        somePermissionsForeverDenied = true
                    }
                }
            }
            if (somePermissionsForeverDenied) {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle(getString(R.string.title_permission_required_dialog))
                    .setMessage(
                        getString(R.string.you_force_fully_denied_permissions) +
                                getString(R.string.open_settings_and_allow_themall)
                    )
                    .setPositiveButton(
                        getString(R.string.go_to_settings),
                        { dialog: DialogInterface?, which: Int ->
                            val intent: Intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        })
                    .setNegativeButton(getString(R.string.cancel_dialog),
                        { dialog: DialogInterface?, which: Int -> })
                    .setCancelable(false)
                    .create()
                    .show()
            }
        } //act according to the request code used while requesting the permission(s).
    }

    private fun CheckAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager!!.getAppUpdateInfo()

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener({ appUpdateInfo: AppUpdateInfo ->
            if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
            ) {
                try {
                    appUpdateManager!!.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        AppUpdateType.IMMEDIATE,  // The current activity making the update request.
                        this@MainActivity,  // Include a request code to later monitor this update request.
                        MY_REQUEST_CODE
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun recreate() {
        finish()
        findViewById<View>(R.id.btn_EnableKeyBoard).clearAnimation()
        findViewById<View>(R.id.btn_SetKeyboard).clearAnimation()
        startActivity(
            Intent(
                this@MainActivity,
               MainActivity::class.java
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (adView != null) {
            adView!!.resume()
        }
        check_Keyboard()
        appUpdateManager!!.appUpdateInfo.addOnSuccessListener({ appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                // If an in-app update is already running, resume the update.
                try {
                    appUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this@MainActivity,
                        MY_REQUEST_CODE
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onPause() {
        if (adView != null) {
            adView!!.pause()
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (adView != null) {
            adView!!.destroy()
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                val parentLayout = findViewById<View>(android.R.id.content)
                val snackbar = Snackbar
                    .make(parentLayout, R.string.install_failed, Snackbar.LENGTH_LONG)
                snackbar.show()
            }
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

    private fun adInitializer() {
        if (!isPurchesed) {
            adLoader = AdLoader.Builder(this, AddIds.getNativeId())
                .withAdListener(object : AdListener() {
                    override fun onAdClosed() {
                        super.onAdClosed()
                        Log.e(ContentValues.TAG, "onAdClosed")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        Log.e(ContentValues.TAG, "onAdFailedToLoad " + loadAdError.message)
                        Log.e(ContentValues.TAG, "onAdFailedToLoad $loadAdError")
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        Log.e(ContentValues.TAG, "onAdOpened")
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Log.e(ContentValues.TAG, "onAdLoaded")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        Log.e(ContentValues.TAG, "onAdClicked")
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        Log.e(ContentValues.TAG, "onAdImpression")
                    }
                })
                .forNativeAd({ ad: NativeAd ->
                    nativeAd = ad
                }).build()
            val adRequest = AdRequest.Builder().build()
            adLoader!!.loadAd(adRequest)
            Log.e(ContentValues.TAG, "add is loading")
            Log.e(ContentValues.TAG, "build called")
        }
    }


    override fun onStop() {
        super.onStop()
        if (mAppUpdateManager != null) {
            mAppUpdateManager!!.unregisterListener(installStateUpdatedListener)
        }
    }
}