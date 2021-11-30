package com.german.keyboard.app.free.inputmethod.event

import android.text.SpannableStringBuilder
import android.text.TextUtils
import com.german.keyboard.app.free.latin.common.Constants
import java.util.*

class CombinerChain(initialText: String?) {
    private val mCombinedText: StringBuilder
    private val mStateFeedback: SpannableStringBuilder
    private val mCombiners: ArrayList<Combiner>
    fun reset() {
        mCombinedText.setLength(0)
        mStateFeedback.clear()
        for (c in mCombiners) {
            c.reset()
        }
    }

    private fun updateStateFeedback() {
        mStateFeedback.clear()
        for (i in mCombiners.indices.reversed()) {
            mStateFeedback.append(mCombiners[i].combiningStateFeedback)
        }
    }

    fun processEvent(previousEvents: ArrayList<Event>?,
                     newEvent: Event?): Event? {
        val modifiablePreviousEvents = ArrayList(previousEvents!!)
        var event = newEvent
        for (combiner in mCombiners) {
            event = combiner.processEvent(modifiablePreviousEvents, event)
            if (event!!.isConsumed) {
                break
            }
        }
        updateStateFeedback()
        return event
    }

    fun applyProcessedEvent(event: Event?) {
        if (null != event) { // TODO: figure out the generic way of doing this
            if (Constants.CODE_DELETE == event.mKeyCode) {
                val length = mCombinedText.length
                if (length > 0) {
                    val lastCodePoint = mCombinedText.codePointBefore(length)
                    mCombinedText.delete(length - Character.charCount(lastCodePoint), length)
                }
            } else {
                val textToCommit = event.textToCommit
                if (!TextUtils.isEmpty(textToCommit)) {
                    mCombinedText.append(textToCommit)
                }
            }
        }
        updateStateFeedback()
    }

    val composingWordWithCombiningFeedback: CharSequence
        get() {
            val s = SpannableStringBuilder(mCombinedText)
            return s.append(mStateFeedback)
        }

    init {
        mCombiners = ArrayList()
        mCombiners.add(DeadKeyCombiner())
        mCombinedText = StringBuilder(initialText!!)
        mStateFeedback = SpannableStringBuilder()
    }
}