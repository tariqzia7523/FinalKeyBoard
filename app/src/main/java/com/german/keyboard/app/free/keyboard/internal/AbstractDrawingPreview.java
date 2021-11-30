package com.german.keyboard.app.free.keyboard.internal;

import android.graphics.Canvas;
import android.view.View;

import com.german.keyboard.app.free.keyboard.PointerTracker;

import javax.annotation.Nonnull;

public abstract class AbstractDrawingPreview {
    private View mDrawingView;
    private boolean mPreviewEnabled;
    private boolean mHasValidGeometry;

    public void setDrawingView(@Nonnull final DrawingPreviewPlacerView drawingView) {
        mDrawingView = drawingView;
        drawingView.addPreview(this);
    }

    protected void invalidateDrawingView() {
        if (mDrawingView != null) {
            mDrawingView.invalidate();
        }
    }

    protected final boolean isPreviewEnabled() {
        return mPreviewEnabled && mHasValidGeometry;
    }
    public final void setPreviewEnabled(final boolean enabled) {
        mPreviewEnabled = enabled;
    }
    public void setKeyboardViewGeometry(@Nonnull final int[] originCoords, final int width,
            final int height) {
        mHasValidGeometry = (width > 0 && height > 0);
    }
    public abstract void onDeallocateMemory();
    public abstract void drawPreview(@Nonnull final Canvas canvas);
    public abstract void setPreviewPosition(@Nonnull final PointerTracker tracker);
}
