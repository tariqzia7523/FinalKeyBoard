package com.german.keyboard.app.free.inputmethod.event

import android.text.TextUtils
import android.util.SparseIntArray
import com.german.keyboard.app.free.latin.common.Constants
import java.text.Normalizer
import java.util.*

class DeadKeyCombiner : Combiner {
    private object Data {
        private const val ACCENT_ACUTE = '\u00B4'.toInt()
        private const val ACCENT_BREVE = '\u02D8'.toInt()
        private const val ACCENT_CARON = '\u02C7'.toInt()
        private const val ACCENT_CEDILLA = '\u00B8'.toInt()
        private const val ACCENT_CIRCUMFLEX = '\u02C6'.toInt()
        private const val ACCENT_COMMA_ABOVE = '\u1FBD'.toInt()
        private const val ACCENT_COMMA_ABOVE_RIGHT = '\u02BC'.toInt()
        private const val ACCENT_DOT_ABOVE = '\u02D9'.toInt()
        private const val ACCENT_DOT_BELOW = Constants.CODE_PERIOD // approximate
        private const val ACCENT_DOUBLE_ACUTE = '\u02DD'.toInt()
        private const val ACCENT_GRAVE = '\u02CB'.toInt()
        private const val ACCENT_HOOK_ABOVE = '\u02C0'.toInt()
        private const val ACCENT_HORN = Constants.CODE_SINGLE_QUOTE // approximate
        private const val ACCENT_MACRON = '\u00AF'.toInt()
        private const val ACCENT_MACRON_BELOW = '\u02CD'.toInt()
        private const val ACCENT_OGONEK = '\u02DB'.toInt()
        private const val ACCENT_REVERSED_COMMA_ABOVE = '\u02BD'.toInt()
        private const val ACCENT_RING_ABOVE = '\u02DA'.toInt()
        private const val ACCENT_STROKE = Constants.CODE_DASH // approximate
        private const val ACCENT_TILDE = '\u02DC'.toInt()
        private const val ACCENT_TURNED_COMMA_ABOVE = '\u02BB'.toInt()
        private const val ACCENT_UMLAUT = '\u00A8'.toInt()
        private const val ACCENT_VERTICAL_LINE_ABOVE = '\u02C8'.toInt()
        private const val ACCENT_VERTICAL_LINE_BELOW = '\u02CC'.toInt()
        private const val ACCENT_GRAVE_LEGACY = Constants.CODE_GRAVE_ACCENT
        private const val ACCENT_CIRCUMFLEX_LEGACY = Constants.CODE_CIRCUMFLEX_ACCENT
        private const val ACCENT_TILDE_LEGACY = Constants.CODE_TILDE
        val sCombiningToAccent = SparseIntArray()
        val sAccentToCombining = SparseIntArray()
        private fun addCombining(combining: Int, accent: Int) {
            sCombiningToAccent.append(combining, accent)
            sAccentToCombining.append(accent, combining)
        }

        private val sNonstandardDeadCombinations = SparseIntArray()

        private fun addNonStandardDeadCombination(deadCodePoint: Int,
                                                  spacingCodePoint: Int, result: Int) {
            val combination = deadCodePoint shl 16 or spacingCodePoint
            sNonstandardDeadCombinations.put(combination, result)
        }

        const val NOT_A_CHAR = 0
        const val BITS_TO_SHIFT_DEAD_CODE_POINT_FOR_NON_STANDARD_COMBINATION = 16
        fun getNonstandardCombination(deadCodePoint: Int,
                                      spacingCodePoint: Int): Char {
            val combination = spacingCodePoint or
                    (deadCodePoint shl BITS_TO_SHIFT_DEAD_CODE_POINT_FOR_NON_STANDARD_COMBINATION)
            return sNonstandardDeadCombinations[combination, NOT_A_CHAR].toChar()
        }

