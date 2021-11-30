package com.german.keyboard.app.free.inputmethod.accessibility

import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent
import com.german.keyboard.app.free.keyboard.KeyDetector
import com.german.keyboard.app.free.keyboard.MoreKeysKeyboardView
import com.german.keyboard.app.free.keyboard.PointerTracker

class MoreKeysKeyboardAccessibilityDelegate(moreKeysKeyboardView: MoreKeysKeyboardView,
                                            keyDetector: KeyDetector
) : KeyboardAccessibilityDelegate<MoreKeysKeyboardView?>(moreKeysKeyboardView, keyDetector) {
    private val mMoreKeysKeyboardValidBounds = Rect()
    private var mOpenAnnounceResId = 0
    private var mCloseAnnounceResId = 0
    fun setOpenAnnounce(resId: Int) {
        mOpenAnnounceResId = resId
    }

    fun setCloseAnnounce(resId: Int) {
        mCloseAnnounceResId = resId
    }

    fun onShowMoreKeysKeyboard() {
        sendWindowStateChanged(mOpenAnnounceResId)
    }

    fun onDismissMoreKeysKeyboard() {
        sendWindowStateChanged(mCloseAnnounceResId)
    }

    override fun onHoverEnter(event: MotionEvent) {
        if (DEBUG_HOVER) {
            Log.d(TAG, "onHoverEnter: key=" + getHoverKeyOf(event))
        }
        super.onHoverEnter(event)
        val actionIndex = event.actionIndex
        val x = event.getX(actionIndex).toInt()
        val y = event.getY(actionIndex).toInt()
        val pointerId = event.getPointerId(actionIndex)
        val eventTime = event.eventTime
        mKeyboardView!!.onDownEvent(x, y, pointerId, eventTime)
    }

    override fun onHoverMove(event: MotionEvent) {
        super.onHoverMove(event)
        val actionIndex = event.actionIndex
        val x = event.getX(actionIndex).toInt()
        val y = event.getY(actionIndex).toInt()
        val pointerId = event.getPointerId(actionIndex)
        val eventTime = event.eventTime
        mKeyboardView!!.onMoveEvent(x, y, pointerId, eventTime)
    }

    override fun onHoverExit(event: MotionEvent) {
        val lastKey = lastHoverKey
        if (DEBUG_HOVER) {
            Log.d(TAG, "onHoverExit: key=" + getHoverKeyOf(event) + " last=" + lastKey)
        }
        if (lastKey != null) {
            super.onHoverExitFrom(lastKey)
        }
        lastHoverKey = null
        val actionIndex = event.actionIndex
        val x = event.getX(actionIndex).toInt()
        val y = event.getY(actionIndex).toInt()
        val pointerId = event.getPointerId(actionIndex)
        val eventTime = event.eventTime

        mMoreKeysKeyboardValidBounds[0, 0, mKeyboardView!!.width] = mKeyboardView.height
        mMoreKeysKeyboardValidBounds.inset(CLOSING_INSET_IN_PIXEL, CLOSING_INSET_IN_PIXEL)
        if (mMoreKeysKeyboardValidBounds.contains(x, y)) {
            mKeyboardView.onUpEvent(x, y, pointerId, eventTime)
            // TODO: Should fix this reference. This is a hack to clear the state of
            PointerTracker.dismissAllMoreKeysPanels()
            return
        }
// TODO: Should fix this reference. This is a hack to clear the state of
        PointerTracker.dismissAllMoreKeysPanels()
    }

    companion object {
        private val TAG = MoreKeysKeyboardAccessibilityDelegate::class.java.simpleName
        private const val CLOSING_INSET_IN_PIXEL = 1
    }
}