package com.german.keyboard.app.free.inputmethod.event

import com.german.keyboard.app.free.inputmethod.annotations.ExternallyReferenced
import com.german.keyboard.app.free.latin.SuggestedWords.SuggestedWordInfo
import com.german.keyboard.app.free.latin.common.Constants
import com.german.keyboard.app.free.latin.common.StringUtils

class Event private constructor(
    private val mEventType: Int,
    val mText: CharSequence?,
    val mCodePoint: Int,
    val mKeyCode: Int,
    val mX: Int, val mY: Int,
    val mSuggestedWordInfo: SuggestedWordInfo?,
    private val mFlags: Int,
    val mNextEvent: Event?) {
    val isFunctionalKeyEvent: Boolean
        get() =
            NOT_A_CODE_POINT == mCodePoint
    val isDead: Boolean
        get() = 0 != FLAG_DEAD and mFlags

    val isKeyRepeat: Boolean
        get() = 0 != FLAG_REPEAT and mFlags

    val isConsumed: Boolean
        get() = 0 != FLAG_CONSUMED and mFlags

    val isGesture: Boolean
        get() = EVENT_TYPE_GESTURE == mEventType
    val isSuggestionStripPress: Boolean
        get() = EVENT_TYPE_SUGGESTION_PICKED == mEventType

    val isHandled: Boolean
        get() = EVENT_TYPE_NOT_HANDLED != mEventType
    val textToCommit: CharSequence?
        get() {
            if (isConsumed) {
                return ""
            }
            when (mEventType) {
                EVENT_TYPE_MODE_KEY, EVENT_TYPE_NOT_HANDLED, EVENT_TYPE_TOGGLE, EVENT_TYPE_CURSOR_MOVE -> return ""
                EVENT_TYPE_INPUT_KEYPRESS -> return StringUtils.newSingleCodePointString(mCodePoint)
                EVENT_TYPE_GESTURE, EVENT_TYPE_SOFTWARE_GENERATED_STRING, EVENT_TYPE_SUGGESTION_PICKED -> return mText
            }
            throw RuntimeException("Unknown event type: $mEventType")
        }

    companion object {
        const val EVENT_TYPE_NOT_HANDLED = 0
        const val EVENT_TYPE_INPUT_KEYPRESS = 1
        const val EVENT_TYPE_TOGGLE = 2
        const val EVENT_TYPE_MODE_KEY = 3
        const val EVENT_TYPE_GESTURE = 4
        const val EVENT_TYPE_SUGGESTION_PICKED = 5
        const val EVENT_TYPE_SOFTWARE_GENERATED_STRING = 6
        const val EVENT_TYPE_CURSOR_MOVE = 7
        const val NOT_A_CODE_POINT = -1
        const val NOT_A_KEY_CODE = 0
        private const val FLAG_NONE = 0
        private const val FLAG_DEAD = 0x1
        private const val FLAG_REPEAT = 0x2
        private const val FLAG_CONSUMED = 0x4

        @JvmStatic
        fun createSoftwareKeypressEvent(
            codePoint: Int, keyCode: Int,
            x: Int, y: Int, isKeyRepeat: Boolean
        ): Event {
            return Event(
                EVENT_TYPE_INPUT_KEYPRESS, null /* text */, codePoint, keyCode, x, y,
                null /* suggestedWordInfo */, if (isKeyRepeat) FLAG_REPEAT else FLAG_NONE, null
            )
        }

        fun createHardwareKeypressEvent(
            codePoint: Int, keyCode: Int,
            next: Event?, isKeyRepeat: Boolean
        ): Event {
            return Event(
                EVENT_TYPE_INPUT_KEYPRESS, null /* text */, codePoint, keyCode,
                Constants.EXTERNAL_KEYBOARD_COORDINATE, Constants.EXTERNAL_KEYBOARD_COORDINATE,
                null /* suggestedWordInfo */, if (isKeyRepeat) FLAG_REPEAT else FLAG_NONE, next
            )
        }

        @ExternallyReferenced
        fun createDeadEvent(
            codePoint: Int,
            keyCode: Int,
            next: Event?
        ): Event { // TODO: add an argument or something if we ever create a software layout with dead keys.
            return Event(
                EVENT_TYPE_INPUT_KEYPRESS, null /* text */, codePoint, keyCode,
                Constants.EXTERNAL_KEYBOARD_COORDINATE, Constants.EXTERNAL_KEYBOARD_COORDINATE,
                null /* suggestedWordInfo */, FLAG_DEAD, next
            )
        }

        @JvmStatic
        fun createEventForCodePointFromUnknownSource(codePoint: Int): Event { // TODO: should we have a different type of event for this? After all, it's not a key press.
            return Event(
                EVENT_TYPE_INPUT_KEYPRESS, null /* text */, codePoint, NOT_A_KEY_CODE,
                Constants.NOT_A_COORDINATE, Constants.NOT_A_COORDINATE,
                null /* suggestedWordInfo */, FLAG_NONE, null /* next */
            )
        }

        @JvmStatic
        fun createEventForCodePointFromAlreadyTypedText(
            codePoint: Int,
            x: Int, y: Int
        ): Event { // TODO: should we have a different type of event for this? After all, it's not a key press.
            return Event(
                EVENT_TYPE_INPUT_KEYPRESS, null /* text */, codePoint, NOT_A_KEY_CODE,
                x, y, null /* suggestedWordInfo */, FLAG_NONE, null /* next */
            )
        }

        @JvmStatic
        fun createSuggestionPickedEvent(suggestedWordInfo: SuggestedWordInfo): Event {
            return Event(
                EVENT_TYPE_SUGGESTION_PICKED, suggestedWordInfo.mWord,
                NOT_A_CODE_POINT, NOT_A_KEY_CODE,
                Constants.SUGGESTION_STRIP_COORDINATE, Constants.SUGGESTION_STRIP_COORDINATE,
                suggestedWordInfo, FLAG_NONE, null /* next */
            )
        }

        @JvmStatic
        fun createSoftwareTextEvent(text: CharSequence?, keyCode: Int): Event {
            return Event(
                EVENT_TYPE_SOFTWARE_GENERATED_STRING, text, NOT_A_CODE_POINT, keyCode,
                Constants.NOT_A_COORDINATE, Constants.NOT_A_COORDINATE,
                null /* suggestedWordInfo */, FLAG_NONE, null /* next */
            )
        }

        @JvmStatic
        fun createPunctuationSuggestionPickedEvent(
            suggestedWordInfo: SuggestedWordInfo
        ): Event {
            val primaryCode = suggestedWordInfo.mWord[0].toInt()
            return Event(
                EVENT_TYPE_SUGGESTION_PICKED, suggestedWordInfo.mWord, primaryCode,
                NOT_A_KEY_CODE, Constants.SUGGESTION_STRIP_COORDINATE,
                Constants.SUGGESTION_STRIP_COORDINATE, suggestedWordInfo, FLAG_NONE,
                null /* next */
            )
        }

        @JvmStatic
        fun createCursorMovedEvent(moveAmount: Int): Event {
            return Event(
                EVENT_TYPE_CURSOR_MOVE, null, NOT_A_CODE_POINT, NOT_A_KEY_CODE,
                moveAmount, Constants.NOT_A_COORDINATE, null, FLAG_NONE, null
            )
        }

        fun createConsumedEvent(source: Event?): Event { // A consumed event should not input any text at all, so we pass the empty string as text.
            return Event(
                source!!.mEventType, source.mText, source.mCodePoint, source.mKeyCode,
                source.mX, source.mY, source.mSuggestedWordInfo, source.mFlags or FLAG_CONSUMED,
                source.mNextEvent
            )
        }

        fun createNotHandledEvent(): Event {
            return Event(
                EVENT_TYPE_NOT_HANDLED, null /* text */, NOT_A_CODE_POINT, NOT_A_KEY_CODE,
                Constants.NOT_A_COORDINATE, Constants.NOT_A_COORDINATE,
                null /* suggestedWordInfo */, FLAG_NONE, null
            )
        }
    }

    init {
        if (EVENT_TYPE_SUGGESTION_PICKED == mEventType) {
            if (null == mSuggestedWordInfo) {
                throw RuntimeException(
                    "Wrong event: SUGGESTION_PICKED event must have a "
                            + "non-null SuggestedWordInfo"
                )
            }
        } else {
            if (null != mSuggestedWordInfo) {
                throw RuntimeException(
                    "Wrong event: only SUGGESTION_PICKED events may have " +
                            "a non-null SuggestedWordInfo"
                )
            }
        }
    }
}