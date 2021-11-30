package com.german.keyboard.app.free.keyboard.internal;

import android.content.res.TypedArray;

import com.german.keyboard.app.free.R;
import com.german.keyboard.app.free.latin.utils.ResourceUtils;

public final class GestureStrokeRecognitionParams {
    public final int mStaticTimeThresholdAfterFastTyping;
    public final float mDetectFastMoveSpeedThreshold;
    public final int mDynamicThresholdDecayDuration;
    public final int mDynamicTimeThresholdFrom;
    public final int mDynamicTimeThresholdTo;
    public final float mDynamicDistanceThresholdFrom;
    public final float mDynamicDistanceThresholdTo;
    public final float mSamplingMinimumDistance;
    public final int mRecognitionMinimumTime;
    public final float mRecognitionSpeedThreshold;

    public static final GestureStrokeRecognitionParams DEFAULT =
            new GestureStrokeRecognitionParams();

    private GestureStrokeRecognitionParams() {
        mStaticTimeThresholdAfterFastTyping = 350;
        mDetectFastMoveSpeedThreshold = 1.5f;
        mDynamicThresholdDecayDuration = 450;
        mDynamicTimeThresholdFrom = 300;
        mDynamicTimeThresholdTo = 20;
        mDynamicDistanceThresholdFrom = 6.0f;
        mDynamicDistanceThresholdTo = 0.35f;
        mSamplingMinimumDistance = 1.0f / 6.0f;
        mRecognitionMinimumTime = 100;
        mRecognitionSpeedThreshold = 5.5f;
    }

    public GestureStrokeRecognitionParams(final TypedArray mainKeyboardViewAttr) {
        mStaticTimeThresholdAfterFastTyping = mainKeyboardViewAttr.getInt(
                R.styleable.MainKeyboardView_gestureStaticTimeThresholdAfterFastTyping,
                DEFAULT.mStaticTimeThresholdAfterFastTyping);
        mDetectFastMoveSpeedThreshold = ResourceUtils.getFraction(mainKeyboardViewAttr,
                R.styleable.MainKeyboardView_gestureDetectFastMoveSpeedThreshold,
                DEFAULT.mDetectFastMoveSpeedThreshold);
        mDynamicThresholdDecayDuration = mainKeyboardViewAttr.getInt(
                R.styleable.MainKeyboardView_gestureDynamicThresholdDecayDuration,
                DEFAULT.mDynamicThresholdDecayDuration);
        mDynamicTimeThresholdFrom = mainKeyboardViewAttr.getInt(
                R.styleable.MainKeyboardView_gestureDynamicTimeThresholdFrom,
                DEFAULT.mDynamicTimeThresholdFrom);
        mDynamicTimeThresholdTo = mainKeyboardViewAttr.getInt(
                R.styleable.MainKeyboardView_gestureDynamicTimeThresholdTo,
                DEFAULT.mDynamicTimeThresholdTo);
        mDynamicDistanceThresholdFrom = ResourceUtils.getFraction(mainKeyboardViewAttr,
                R.styleable.MainKeyboardView_gestureDynamicDistanceThresholdFrom,
                DEFAULT.mDynamicDistanceThresholdFrom);
        mDynamicDistanceThresholdTo = ResourceUtils.getFraction(mainKeyboardViewAttr,
                R.styleable.MainKeyboardView_gestureDynamicDistanceThresholdTo,
                DEFAULT.mDynamicDistanceThresholdTo);
        mSamplingMinimumDistance = ResourceUtils.getFraction(mainKeyboardViewAttr,
                R.styleable.MainKeyboardView_gestureSamplingMinimumDistance,
                DEFAULT.mSamplingMinimumDistance);
        mRecognitionMinimumTime = mainKeyboardViewAttr.getInt(
                R.styleable.MainKeyboardView_gestureRecognitionMinimumTime,
                DEFAULT.mRecognitionMinimumTime);
        mRecognitionSpeedThreshold = ResourceUtils.getFraction(mainKeyboardViewAttr,
                R.styleable.MainKeyboardView_gestureRecognitionSpeedThreshold,
                DEFAULT.mRecognitionSpeedThreshold);
    }
}
