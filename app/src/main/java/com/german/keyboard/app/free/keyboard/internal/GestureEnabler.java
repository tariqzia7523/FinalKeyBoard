package com.german.keyboard.app.free.keyboard.internal;

import com.german.keyboard.app.free.inputmethod.accessibility.AccessibilityUtils;

public final class GestureEnabler {
    private boolean mShouldHandleGesture;
    private boolean mMainDictionaryAvailable;
    private boolean mGestureHandlingEnabledByInputField;
    private boolean mGestureHandlingEnabledByUser;

    private void updateGestureHandlingMode() {
        mShouldHandleGesture = mMainDictionaryAvailable
                && mGestureHandlingEnabledByInputField
                && mGestureHandlingEnabledByUser
                && !AccessibilityUtils.Companion.getInstance().isTouchExplorationEnabled();
    }

    public void setMainDictionaryAvailability(final boolean mainDictionaryAvailable) {
        mMainDictionaryAvailable = mainDictionaryAvailable;
        updateGestureHandlingMode();
    }

    public void setGestureHandlingEnabledByUser(final boolean gestureHandlingEnabledByUser) {
        mGestureHandlingEnabledByUser = gestureHandlingEnabledByUser;
        updateGestureHandlingMode();
    }

    public void setPasswordMode(final boolean passwordMode) {
        mGestureHandlingEnabledByInputField = !passwordMode;
        updateGestureHandlingMode();
    }

    public boolean shouldHandleGesture() {
        return mShouldHandleGesture;
    }
}
