package com.german.keyboard.app.free.changes

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*


class ChangeLanguage @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1) constructor(
    context: Context,
    languageCode: String?
) {
    init {
        val res = context.resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(Locale(languageCode))
        res.updateConfiguration(conf, dm)
    }
}