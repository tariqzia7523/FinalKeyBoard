package com.german.keyboard.app.free.inputmethod.event

import java.util.*

interface Combiner {
    fun processEvent(previousEvents: ArrayList<Event>?, event: Event?): Event?
    val combiningStateFeedback: CharSequence
    fun reset()
}