package com.german.keyboard.app.free.keyboard.internal;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import com.german.keyboard.app.free.R;
import com.german.keyboard.app.free.latin.common.Constants;
import com.german.keyboard.app.free.latin.define.DebugFlags;

public final class BogusMoveEventDetector {
    private static final String TAG = BogusMoveEventDetector.class.getSimpleName();
    private static final boolean DEBUG_MODE = DebugFlags.DEBUG_ENABLED;

    private static final float BOGUS_MOVE_ACCUMULATED_DISTANCE_THRESHOLD = 0.53f;
    private static final float BOGUS_MOVE_RADIUS_THRESHOLD = 1.14f;

    private static boolean sNeedsProximateBogusDownMoveUpEventHack;

    public static void init(final Resources res) {
        final int screenMetrics = res.getInteger(R.integer.config_screen_metrics);
        final boolean isLargeTablet = (screenMetrics == Constants.SCREEN_METRICS_LARGE_TABLET);
        final boolean isSmallTablet = (screenMetrics == Constants.SCREEN_METRICS_SMALL_TABLET);
        final int densityDpi = res.getDisplayMetrics().densityDpi;
        final boolean hasLowDensityScreen = (densityDpi < DisplayMetrics.DENSITY_HIGH);
        final boolean needsTheHack = isLargeTablet || (isSmallTablet && hasLowDensityScreen);
        if (DEBUG_MODE) {
            final int sw = res.getConfiguration().smallestScreenWidthDp;
            Log.d(TAG, "needsProximateBogusDownMoveUpEventHack=" + needsTheHack
                    + " smallestScreenWidthDp=" + sw + " densityDpi=" + densityDpi
                    + " screenMetrics=" + screenMetrics);
        }
        sNeedsProximateBogusDownMoveUpEventHack = needsTheHack;
    }

    private int mAccumulatedDistanceThreshold;
    private int mRadiusThreshold;

    /* package */ int mAccumulatedDistanceFromDownKey;
    private int mActualDownX;
    private int mActualDownY;

    public void setKeyboardGeometry(final int keyWidth, final int keyHeight) {
        final float keyDiagonal = (float)Math.hypot(keyWidth, keyHeight);
        mAccumulatedDistanceThreshold = (int)(
                keyDiagonal * BOGUS_MOVE_ACCUMULATED_DISTANCE_THRESHOLD);
        mRadiusThreshold = (int)(keyDiagonal * BOGUS_MOVE_RADIUS_THRESHOLD);
    }

    public void onActualDownEvent(final int x, final int y) {
        mActualDownX = x;
        mActualDownY = y;
    }

    public void onDownKey() {
        mAccumulatedDistanceFromDownKey = 0;
    }

    public void onMoveKey(final int distance) {
        mAccumulatedDistanceFromDownKey += distance;
    }

    public boolean hasTraveledLongDistance(final int x, final int y) {
        if (!sNeedsProximateBogusDownMoveUpEventHack) {
            return false;
        }
        final int dx = Math.abs(x - mActualDownX);
        final int dy = Math.abs(y - mActualDownY);
        return dx >= dy && mAccumulatedDistanceFromDownKey >= mAccumulatedDistanceThreshold;
    }

    public int getAccumulatedDistanceFromDownKey() {
        return mAccumulatedDistanceFromDownKey;
    }

    public int getDistanceFromDownEvent(final int x, final int y) {
        return getDistance(x, y, mActualDownX, mActualDownY);
    }

    private static int getDistance(final int x1, final int y1, final int x2, final int y2) {
        return (int)Math.hypot(x1 - x2, y1 - y2);
    }

    public boolean isCloseToActualDownEvent(final int x, final int y) {
        return sNeedsProximateBogusDownMoveUpEventHack
                && getDistanceFromDownEvent(x, y) < mRadiusThreshold;
    }
}
