package com.german.keyboard.app.free.changes

import android.Manifest
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import android.view.animation.Animation
import com.infideap.drawerbehavior.Advance3DDrawerLayout
import com.google.android.material.navigation.NavigationView
import com.module.ads.AddInitilizer
import android.content.Intent
import com.german.keyboard.app.free.inputmethod.changes.ThemeActivity
import android.annotation.SuppressLint
import androidx.annotation.RequiresApi
import android.os.Build
import android.os.Bundle
import com.german.keyboard.app.free.R
import androidx.core.content.ContextCompat
import android.app.ProgressDialog
import androidx.core.view.GravityCompat
import android.content.pm.PackageManager
import com.german.keyboard.app.free.language.settings.SettingsActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.module.ads.MySharedPref
import com.german.keyboard.app.free.language.utils.UncachedInputMethodManagerUtils
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import android.preference.PreferenceManager
import android.widget.TextView
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.install.model.AppUpdateType
import android.content.IntentSender.SendIntentException
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap

class Main_Activity : LocalizationActivity() {
    var prefManager: PrefManager? = null
    var isInputDeviceEnabled = false
    private val MY_REQUEST_CODE = 999
    private var appUpdateManager: AppUpdateManager? = null
    var animation: Animation? = null
    var alertView: View? = null
    var count = 0
    var isPurchesed = false
    lateinit var drawerLayout: Advance3DDrawerLayout
    lateinit var navView: NavigationView
    private var mImm: InputMethodManager? = null
    var addInitilizer: AddInitilizer? = null
    var testActivityAddsFlag = false
    fun furtherCall() {
        if (count == 1) {
            enableKeyboard_Func()
        } else if (count == 2) {
            setKeyboard_Func()
        } else if (count == 3) {
            startActivity(Intent(this@Main_Activity, ThemeActivity::class.java))
        } else if (count == 4) {
            changeLanguage()
        } else if (count == 5) {
            startActivity(Intent(this@Main_Activity, TestActivity::class.java))
        }
    }

