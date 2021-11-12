package io.github.otobikb.inputmethod.compat

import android.os.Build
import android.os.Build.VERSION_CODES
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodSubtype
import io.github.otobikb.inputmethod.annotations.UsedForTesting
import io.github.otobikb.inputmethod.latin.RichInputMethodSubtype
import io.github.otobikb.inputmethod.latin.common.Constants
import io.github.otobikb.inputmethod.latin.common.LocaleUtils
import java.util.*

object InputMethodSubtypeCompatUtils {
    // Note that InputMethodSubtype.getLanguageTag() is expected to be available in Android N+.
    private val GET_LANGUAGE_TAG = CompatUtils.getMethod(InputMethodSubtype::class.java, "getLanguageTag")

    @kotlin.jvm.JvmStatic
    fun getLocaleObject(subtype: InputMethodSubtype): Locale { // Locale.forLanguageTag() is available only in Android L and later.
        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val languageTag = CompatUtils.invoke(subtype, null, GET_LANGUAGE_TAG) as String?
            if (!TextUtils.isEmpty(languageTag)) {
                return Locale.forLanguageTag(languageTag)
            }
        }
        return LocaleUtils.constructLocaleFromString(subtype.locale)
    }

}