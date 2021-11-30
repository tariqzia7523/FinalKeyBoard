package com.german.keyboard.app.free.keyboard.internal;

import android.content.res.TypedArray;

import com.german.keyboard.app.free.R;

public final class GestureStrokeDrawingParams {
    public final double mMinSamplingDistance; // in pixel
    public final double mMaxInterpolationAngularThreshold; // in radian
    public final double mMaxInterpolationDistanceThreshold; // in pixel
    public final int mMaxInterpolationSegments;

    private static final float DEFAULT_MIN_SAMPLING_DISTANCE = 0.0f; // dp
    private static final int DEFAULT_MAX_INTERPOLATION_ANGULAR_THRESHOLD = 15; // in degree
    private static final float DEFAULT_MAX_INTERPOLATION_DISTANCE_THRESHOLD = 0.0f; // dp
    private static final int DEFAULT_MAX_INTERPOLATION_SEGMENTS = 4;

    public GestureStrokeDrawingParams(final TypedArray mainKeyboardViewAttr) {
        mMinSamplingDistance = mainKeyboardViewAttr.getDimension(
                R.styleable.MainKeyboardView_gestureTrailMinSamplingDistance,
                DEFAULT_MIN_SAMPLING_DISTANCE);
        final int interpolationAngularDegree = mainKeyboardViewAttr.getInteger(R.styleable
                .MainKeyboardView_gestureTrailMaxInterpolationAngularThreshold, 0);
        mMaxInterpolationAngularThreshold = (interpolationAngularDegree <= 0)
                ? Math.toRadians(DEFAULT_MAX_INTERPOLATION_ANGULAR_THRESHOLD)
                : Math.toRadians(interpolationAngularDegree);
        mMaxInterpolationDistanceThreshold = mainKeyboardViewAttr.getDimension(R.styleable
                .MainKeyboardView_gestureTrailMaxInterpolationDistanceThreshold,
                DEFAULT_MAX_INTERPOLATION_DISTANCE_THRESHOLD);
        mMaxInterpolationSegments = mainKeyboardViewAttr.getInteger(
                R.styleable.MainKeyboardView_gestureTrailMaxInterpolationSegments,
                DEFAULT_MAX_INTERPOLATION_SEGMENTS);
    }
}
