package com.german.keyboard.app.free.keyboard.internal;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.util.SparseArray;

import com.german.keyboard.app.free.keyboard.PointerTracker;

public final class GestureTrailsDrawingPreview extends AbstractDrawingPreview implements Runnable {
    private final SparseArray<GestureTrailDrawingPoints> mGestureTrails = new SparseArray<>();
    private final GestureTrailDrawingParams mDrawingParams;
    private final Paint mGesturePaint;
    private int mOffscreenWidth;
    private int mOffscreenHeight;
    private int mOffscreenOffsetY;
    private Bitmap mOffscreenBuffer;
    private final Canvas mOffscreenCanvas = new Canvas();
    private final Rect mOffscreenSrcRect = new Rect();
    private final Rect mDirtyRect = new Rect();
    private final Rect mGestureTrailBoundsRect = new Rect(); // per trail

    private final Handler mDrawingHandler = new Handler();

    public GestureTrailsDrawingPreview(final TypedArray mainKeyboardViewAttr) {
        mDrawingParams = new GestureTrailDrawingParams(mainKeyboardViewAttr);
        final Paint gesturePaint = new Paint();
        gesturePaint.setAntiAlias(true);
        gesturePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        mGesturePaint = gesturePaint;
    }

    @Override
    public void setKeyboardViewGeometry(final int[] originCoords, final int width,
            final int height) {
        super.setKeyboardViewGeometry(originCoords, width, height);
        mOffscreenOffsetY = (int)(height
                * GestureStrokeRecognitionPoints.EXTRA_GESTURE_TRAIL_AREA_ABOVE_KEYBOARD_RATIO);
        mOffscreenWidth = width;
        mOffscreenHeight = mOffscreenOffsetY + height;
    }

    @Override
    public void onDeallocateMemory() {
        freeOffscreenBuffer();
    }

    private void freeOffscreenBuffer() {
        mOffscreenCanvas.setBitmap(null);
        mOffscreenCanvas.setMatrix(null);
        if (mOffscreenBuffer != null) {
            mOffscreenBuffer.recycle();
            mOffscreenBuffer = null;
        }
    }

    private void mayAllocateOffscreenBuffer() {
        if (mOffscreenBuffer != null && mOffscreenBuffer.getWidth() == mOffscreenWidth
                && mOffscreenBuffer.getHeight() == mOffscreenHeight) {
            return;
        }
        freeOffscreenBuffer();
        mOffscreenBuffer = Bitmap.createBitmap(
                mOffscreenWidth, mOffscreenHeight, Bitmap.Config.ARGB_8888);
        mOffscreenCanvas.setBitmap(mOffscreenBuffer);
        mOffscreenCanvas.translate(0, mOffscreenOffsetY);
    }

    private boolean drawGestureTrails(final Canvas offscreenCanvas, final Paint paint,
            final Rect dirtyRect) {
        if (!dirtyRect.isEmpty()) {
            paint.setColor(Color.TRANSPARENT);
            paint.setStyle(Paint.Style.FILL);
            offscreenCanvas.drawRect(dirtyRect, paint);
        }
        dirtyRect.setEmpty();
        boolean needsUpdatingGestureTrail = false;
        synchronized (mGestureTrails) {
            final int trailsCount = mGestureTrails.size();
            for (int index = 0; index < trailsCount; index++) {
                final GestureTrailDrawingPoints trail = mGestureTrails.valueAt(index);
                needsUpdatingGestureTrail |= trail.drawGestureTrail(offscreenCanvas, paint,
                        mGestureTrailBoundsRect, mDrawingParams);
                dirtyRect.union(mGestureTrailBoundsRect);
            }
        }
        return needsUpdatingGestureTrail;
    }

    @Override
    public void run() {
        invalidateDrawingView();
    }

    @Override
    public void drawPreview(final Canvas canvas) {
        if (!isPreviewEnabled()) {
            return;
        }
        mayAllocateOffscreenBuffer();
        final boolean needsUpdatingGestureTrail = drawGestureTrails(
                mOffscreenCanvas, mGesturePaint, mDirtyRect);
        if (needsUpdatingGestureTrail) {
            mDrawingHandler.removeCallbacks(this);
            mDrawingHandler.postDelayed(this, mDrawingParams.mUpdateInterval);
        }
        if (!mDirtyRect.isEmpty()) {
            mOffscreenSrcRect.set(mDirtyRect);
            mOffscreenSrcRect.offset(0, mOffscreenOffsetY);
            canvas.drawBitmap(mOffscreenBuffer, mOffscreenSrcRect, mDirtyRect, null);
        }
    }

    @Override
    public void setPreviewPosition(final PointerTracker tracker) {
        if (!isPreviewEnabled()) {
            return;
        }
        GestureTrailDrawingPoints trail;
        synchronized (mGestureTrails) {
            trail = mGestureTrails.get(tracker.mPointerId);
            if (trail == null) {
                trail = new GestureTrailDrawingPoints();
                mGestureTrails.put(tracker.mPointerId, trail);
            }
        }
        trail.addStroke(tracker.getGestureStrokeDrawingPoints(), tracker.getDownTime());

        // TODO: Should narrow the invalidate region.
        invalidateDrawingView();
    }
}