    @SuppressLint("MissingSuperCall")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkAndroidVersion()
        addInitilizer = AddInitilizer(applicationContext, this) { furtherCall() }
        addInitilizer!!.loadBanner(findViewById(R.id.banner_container))
        addInitilizer!!.adInitializer(null, null, null)
        mImm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }
        val sharedPreferences = getSharedPreferences(getString(R.string.MY_PREF), MODE_PRIVATE)
        isPurchesed = sharedPreferences.getBoolean(getString(R.string.is_purchsed), false)
        val progressDialog = ProgressDialog(this@Main_Activity)
        progressDialog.setMessage(getString(R.string.loading_ad))
        if (supportActionBar != null) supportActionBar!!.hide()
        val actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        drawerLayout.setViewScale(GravityCompat.START, 0.96f)
        drawerLayout.setRadius(GravityCompat.START, 20f)
        drawerLayout.setViewElevation(GravityCompat.START, 8f)
        drawerLayout.setViewRotation(GravityCompat.START, 15f)
        navView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_test_keyboard -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    count = 5
                    testActivityAddsFlag = !testActivityAddsFlag
                    if (testActivityAddsFlag) {
                        if (!addInitilizer!!.showInterstailAdd()) startActivity(
                            Intent(
                                this@Main_Activity,
                                TestActivity::class.java
                            )
                        )
                    } else {
                        startActivity(Intent(this@Main_Activity, TestActivity::class.java))
                    }
                }
                R.id.nav_enable_keyboard -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    count = 1
                    if (!addInitilizer!!.showInterstailAdd()) furtherCall()
                }
                R.id.nav_select_keyboard -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    count = 2
                    if (ContextCompat.checkSelfPermission(
                            this@Main_Activity,
                            Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        checkPermission()
                    } else {
                        if (!addInitilizer!!.showInterstailAdd()) furtherCall()
                    }
                }
                R.id.nav_themes -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    count = 3
                    if (!addInitilizer!!.showInterstailAdd()) furtherCall()
                }
                R.id.nav_language -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    count = 4
                    furtherCall()
                }
                R.id.nav_rate -> {
                    rateus()
                    drawerLayout.closeDrawer(GravityCompat.START)

                }
                R.id.nav_feedback -> {
                    feedback()
                    drawerLayout.closeDrawer(GravityCompat.START)

                }
                R.id.nav_more_apps -> {
                    moreApps()
                    drawerLayout.closeDrawer(GravityCompat.START)

                }
                R.id.nav_app_policy -> {
                    startActivity(Intent(this@Main_Activity, PrivacyPolicy::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)

                }
                R.id.nav_share -> {
                    share()
                    drawerLayout.closeDrawer(GravityCompat.START)

                }
                R.id.nav_change_language -> {
                    invokeSubtypeEnablerOfThisIme()
                    drawerLayout.closeDrawer(GravityCompat.START)

                }
            }
            true
        })
        findViewById<View>(R.id.drawerBtn).setOnClickListener { view: View? ->
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        findViewById<View>(R.id.testBtn).setOnClickListener { view: View? ->
            count = 5
            testActivityAddsFlag = !testActivityAddsFlag
            if (testActivityAddsFlag) {
                if (!addInitilizer!!.showInterstailAdd()) startActivity(
                    Intent(
                        this@Main_Activity,
                        TestActivity::class.java
                    )
                )
            } else {
                startActivity(Intent(this@Main_Activity, TestActivity::class.java))
            }
        }
        findViewById<View>(R.id.setting).setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@Main_Activity,
                    SettingsActivity::class.java
                )
            )
        }
        val inflater =
            LayoutInflater.from(ContextThemeWrapper(this@Main_Activity, R.style.my_dialog))
        alertView = inflater.inflate(R.layout.exit, findViewById(android.R.id.content), false)
        CheckAppUpdate()
        animation = AnimationUtils.loadAnimation(applicationContext, R.anim.anim)
        prefManager = PrefManager(this)
        findViewById<View>(R.id.btn_changeTheme).setOnClickListener { v: View? ->
            count = 3
            if (!addInitilizer!!.showInterstailAdd()) furtherCall()
        }
        findViewById<View>(R.id.btn_SetKeyboard).setOnClickListener { v: View? ->
            count = 2
            if (ContextCompat.checkSelfPermission(
                    this@Main_Activity,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                checkPermission()
            } else if (!addInitilizer!!.showInterstailAdd()) furtherCall()
        }
        findViewById<View>(R.id.btn_setting).setOnClickListener { view: View? ->
            Log.e("***cgange", "clicked")
            count = 4
            furtherCall()
        }
        if (MySharedPref(this).isPurshed) {
            findViewById<View>(R.id.premium).visibility = View.GONE
        } else {
            findViewById<View>(R.id.premium).setOnClickListener { view: View? ->
                startActivity(
                    Intent(this@Main_Activity, Premium::class.java)
                )
            }
        }
    }

    fun invokeSubtypeEnablerOfThisIme() {
        val imi = UncachedInputMethodManagerUtils.getInputMethodInfoOf(packageName, mImm) ?: return
        val intent = Intent()
        intent.action = Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra(Settings.EXTRA_INPUT_METHOD_ID, imi.id)
        startActivity(intent)
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
            val imi =
                UncachedInputMethodManagerUtils.getInputMethodInfoOf(packageName, mImm) ?: return
            val enableIntent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            enableIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(enableIntent)
            prefManager!!.enabledKeyboard(true)
            check_Keyboard()
        }
    }

    fun setKeyboard_Func() {
        if (isInputDeviceEnabled) {
            val imeManager =
                applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imeManager.showInputMethodPicker()
        } else {
            val imeManager =
                applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imeManager.showInputMethodPicker()
        }
    }

    fun changeLanguage() {
        val locale = getCurrentLanguage()
        val language = locale.language
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (language.endsWith("de")) {
            com.german.keyboard.app.free.language.settings.Settings.setSelectedLaguage(pref, "en")
            setLanguage("en")
        } else {
            com.german.keyboard.app.free.language.settings.Settings.setSelectedLaguage(pref, "de")
            setLanguage("de")
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this@Main_Activity)
        if (alertView!!.parent != null) {
            (alertView!!.parent as ViewGroup).removeView(alertView)
        }
        builder.setView(alertView)
        val btn_positive = alertView!!.findViewById<TextView>(R.id.rateBtn)
        val btn_negative = alertView!!.findViewById<TextView>(R.id.exitBtn)
        val dialog = builder.create()
        btn_positive.setOnClickListener { v: View? -> rateus() }
        btn_negative.setOnClickListener { view: View? -> finishAffinity() }
        addInitilizer!!.setnativeAddOnView(alertView!!.findViewById(R.id.alertNativeAd))
        dialog.show()
    }

    fun share() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
            var shareMessage = getString(R.string.recomendation)
            shareMessage = """
                ${shareMessage}https://play.google.com/store/apps/details?id=$packageName
                
                
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
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
        for (inputMethod in list) {
            val packageName = inputMethod.packageName
            if (packageName == packageLocal) {
                isInputDeviceEnabled = true
                prefManager!!.enabledKeyboard(true)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("in fragment on request", "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> = HashMap()
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }
                    if (perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(
                            "in fragment on request",
                            "CAMERA & WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE permission granted"
                        )
                    } else {
                        Log.d(
                            "in fragment on request",
                            "Some permissions are not granted ask again "
                        )
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.CAMERA
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            showDialogOK(
                                "Camera and Storage Permission required for this app"
                            ) { dialog, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Go to settings and enable permissions",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                }
            }
        }
        if (permissions.size == 0) {
            return
        }
        var allPermissionsGranted = true
        if (grantResults.size > 0) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
        } else if (!allPermissionsGranted) {
            var somePermissionsForeverDenied = false
            for (permission in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    //denied
                    Log.e("denied", permission)
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle(R.string.title_permission_required_dialog)
                        .setMessage(
                            getString(R.string.you_must_allow_permission) +
                                    getString(R.string.otherwise_application_will_not_work) +
                                    getString(R.string.go_to_settings_and_allow) +
                                    getString(R.string.if_you_cancel_application_cloase)
                        )
                        .setPositiveButton(R.string.go_to_settings) { dialog: DialogInterface?, which: Int ->
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .setNegativeButton(R.string.cancel_dialog) { dialog: DialogInterface?, which: Int -> finish() }
                        .setCancelable(false)
                        .create()
                        .show()
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            permission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //allowed
                        Log.e("allowed", permission)
                    } else {
                        //set to never ask again
                        Log.e("set to never ask again", permission)
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
                    .setPositiveButton(getString(R.string.go_to_settings)) { dialog: DialogInterface?, which: Int ->
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null)
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    .setNegativeButton(getString(R.string.cancel_dialog)) { dialog: DialogInterface?, which: Int -> }
                    .setCancelable(false)
                    .create()
                    .show()
            }
        }
    }

    private fun CheckAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    appUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this@Main_Activity,
                        MY_REQUEST_CODE
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun recreate() {
        finish()
        //findViewById(R.id.btn_EnableKeyBoard).clearAnimation();
        findViewById<View>(R.id.btn_SetKeyboard).clearAnimation()
        startActivity(Intent(this@Main_Activity, Main_Activity::class.java))
    }

    private fun checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions()
        } else {
            // code for lollipop and pre-lollipop devices
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        val wtite =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val read =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(PREF_KEY_FIRST_START, false)
                    .apply()
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(PREF_KEY_FIRST_START, true)
                    .apply()
                finish()
            }
        } else if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                val parentLayout = findViewById<View>(android.R.id.content)
                val snackbar = Snackbar
                    .make(parentLayout, R.string.install_failed, Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        }
    }

    companion object {
        const val RecordAudioRequestCode = 1
        const val PREF_KEY_FIRST_START = "com.german.keyboard.app.free.changes.PREF_KEY_FIRST_START"
        const val REQUEST_CODE_INTRO = 1
        const val EXTRA_ENTRY_KEY = "entry"
        const val EXTRA_ENTRY_VALUE_APP_ICON = "app_icon"
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 7
    }
}