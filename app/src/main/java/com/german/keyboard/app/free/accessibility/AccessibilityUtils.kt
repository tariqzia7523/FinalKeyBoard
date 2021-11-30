package com.german.keyboard.app.free.inputmethod.accessibility

import android.content.Context
import android.media.AudioManager
import android.os.SystemClock
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.EditorInfo
import androidx.core.view.accessibility.AccessibilityEventCompat
import com.german.keyboard.app.free.R
import com.german.keyboard.app.free.latin.SuggestedWords
import com.german.keyboard.app.free.latin.utils.InputTypeUtils

class AccessibilityUtils private constructor() {
    private var mContext: Context? = null
    private var mAccessibilityManager: AccessibilityManager? = null
    private var mAudioManager: AudioManager? = null
    /** The most recent auto-correction.  */
    private var mAutoCorrectionWord: String? = null
    /** The most recent typed word for auto-correction.  */
    private var mTypedWord: String? = null

    private fun initInternal(context: Context) {
        mContext = context
        mAccessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    val isAccessibilityEnabled: Boolean
        get() = ENABLE_ACCESSIBILITY && mAccessibilityManager!!.isEnabled

    val isTouchExplorationEnabled: Boolean
        get() = isAccessibilityEnabled && mAccessibilityManager!!.isTouchExplorationEnabled

    fun shouldObscureInput(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        // The user can optionally force speaking passwords.
        if (Settings.Secure.ACCESSIBILITY_SPEAK_PASSWORD != null) {
            val speakPassword = Settings.Secure.getInt(mContext!!.contentResolver,
                    Settings.Secure.ACCESSIBILITY_SPEAK_PASSWORD, 0) != 0
            if (speakPassword) return false
        }
        // Always speak if the user is listening through headphones.
        return if (mAudioManager!!.isWiredHeadsetOn || mAudioManager!!.isBluetoothA2dpOn) {
            false
        } else InputTypeUtils.isPasswordInputType(editorInfo.inputType)
        // Don't speak if the IME is connected to a password field.
    }

    fun setAutoCorrection(suggestedWords: SuggestedWords) {
        if (suggestedWords.mWillAutoCorrect) {
            mAutoCorrectionWord = suggestedWords.getWord(SuggestedWords.INDEX_OF_AUTO_CORRECTION)
            val typedWordInfo = suggestedWords.mTypedWordInfo
            mTypedWord = typedWordInfo?.mWord
        } else {
            mAutoCorrectionWord = null
            mTypedWord = null
        }
    }

    fun getAutoCorrectionDescription(
            keyCodeDescription: String?, shouldObscure: Boolean): String? {
        if (!TextUtils.isEmpty(mAutoCorrectionWord)) {
            if (!TextUtils.equals(mAutoCorrectionWord, mTypedWord)) {
                return if (shouldObscure) { // This should never happen, but just in case...
                    mContext!!.getString(R.string.spoken_auto_correct_obscured,
                            keyCodeDescription)
                } else mContext!!.getString(R.string.spoken_auto_correct, keyCodeDescription,
                        mTypedWord, mAutoCorrectionWord)
            }
        }
        return keyCodeDescription
    }

    fun announceForAccessibility(view: View, text: CharSequence?) {
        if (!mAccessibilityManager!!.isEnabled) {
            Log.e(TAG, "Attempted to speak when accessibility was disabled!")
            return
        }

        val event = AccessibilityEvent.obtain()
        event.packageName = PACKAGE
        event.className = CLASS
        event.eventTime = SystemClock.uptimeMillis()
        event.isEnabled = true
        event.text.add(text)
        // Platforms starting at SDK version 16 (Build.VERSION_CODES.JELLY_BEAN) should use
// announce events.
        event.eventType = AccessibilityEventCompat.TYPE_ANNOUNCEMENT
        val viewParent = view.parent
        if (viewParent == null || viewParent !is ViewGroup) {
            Log.e(TAG, "Failed to obtain ViewParent in announceForAccessibility")
            return
        }
        viewParent.requestSendAccessibilityEvent(view, event)
    }

    fun onStartInputViewInternal(view: View, editorInfo: EditorInfo?,
                                 restarting: Boolean) {
        if (shouldObscureInput(editorInfo)) {
            val text = mContext!!.getText(R.string.spoken_use_headphones)
            announceForAccessibility(view, text)
        }
    }

    fun requestSendAccessibilityEvent(event: AccessibilityEvent?) {
        if (mAccessibilityManager!!.isEnabled) {
            mAccessibilityManager!!.sendAccessibilityEvent(event)
        }
    }

    companion object {
        private val TAG = AccessibilityUtils::class.java.simpleName
        private val CLASS = AccessibilityUtils::class.java.name
        private val PACKAGE = AccessibilityUtils::class.java.getPackage()!!.name
        val instance = AccessibilityUtils()

        private const val ENABLE_ACCESSIBILITY = true

        @JvmStatic
        fun init(context: Context) {
            if (!ENABLE_ACCESSIBILITY) return
            // These only need to be initialized if the kill switch is off.
            instance.initInternal(context)
        }

        fun isTouchExplorationEvent(event: MotionEvent): Boolean {
            val action = event.action
            return action == MotionEvent.ACTION_HOVER_ENTER || action == MotionEvent.ACTION_HOVER_EXIT || action == MotionEvent.ACTION_HOVER_MOVE
        }
    }
}