        init {
            addCombining('\u0300'.toInt(), ACCENT_GRAVE)
            addCombining('\u0301'.toInt(), ACCENT_ACUTE)
            addCombining('\u0302'.toInt(), ACCENT_CIRCUMFLEX)
            addCombining('\u0303'.toInt(), ACCENT_TILDE)
            addCombining('\u0304'.toInt(), ACCENT_MACRON)
            addCombining('\u0306'.toInt(), ACCENT_BREVE)
            addCombining('\u0307'.toInt(), ACCENT_DOT_ABOVE)
            addCombining('\u0308'.toInt(), ACCENT_UMLAUT)
            addCombining('\u0309'.toInt(), ACCENT_HOOK_ABOVE)
            addCombining('\u030A'.toInt(), ACCENT_RING_ABOVE)
            addCombining('\u030B'.toInt(), ACCENT_DOUBLE_ACUTE)
            addCombining('\u030C'.toInt(), ACCENT_CARON)
            addCombining('\u030D'.toInt(), ACCENT_VERTICAL_LINE_ABOVE)
            addCombining('\u0312'.toInt(), ACCENT_TURNED_COMMA_ABOVE)
            addCombining('\u0313'.toInt(), ACCENT_COMMA_ABOVE)
            addCombining('\u0314'.toInt(), ACCENT_REVERSED_COMMA_ABOVE)
            addCombining('\u0315'.toInt(), ACCENT_COMMA_ABOVE_RIGHT)
            addCombining('\u031B'.toInt(), ACCENT_HORN)
            addCombining('\u0323'.toInt(), ACCENT_DOT_BELOW)
            addCombining('\u0327'.toInt(), ACCENT_CEDILLA)
            addCombining('\u0328'.toInt(), ACCENT_OGONEK)
            addCombining('\u0329'.toInt(), ACCENT_VERTICAL_LINE_BELOW)
            addCombining('\u0331'.toInt(), ACCENT_MACRON_BELOW)
            addCombining('\u0335'.toInt(), ACCENT_STROKE)
            sCombiningToAccent.append('\u0340'.toInt(), ACCENT_GRAVE)
            sCombiningToAccent.append('\u0341'.toInt(), ACCENT_ACUTE)
            sCombiningToAccent.append('\u0343'.toInt(), ACCENT_COMMA_ABOVE)
            sAccentToCombining.append(ACCENT_GRAVE_LEGACY, '\u0300'.toInt())
            sAccentToCombining.append(ACCENT_CIRCUMFLEX_LEGACY, '\u0302'.toInt())
            sAccentToCombining.append(ACCENT_TILDE_LEGACY, '\u0303'.toInt())
        }

        init {
            addNonStandardDeadCombination(ACCENT_STROKE, 'D'.toInt(), '\u0110'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'G'.toInt(), '\u01e4'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'H'.toInt(), '\u0126'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'I'.toInt(), '\u0197'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'L'.toInt(), '\u0141'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'O'.toInt(), '\u00d8'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'T'.toInt(), '\u0166'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'd'.toInt(), '\u0111'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'g'.toInt(), '\u01e5'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'h'.toInt(), '\u0127'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'i'.toInt(), '\u0268'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'l'.toInt(), '\u0142'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 'o'.toInt(), '\u00f8'.toInt())
            addNonStandardDeadCombination(ACCENT_STROKE, 't'.toInt(), '\u0167'.toInt())
        }
    }

    // TODO: make this a list of events instead
    val mDeadSequence = StringBuilder()

    override fun processEvent(previousEvents: ArrayList<Event>?, event: Event?): Event? {
        if (TextUtils.isEmpty(mDeadSequence)) {
            if (event!!.isDead) {
                mDeadSequence.appendCodePoint(event.mCodePoint)
                return Event.createConsumedEvent(event)
            }
            return event
        }
        if (Character.isWhitespace(event!!.mCodePoint)
                || event.mCodePoint == mDeadSequence.codePointBefore(mDeadSequence.length)) {
            val resultEvent = createEventChainFromSequence(mDeadSequence.toString(),
                    event)
            mDeadSequence.setLength(0)
            return resultEvent
        }
        if (event.isFunctionalKeyEvent) {
            if (Constants.CODE_DELETE == event.mKeyCode) {
                val trimIndex = mDeadSequence.length - Character.charCount(
                        mDeadSequence.codePointBefore(mDeadSequence.length))
                mDeadSequence.setLength(trimIndex)
                return Event.createConsumedEvent(event)
            }
            return event
        }
        if (event.isDead) {
            mDeadSequence.appendCodePoint(event.mCodePoint)
            return Event.createConsumedEvent(event)
        }

        val sb = StringBuilder()
        sb.appendCodePoint(event.mCodePoint)
        var codePointIndex = 0
        while (codePointIndex < mDeadSequence.length) {
            val deadCodePoint = mDeadSequence.codePointAt(codePointIndex)
            val replacementSpacingChar =
                Data.getNonstandardCombination(deadCodePoint, event.mCodePoint)
            if (Data.NOT_A_CHAR != replacementSpacingChar.toInt()) {
                sb.setCharAt(0, replacementSpacingChar)
            } else {
                val combining = Data.sAccentToCombining[deadCodePoint]
                sb.appendCodePoint(if (0 == combining) deadCodePoint else combining)
            }
            codePointIndex += if (Character.isSupplementaryCodePoint(deadCodePoint)) 2 else 1
        }
        val normalizedString = Normalizer.normalize(sb, Normalizer.Form.NFC)
        val resultEvent = createEventChainFromSequence(normalizedString, event)
        mDeadSequence.setLength(0)
        return resultEvent
    }

    override fun reset() {
        mDeadSequence.setLength(0)
    }

    override val combiningStateFeedback: CharSequence
        get() = mDeadSequence

    companion object {
        private fun createEventChainFromSequence(text: CharSequence,
                                                 originalEvent: Event?): Event? {
            var index = text.length
            if (index <= 0) {
                return originalEvent
            }
            var lastEvent: Event? = null
            do {
                val codePoint = Character.codePointBefore(text, index)
                lastEvent = Event.createHardwareKeypressEvent(
                    codePoint,
                    originalEvent!!.mKeyCode, lastEvent, false /* isKeyRepeat */
                )
                index -= Character.charCount(codePoint)
            } while (index > 0)
            return lastEvent
        }
    }
}