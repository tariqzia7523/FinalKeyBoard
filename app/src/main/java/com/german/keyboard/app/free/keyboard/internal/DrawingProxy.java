package com.german.keyboard.app.free.keyboard.internal;

import com.german.keyboard.app.free.keyboard.Key;
import com.german.keyboard.app.free.keyboard.MoreKeysPanel;
import com.german.keyboard.app.free.keyboard.PointerTracker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DrawingProxy {
    void onKeyPressed(@Nonnull Key key, boolean withPreview);
    void onKeyReleased(@Nonnull Key key, boolean withAnimation);

    @Nullable
    MoreKeysPanel showMoreKeysKeyboard(@Nonnull Key key, @Nonnull PointerTracker tracker);

    void startWhileTypingAnimation(int fadeInOrOut);
    int FADE_IN = 0;
    int FADE_OUT = 1;

    void showSlidingKeyInputPreview(@Nullable PointerTracker tracker);

    void showGestureTrail(@Nonnull PointerTracker tracker, boolean showsFloatingPreviewText);

    void dismissGestureFloatingPreviewTextWithoutDelay();
}
