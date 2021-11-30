package com.german.keyboard.app.free.inputmethod.compat

import android.view.textservice.TextInfo
import com.german.keyboard.app.free.inputmethod.annotations.UsedForTesting


object TextInfoCompatUtils {
    private val TEXT_INFO_GET_CHAR_SEQUENCE =
        CompatUtils.getMethod(TextInfo::class.java, "getCharSequence")
    private val TEXT_INFO_CONSTRUCTOR_FOR_CHAR_SEQUENCE = CompatUtils.getConstructor(
        TextInfo::class.java,
        CharSequence::class.java,
        Int::class.javaPrimitiveType,
        Int::class.javaPrimitiveType,
        Int::class.javaPrimitiveType,
        Int::class.javaPrimitiveType
    )

    @get:UsedForTesting
    val isCharSequenceSupported: Boolean
        get() = TEXT_INFO_GET_CHAR_SEQUENCE != null &&
                TEXT_INFO_CONSTRUCTOR_FOR_CHAR_SEQUENCE != null

    @kotlin.jvm.JvmStatic
    @UsedForTesting
    fun newInstance(charSequence: CharSequence, start: Int, end: Int, cookie: Int,
                    sequenceNumber: Int): TextInfo? {
        return if (TEXT_INFO_CONSTRUCTOR_FOR_CHAR_SEQUENCE != null) {
            CompatUtils.newInstance(
                TEXT_INFO_CONSTRUCTOR_FOR_CHAR_SEQUENCE,
                charSequence, start, end, cookie, sequenceNumber
            ) as TextInfo
        } else TextInfo(charSequence.subSequence(start, end).toString(), cookie,
                sequenceNumber)
    }

    @kotlin.jvm.JvmStatic
    @UsedForTesting
    fun getCharSequenceOrString(textInfo: TextInfo?): CharSequence? {
        val defaultValue: CharSequence? = textInfo?.text
        return CompatUtils.invoke(
            textInfo, defaultValue!!,
            TEXT_INFO_GET_CHAR_SEQUENCE
        ) as CharSequence
    }
}