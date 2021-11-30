package com.german.keyboard.app.free.inputmethod.accessibility

import android.graphics.Rect
import android.os.SystemClock
import android.util.Log
import android.util.SparseIntArray
import android.view.MotionEvent
import com.german.keyboard.app.free.inputmethod.accessibility.AccessibilityLongPressTimer.LongPressTimerCallback
import com.german.keyboard.app.free.R
import com.german.keyboard.app.free.keyboard.*
import com.german.keyboard.app.free.latin.utils.SubtypeLocaleUtils

class MainKeyboardAccessibilityDelegate(mainKeyboardView: MainKeyboardView,
                                        keyDetector: KeyDetector
) : KeyboardAccessibilityDelegate<MainKeyboardView?>(mainKeyboardView, keyDetector), LongPressTimerCallback {
    companion object {
        private val TAG = MainKeyboardAccessibilityDelegate::class.java.simpleName
        /** Map of keyboard modes to resource IDs.  */
        private val KEYBOARD_MODE_RES_IDS = SparseIntArray()
        private const val KEYBOARD_IS_HIDDEN = -1

        init {
            KEYBOARD_MODE_RES_IDS.put(KeyboardId.MODE_DATE, R.string.keyboard_mode_date)
            KEYBOARD_MODE_RES_IDS.put(KeyboardId.MODE_DATETIME, R.string.keyboard_mode_date_time)
            KEYBOARD_MODE_RES_IDS.put(KeyboardId.MODE_EMAIL, R.string.keyboard_mode_email)
            KEYBOARD_MODE_RES_IDS.put(KeyboardId.MODE_IM, R.string.keyboard_mode_im)
            KEYBOARD_MODE_RES_IDS.put(KeyboardId.MODE_NUMBER, R.string.keyboard_mode_number)
            KEYBOARD_MODE_RES_IDS.put(KeyboardId.MODE_PHONE, R.string.keyboard_mode_phone)
            KEYBOARD_MODE_RES_IDS.put(KeyboardId.MODE_TEXT, R.string.keyboard_mode_text)
            KEYBOARD_MODE_RES_IDS.put(KeyboardId.MODE_TIME, R.string.keyboard_mode_time)
            KEYBOARD_MODE_RES_IDS.put(KeyboardId.MODE_URL, R.string.keyboard_mode_url)
        }
    }

    private var mLastKeyboardMode = KEYBOARD_IS_HIDDEN
    private val mBoundsToIgnoreHoverEvent = Rect()
    private val mAccessibilityLongPressTimer: AccessibilityLongPressTimer// Since this method is called even when accessibility is off, make sure

    override var keyboard: Keyboard?
        get() = super.keyboard
        set(keyboard) {
            if (keyboard == null) {
                return
            }
            val lastKeyboard = super.keyboard
            super.keyboard = keyboard
            val lastKeyboardMode = mLastKeyboardMode
            mLastKeyboardMode = keyboard.mId.mMode

            if (!AccessibilityUtils.instance.isAccessibilityEnabled) {
                return
            }
            // Announce the language name only when the language is changed.
            if (lastKeyboard == null || keyboard.mId.mSubtype != lastKeyboard.mId.mSubtype) {
                announceKeyboardLanguage(keyboard)
                return
            }
            // Announce the mode only when the mode is changed.
            if (keyboard.mId.mMode != lastKeyboardMode) {
                announceKeyboardMode(keyboard)
                return
            }
            // Announce the keyboard type only when the type is changed.
            if (keyboard.mId.mElementId != lastKeyboard.mId.mElementId) {
                announceKeyboardType(keyboard, lastKeyboard)
                return
            }
        }

    fun onHideWindow() {
        if (mLastKeyboardMode != KEYBOARD_IS_HIDDEN) {
            announceKeyboardHidden()
        }
        mLastKeyboardMode = KEYBOARD_IS_HIDDEN
    }

    private fun announceKeyboardLanguage(keyboard: Keyboard) {
        val languageText = SubtypeLocaleUtils.getSubtypeDisplayNameInSystemLocale(
                keyboard.mId.mSubtype.rawSubtype)
        sendWindowStateChanged(languageText)
    }

    private fun announceKeyboardMode(keyboard: Keyboard) {
        val context = mKeyboardView!!.context
        val modeTextResId = KEYBOARD_MODE_RES_IDS[keyboard.mId.mMode]
        if (modeTextResId == 0) {
            return
        }
        val modeText = context.getString(modeTextResId)
        val text = context.getString(R.string.announce_keyboard_mode, modeText)
        sendWindowStateChanged(text)
    }

    private fun announceKeyboardType(keyboard: Keyboard, lastKeyboard: Keyboard) {
        val lastElementId = lastKeyboard.mId.mElementId
        val resId: Int
        resId = when (keyboard.mId.mElementId) {
            KeyboardId.ELEMENT_ALPHABET_AUTOMATIC_SHIFTED, KeyboardId.ELEMENT_ALPHABET -> {
                if (lastElementId == KeyboardId.ELEMENT_ALPHABET
                        || lastElementId == KeyboardId.ELEMENT_ALPHABET_AUTOMATIC_SHIFTED) {
                    return
                }
                R.string.spoken_description_mode_alpha
            }
            KeyboardId.ELEMENT_ALPHABET_MANUAL_SHIFTED -> {
                if (lastElementId == KeyboardId.ELEMENT_ALPHABET_AUTOMATIC_SHIFTED) {
                    return
                }
                R.string.spoken_description_shiftmode_on
            }
            KeyboardId.ELEMENT_ALPHABET_SHIFT_LOCK_SHIFTED -> {
                if (lastElementId == KeyboardId.ELEMENT_ALPHABET_SHIFT_LOCKED) { // Resetting caps locked mode by pressing the shift key causes the transition
                    return
                }
                R.string.spoken_description_shiftmode_locked
            }
            KeyboardId.ELEMENT_ALPHABET_SHIFT_LOCKED -> R.string.spoken_description_shiftmode_locked
            KeyboardId.ELEMENT_SYMBOLS -> R.string.spoken_description_mode_symbol
            KeyboardId.ELEMENT_SYMBOLS_SHIFTED -> R.string.spoken_description_mode_symbol_shift
            KeyboardId.ELEMENT_PHONE -> R.string.spoken_description_mode_phone
            KeyboardId.ELEMENT_PHONE_SYMBOLS -> R.string.spoken_description_mode_phone_shift
            else -> return
        }
        sendWindowStateChanged(resId)
    }

    private fun announceKeyboardHidden() {
        sendWindowStateChanged(R.string.announce_keyboard_hidden)
    }

    override fun performClickOn(key: Key) {
        val x = key.hitBox.centerX()
        val y = key.hitBox.centerY()
        if (DEBUG_HOVER) {
            Log.d(
                TAG, "performClickOn: key=" + key
                    + " inIgnoreBounds=" + mBoundsToIgnoreHoverEvent.contains(x, y))
        }
        if (mBoundsToIgnoreHoverEvent.contains(x, y)) {
            mBoundsToIgnoreHoverEvent.setEmpty()
            return
        }
        super.performClickOn(key)
    }

    override fun onHoverEnterTo(key: Key) {
        val x = key.hitBox.centerX()
        val y = key.hitBox.centerY()
        if (DEBUG_HOVER) {
            Log.d(
                TAG, "onHoverEnterTo: key=" + key
                    + " inIgnoreBounds=" + mBoundsToIgnoreHoverEvent.contains(x, y))
        }
        mAccessibilityLongPressTimer.cancelLongPress()
        if (mBoundsToIgnoreHoverEvent.contains(x, y)) {
            return
        }

        mBoundsToIgnoreHoverEvent.setEmpty()
        super.onHoverEnterTo(key)
        if (key.isLongPressEnabled) {
            mAccessibilityLongPressTimer.startLongPress(key)
        }
    }

    override fun onHoverExitFrom(key: Key) {
        val x = key.hitBox.centerX()
        val y = key.hitBox.centerY()
        if (DEBUG_HOVER) {
            Log.d(
                TAG, "onHoverExitFrom: key=" + key
                    + " inIgnoreBounds=" + mBoundsToIgnoreHoverEvent.contains(x, y))
        }
        mAccessibilityLongPressTimer.cancelLongPress()
        super.onHoverExitFrom(key)
    }

    override fun performLongClickOn(key: Key) {
        if (DEBUG_HOVER) {
            Log.d(TAG, "performLongClickOn: key=$key")
        }
        val tracker = PointerTracker.getPointerTracker(HOVER_EVENT_POINTER_ID)
        val eventTime = SystemClock.uptimeMillis()
        val x = key.hitBox.centerX()
        val y = key.hitBox.centerY()
        val downEvent = MotionEvent.obtain(
                eventTime, eventTime, MotionEvent.ACTION_DOWN, x.toFloat(), y.toFloat(), 0 /* metaState */)
        tracker.processMotionEvent(downEvent, mKeyDetector)
        downEvent.recycle()
        tracker.onLongPressed()

        if (tracker.isInOperation) {
            mBoundsToIgnoreHoverEvent.setEmpty()
            return
        }
        mBoundsToIgnoreHoverEvent.set(key.hitBox)
        if (key.hasNoPanelAutoMoreKey()) {
            val codePointOfNoPanelAutoMoreKey = key.moreKeys!![0].mCode
            val text: String = KeyCodeDescriptionMapper.instance.getDescriptionForCodePoint(
                    mKeyboardView!!.context, codePointOfNoPanelAutoMoreKey)!!
            text.let { sendWindowStateChanged(it) }
        }
    }

    init {
        mAccessibilityLongPressTimer = AccessibilityLongPressTimer(
                this /* callback */, mainKeyboardView.context)
    }
}