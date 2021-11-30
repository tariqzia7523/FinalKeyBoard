package com.german.keyboard.app.free.inputmethod.compat

import android.view.inputmethod.InputConnection
import com.german.keyboard.app.free.inputmethod.compat.CompatUtils.ClassWrapper
import com.german.keyboard.app.free.inputmethod.compat.CompatUtils.ToBooleanMethodWrapper

object InputConnectionCompatUtils {
    private var sInputConnectionType: ClassWrapper? = null
    private var sRequestCursorUpdatesMethod: ToBooleanMethodWrapper? = null
    val isRequestCursorUpdatesAvailable: Boolean
        get() = sRequestCursorUpdatesMethod != null

    private const val CURSOR_UPDATE_IMMEDIATE = 1 shl 0
    private const val CURSOR_UPDATE_MONITOR = 1 shl 1
    private fun requestCursorUpdatesImpl(inputConnection: InputConnection,
                                         cursorUpdateMode: Int): Boolean {
        return if (!isRequestCursorUpdatesAvailable) {
            false
        } else sRequestCursorUpdatesMethod!!.invoke(inputConnection, cursorUpdateMode)
    }

    @kotlin.jvm.JvmStatic
    fun requestCursorUpdates(inputConnection: InputConnection,
                             enableMonitor: Boolean, requestImmediateCallback: Boolean): Boolean {
        val cursorUpdateMode = ((if (enableMonitor) CURSOR_UPDATE_MONITOR else 0)
                or if (requestImmediateCallback) CURSOR_UPDATE_IMMEDIATE else 0)
        return requestCursorUpdatesImpl(inputConnection, cursorUpdateMode)
    }

    init {
        sInputConnectionType = ClassWrapper(InputConnection::class.java)
        sRequestCursorUpdatesMethod = sInputConnectionType!!.getPrimitiveMethod(
                "requestCursorUpdates", false, Int::class.javaPrimitiveType)
    }
}