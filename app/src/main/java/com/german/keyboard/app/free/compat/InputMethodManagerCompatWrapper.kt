package com.german.keyboard.app.free.inputmethod.compat

import android.content.Context
import android.os.IBinder
import android.view.inputmethod.InputMethodManager

class InputMethodManagerCompatWrapper(context: Context) {
    @kotlin.jvm.JvmField
    val mImm: InputMethodManager
    fun switchToNextInputMethod(token: IBinder?, onlyCurrentIme: Boolean): Boolean {
        return CompatUtils.invoke(
            mImm, false /* defaultValue */,
            METHOD_switchToNextInputMethod, token, onlyCurrentIme
        ) as Boolean
    }

    fun shouldOfferSwitchingToNextInputMethod(token: IBinder?): Boolean {
        return CompatUtils.invoke(
            mImm, false /* defaultValue */,
            METHOD_shouldOfferSwitchingToNextInputMethod, token
        ) as Boolean
    }

    companion object {
        private val METHOD_switchToNextInputMethod = CompatUtils.getMethod(
            InputMethodManager::class.java,
            "switchToNextInputMethod",
            IBinder::class.java,
            Boolean::class.javaPrimitiveType
        )
        private val METHOD_shouldOfferSwitchingToNextInputMethod = CompatUtils.getMethod(
            InputMethodManager::class.java,
            "shouldOfferSwitchingToNextInputMethod", IBinder::class.java
        )
    }

    init {
        mImm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
}