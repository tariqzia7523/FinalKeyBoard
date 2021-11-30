package com.german.keyboard.app.free.keyboard.emoji;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

import com.german.keyboard.app.free.inputmethod.accessibility.AccessibilityUtils;
import com.german.keyboard.app.free.inputmethod.accessibility.KeyboardAccessibilityDelegate;
import com.german.keyboard.app.free.keyboard.Key;
import com.german.keyboard.app.free.keyboard.KeyDetector;
import com.german.keyboard.app.free.keyboard.Keyboard;
import com.german.keyboard.app.free.keyboard.KeyboardView;
import com.german.keyboard.app.free.R;

// TODO: Implement key popup preview.
final class EmojiPageKeyboardView extends KeyboardView implements
        GestureDetector.OnGestureListener {
    private static final long KEY_PRESS_DELAY_TIME = 250;  // msec
    private static final long KEY_RELEASE_DELAY_TIME = 30;  // msec

    public interface OnKeyEventListener {
        void onPressKey(Key key);
        void onReleaseKey(Key key);
    }

    private static final OnKeyEventListener EMPTY_LISTENER = new OnKeyEventListener() {
        @Override
        public void onPressKey(final Key key) {}
        @Override
        public void onReleaseKey(final Key key) {}
    };

    private OnKeyEventListener mListener = EMPTY_LISTENER;
    private final KeyDetector mKeyDetector = new KeyDetector();
    private final GestureDetector mGestureDetector;
    private KeyboardAccessibilityDelegate<EmojiPageKeyboardView> mAccessibilityDelegate;

    public EmojiPageKeyboardView(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.keyboardViewStyle);
    }

    public EmojiPageKeyboardView(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setIsLongpressEnabled(false /* isLongpressEnabled */);
        mHandler = new Handler();
    }

    public void setOnKeyEventListener(final OnKeyEventListener listener) {
        mListener = listener;
    }

    @Override
    public void setKeyboard(final Keyboard keyboard) {
        super.setKeyboard(keyboard);
        mKeyDetector.setKeyboard(keyboard, 0 /* correctionX */, 0 /* correctionY */);
        if (AccessibilityUtils.Companion.getInstance().isAccessibilityEnabled()) {
            if (mAccessibilityDelegate == null) {
                mAccessibilityDelegate = new KeyboardAccessibilityDelegate<>(this, mKeyDetector);
            }
            mAccessibilityDelegate.setKeyboard(keyboard);
        } else {
            mAccessibilityDelegate = null;
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent event) {
        // Don't populate accessibility event with all Emoji keys.
        return true;
    }

    @Override
    public boolean onHoverEvent(final MotionEvent event) {
        final KeyboardAccessibilityDelegate<EmojiPageKeyboardView> accessibilityDelegate =
                mAccessibilityDelegate;
        if (accessibilityDelegate != null
                && AccessibilityUtils.Companion.getInstance().isTouchExplorationEnabled()) {
            return accessibilityDelegate.onHoverEvent(event);
        }
        return super.onHoverEvent(event);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent e) {
        if (mGestureDetector.onTouchEvent(e)) {
            return true;
        }
        final Key key = getKey(e);
        if (key != null && key != mCurrentKey) {
            releaseCurrentKey(false /* withKeyRegistering */);
        }
        return true;
    }

    // {@link GestureEnabler#OnGestureListener} methods.
    private Key mCurrentKey;
    private Runnable mPendingKeyDown;
    private final Handler mHandler;

    private Key getKey(final MotionEvent e) {
        final int index = e.getActionIndex();
        final int x = (int)e.getX(index);
        final int y = (int)e.getY(index);
        return mKeyDetector.detectHitKey(x, y);
    }

    void callListenerOnReleaseKey(final Key releasedKey, final boolean withKeyRegistering) {
        releasedKey.onReleased();
        invalidateKey(releasedKey);
        if (withKeyRegistering) {
            mListener.onReleaseKey(releasedKey);
        }
    }

    void callListenerOnPressKey(final Key pressedKey) {
        mPendingKeyDown = null;
        pressedKey.onPressed();
        invalidateKey(pressedKey);
        mListener.onPressKey(pressedKey);
    }

    public void releaseCurrentKey(final boolean withKeyRegistering) {
        mHandler.removeCallbacks(mPendingKeyDown);
        mPendingKeyDown = null;
        final Key currentKey = mCurrentKey;
        if (currentKey == null) {
            return;
        }
        callListenerOnReleaseKey(currentKey, withKeyRegistering);
        mCurrentKey = null;
    }

    @Override
    public boolean onDown(final MotionEvent e) {
        final Key key = getKey(e);
        releaseCurrentKey(false /* withKeyRegistering */);
        mCurrentKey = key;
        if (key == null) {
            return false;
        }
        // Do not trigger key-down effect right now in case this is actually a fling action.
        mPendingKeyDown = new Runnable() {
            @Override
            public void run() {
                callListenerOnPressKey(key);
            }
        };
        mHandler.postDelayed(mPendingKeyDown, KEY_PRESS_DELAY_TIME);
        return false;
    }

    @Override
    public void onShowPress(final MotionEvent e) {
        // User feedback is done at {@link #onDown(MotionEvent)}.
    }

    @Override
    public boolean onSingleTapUp(final MotionEvent e) {
        final Key key = getKey(e);
        final Runnable pendingKeyDown = mPendingKeyDown;
        final Key currentKey = mCurrentKey;
        releaseCurrentKey(false /* withKeyRegistering */);
        if (key == null) {
            return false;
        }
        if (key == currentKey && pendingKeyDown != null) {
            pendingKeyDown.run();
            // Trigger key-release event a little later so that a user can see visual feedback.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    callListenerOnReleaseKey(key, true /* withRegistering */);
                }
            }, KEY_RELEASE_DELAY_TIME);
        } else {
            callListenerOnReleaseKey(key, true /* withRegistering */);
        }
        return true;
    }

    @Override
    public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX,
           final float distanceY) {
        releaseCurrentKey(false /* withKeyRegistering */);
        return false;
    }

    @Override
    public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX,
            final float velocityY) {
        releaseCurrentKey(false /* withKeyRegistering */);
        return false;
    }

    @Override
    public void onLongPress(final MotionEvent e) {
    }
}
