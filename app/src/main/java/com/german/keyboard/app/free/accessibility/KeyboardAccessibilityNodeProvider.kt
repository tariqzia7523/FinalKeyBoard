package com.german.keyboard.app.free.inputmethod.accessibility

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityEventCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeProviderCompat
import com.german.keyboard.app.free.keyboard.Key
import com.german.keyboard.app.free.keyboard.Keyboard
import com.german.keyboard.app.free.keyboard.KeyboardView
import com.german.keyboard.app.free.latin.common.CoordinateUtils
import com.german.keyboard.app.free.latin.settings.Settings

class KeyboardAccessibilityNodeProvider<KV : KeyboardView?>(keyboardView: KV,
                                                            delegate: KeyboardAccessibilityDelegate<KV>
) : AccessibilityNodeProviderCompat() {
    private val mKeyCodeDescriptionMapper: KeyCodeDescriptionMapper
    private val mAccessibilityUtils: AccessibilityUtils
    /** Temporary rect used to calculate in-screen bounds.  */
    private val mTempBoundsInScreen = Rect()
    /** The parent view's cached on-screen location.  */
    private val mParentLocation = CoordinateUtils.newInstance()
    /** The virtual view identifier for the focused node.  */
    private var mAccessibilityFocusedView = UNDEFINED
    /** The virtual view identifier for the hovering node.  */
    private var mHoveringNodeId = UNDEFINED
    /** The keyboard view to provide an accessibility node info.  */
    private val mKeyboardView: KV
    /** The accessibility delegate.  */
    private val mDelegate: KeyboardAccessibilityDelegate<KV>
    /** The current keyboard.  */
    private var mKeyboard: Keyboard? = null

    /**
     * Sets the keyboard represented by this node provider.
     *
     * @param keyboard The keyboard that is being set to the keyboard view.
     */
    fun setKeyboard(keyboard: Keyboard?) {
        mKeyboard = keyboard
    }

    private fun getKeyOf(virtualViewId: Int): Key? {
        if (mKeyboard == null) {
            return null
        }
        val sortedKeys = mKeyboard!!.sortedKeys
        // Use a virtual view id as an index of the sorted keys list.
        return if (virtualViewId >= 0 && virtualViewId < sortedKeys.size) {
            sortedKeys[virtualViewId]
        } else null
    }

    private fun getVirtualViewIdOf(key: Key): Int {
        if (mKeyboard == null) {
            return View.NO_ID
        }
        val sortedKeys = mKeyboard!!.sortedKeys
        val size = sortedKeys.size
        for (index in 0 until size) {
            if (sortedKeys[index] === key) { // Use an index of the sorted keys list as a virtual view id.
                return index
            }
        }
        return View.NO_ID
    }

    fun createAccessibilityEvent(key: Key, eventType: Int): AccessibilityEvent {
        val virtualViewId = getVirtualViewIdOf(key)
        val keyDescription = getKeyDescription(key)
        val event = AccessibilityEvent.obtain(eventType)
        event.packageName = mKeyboardView!!.context.packageName
        event.className = key.javaClass.name
        event.contentDescription = keyDescription
        event.isEnabled = true
        val record = AccessibilityEventCompat.asRecord(event)
        record.setSource(mKeyboardView, virtualViewId)
        return event
    }

    fun onHoverEnterTo(key: Key) {
        val id = getVirtualViewIdOf(key)
        if (id == View.NO_ID) {
            return
        }

        mHoveringNodeId = id
        sendAccessibilityEventForKey(key, AccessibilityEventCompat.TYPE_WINDOW_CONTENT_CHANGED)
        sendAccessibilityEventForKey(key, AccessibilityEventCompat.TYPE_VIEW_HOVER_ENTER)
    }

    fun onHoverExitFrom(key: Key) {
        mHoveringNodeId = UNDEFINED
        sendAccessibilityEventForKey(key, AccessibilityEventCompat.TYPE_WINDOW_CONTENT_CHANGED)
        sendAccessibilityEventForKey(key, AccessibilityEventCompat.TYPE_VIEW_HOVER_EXIT)
    }

    override fun createAccessibilityNodeInfo(virtualViewId: Int): AccessibilityNodeInfoCompat? {
        if (virtualViewId == UNDEFINED) {
            return null
        }
        if (virtualViewId == View.NO_ID) {
            val rootInfo = AccessibilityNodeInfoCompat.obtain(mKeyboardView)
            ViewCompat.onInitializeAccessibilityNodeInfo(mKeyboardView!!, rootInfo)
            updateParentLocation()
            // Add the virtual children of the root View.
            val sortedKeys = mKeyboard!!.sortedKeys
            val size = sortedKeys.size
            for (index in 0 until size) {
                val key = sortedKeys[index]
                if (key.isSpacer) {
                    continue
                }
                // Use an index of the sorted keys list as a virtual view id.
                rootInfo.addChild(mKeyboardView, index)
            }
            return rootInfo
        }
        // Find the key that corresponds to the given virtual view id.
        val key = getKeyOf(virtualViewId)
        if (key == null) {
            Log.e(TAG, "Invalid virtual view ID: $virtualViewId")
            return null
        }
        val keyDescription = getKeyDescription(key)
        val boundsInParent = key.hitBox
        // Calculate the key's in-screen bounds.
        mTempBoundsInScreen.set(boundsInParent)
        mTempBoundsInScreen.offset(
                CoordinateUtils.x(mParentLocation), CoordinateUtils.y(mParentLocation))
        val boundsInScreen = mTempBoundsInScreen
        // Obtain and initialize an AccessibilityNodeInfo with information about the virtual view.
        val info = AccessibilityNodeInfoCompat.obtain()
        info.packageName = mKeyboardView!!.context.packageName
        info.className = key.javaClass.name
        info.contentDescription = keyDescription
        info.setBoundsInParent(boundsInParent)
        info.setBoundsInScreen(boundsInScreen)
        info.setParent(mKeyboardView)
        info.setSource(mKeyboardView, virtualViewId)
        info.isEnabled = key.isEnabled
        info.isVisibleToUser = true

        if (virtualViewId != mHoveringNodeId) {
            info.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
            if (key.isLongPressEnabled) {
                info.addAction(AccessibilityNodeInfoCompat.ACTION_LONG_CLICK)
            }
        }
        if (mAccessibilityFocusedView == virtualViewId) {
            info.addAction(AccessibilityNodeInfoCompat.ACTION_CLEAR_ACCESSIBILITY_FOCUS)
        } else {
            info.addAction(AccessibilityNodeInfoCompat.ACTION_ACCESSIBILITY_FOCUS)
        }
        return info
    }

    override fun performAction(virtualViewId: Int, action: Int,
                               arguments: Bundle): Boolean {
        val key = getKeyOf(virtualViewId) ?: return false
        return performActionForKey(key, action)
    }

    fun performActionForKey(key: Key, action: Int): Boolean {
        return when (action) {
            AccessibilityNodeInfoCompat.ACTION_ACCESSIBILITY_FOCUS -> {
                mAccessibilityFocusedView = getVirtualViewIdOf(key)
                sendAccessibilityEventForKey(
                        key, AccessibilityEventCompat.TYPE_VIEW_ACCESSIBILITY_FOCUSED)
                true
            }
            AccessibilityNodeInfoCompat.ACTION_CLEAR_ACCESSIBILITY_FOCUS -> {
                mAccessibilityFocusedView = UNDEFINED
                sendAccessibilityEventForKey(
                        key, AccessibilityEventCompat.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED)
                true
            }
            AccessibilityNodeInfoCompat.ACTION_CLICK -> {
                sendAccessibilityEventForKey(key, AccessibilityEvent.TYPE_VIEW_CLICKED)
                mDelegate.performClickOn(key)
                true
            }
            AccessibilityNodeInfoCompat.ACTION_LONG_CLICK -> {
                sendAccessibilityEventForKey(key, AccessibilityEvent.TYPE_VIEW_LONG_CLICKED)
                mDelegate.performLongClickOn(key)
                true
            }
            else -> false
        }
    }

    fun sendAccessibilityEventForKey(key: Key, eventType: Int) {
        val event = createAccessibilityEvent(key, eventType)
        mAccessibilityUtils.requestSendAccessibilityEvent(event)
    }

    private fun getKeyDescription(key: Key): String? {
        val editorInfo = mKeyboard!!.mId.mEditorInfo
        val shouldObscure = mAccessibilityUtils.shouldObscureInput(editorInfo)
        val currentSettings = Settings.getInstance().current
        val keyCodeDescription = mKeyCodeDescriptionMapper.getDescriptionForKey(
                mKeyboardView!!.context, mKeyboard, key, shouldObscure)
        return if (currentSettings.isWordSeparator(key.code)) {
            mAccessibilityUtils.getAutoCorrectionDescription(
                    keyCodeDescription, shouldObscure)
        } else keyCodeDescription
    }

    private fun updateParentLocation() {
        mKeyboardView!!.getLocationOnScreen(mParentLocation)
    }

    companion object {
        private val TAG = KeyboardAccessibilityNodeProvider::class.java.simpleName
        // From {@link android.view.accessibility.AccessibilityNodeInfo#UNDEFINED_ITEM_ID}.
        private const val UNDEFINED = Int.MAX_VALUE
    }

    init {
        mKeyCodeDescriptionMapper = KeyCodeDescriptionMapper.instance
        mAccessibilityUtils = AccessibilityUtils.instance
        mKeyboardView = keyboardView
        mDelegate = delegate
        setKeyboard(keyboardView!!.keyboard)
    }
}