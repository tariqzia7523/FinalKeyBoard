package com.german.keyboard.app.free.keyboard.internal;

import com.german.keyboard.app.free.latin.common.Constants;
import com.german.keyboard.app.free.latin.common.InputPointers;

public class BatchInputArbiter {
    public interface BatchInputArbiterListener {
        void onStartBatchInput();
        void onUpdateBatchInput(
                final InputPointers aggregatedPointers, final long moveEventTime);
        void onStartUpdateBatchInputTimer();
        void onEndBatchInput(final InputPointers aggregatedPointers, final long upEventTime);
    }

    private static long sGestureFirstDownTime;
    private static final InputPointers sAggregatedPointers = new InputPointers(
            Constants.DEFAULT_GESTURE_POINTS_CAPACITY);
    private static int sLastRecognitionPointSize = 0; // synchronized using sAggregatedPointers
    private static long sLastRecognitionTime = 0; // synchronized using sAggregatedPointers

    private final GestureStrokeRecognitionPoints mRecognitionPoints;

    public BatchInputArbiter(final int pointerId, final GestureStrokeRecognitionParams params) {
        mRecognitionPoints = new GestureStrokeRecognitionPoints(pointerId, params);
    }

    public void setKeyboardGeometry(final int keyWidth, final int keyboardHeight) {
        mRecognitionPoints.setKeyboardGeometry(keyWidth, keyboardHeight);
    }

    public int getElapsedTimeSinceFirstDown(final long eventTime) {
        return (int)(eventTime - sGestureFirstDownTime);
    }

    public void addDownEventPoint(final int x, final int y, final long downEventTime,
            final long lastLetterTypingTime, final int activePointerCount) {
        if (activePointerCount == 1) {
            sGestureFirstDownTime = downEventTime;
        }
        final int elapsedTimeSinceFirstDown = getElapsedTimeSinceFirstDown(downEventTime);
        final int elapsedTimeSinceLastTyping = (int)(downEventTime - lastLetterTypingTime);
        mRecognitionPoints.addDownEventPoint(
                x, y, elapsedTimeSinceFirstDown, elapsedTimeSinceLastTyping);
    }

    public boolean addMoveEventPoint(final int x, final int y, final long moveEventTime,
            final boolean isMajorEvent, final BatchInputArbiterListener listener) {
        final int beforeLength = mRecognitionPoints.getLength();
        final boolean onValidArea = mRecognitionPoints.addEventPoint(
                x, y, getElapsedTimeSinceFirstDown(moveEventTime), isMajorEvent);
        if (mRecognitionPoints.getLength() > beforeLength) {
            listener.onStartUpdateBatchInputTimer();
        }
        return onValidArea;
    }

    public boolean mayStartBatchInput(final BatchInputArbiterListener listener) {
        if (!mRecognitionPoints.isStartOfAGesture()) {
            return false;
        }
        synchronized (sAggregatedPointers) {
            sAggregatedPointers.reset();
            sLastRecognitionPointSize = 0;
            sLastRecognitionTime = 0;
            listener.onStartBatchInput();
        }
        return true;
    }

    public void updateBatchInputByTimer(final long syntheticMoveEventTime,
            final BatchInputArbiterListener listener) {
        mRecognitionPoints.duplicateLastPointWith(
                getElapsedTimeSinceFirstDown(syntheticMoveEventTime));
        updateBatchInput(syntheticMoveEventTime, listener);
    }

    public void updateBatchInput(final long moveEventTime,
            final BatchInputArbiterListener listener) {
        synchronized (sAggregatedPointers) {
            mRecognitionPoints.appendIncrementalBatchPoints(sAggregatedPointers);
            final int size = sAggregatedPointers.getPointerSize();
            if (size > sLastRecognitionPointSize && mRecognitionPoints.hasRecognitionTimePast(
                    moveEventTime, sLastRecognitionTime)) {
                listener.onUpdateBatchInput(sAggregatedPointers, moveEventTime);
                listener.onStartUpdateBatchInputTimer();
                sLastRecognitionPointSize = sAggregatedPointers.getPointerSize();
                sLastRecognitionTime = moveEventTime;
            }
        }
    }

    public boolean mayEndBatchInput(final long upEventTime, final int activePointerCount,
            final BatchInputArbiterListener listener) {
        synchronized (sAggregatedPointers) {
            mRecognitionPoints.appendAllBatchPoints(sAggregatedPointers);
            if (activePointerCount == 1) {
                listener.onEndBatchInput(sAggregatedPointers, upEventTime);
                return true;
            }
        }
        return false;
    }
}
