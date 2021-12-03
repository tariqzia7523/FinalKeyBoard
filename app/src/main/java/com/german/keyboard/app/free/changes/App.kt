package com.german.keyboard.app.free.changes

import android.content.Context
import androidx.multidex.MultiDex
import com.akexorcist.localizationactivity.ui.LocalizationApplication
import com.module.ads.AppOpenManager
import com.module.ads.MySharedPref
import java.util.*

class App : LocalizationApplication() {
    var appOpenManager: AppOpenManager? = null

    override fun onCreate() {
        super.onCreate()
        appOpenManager = AppOpenManager(this, MySharedPref(this))
    }
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun getDefaultLanguage(base: Context): Locale {
        return Locale.ENGLISH
    }
}