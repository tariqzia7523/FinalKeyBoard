package com.german.keyboard.app.free.keyboard.internal;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.SystemClock;

import com.german.keyboard.app.free.latin.common.Constants;
import com.german.keyboard.app.free.latin.common.ResizableIntArray;

final class GestureTrailDrawingPoints {
    public static final boolean DEBUG_SHOW_POINTS = false;
    public static final int POINT_TYPE_SAMPLED = 1;
    public static final int POINT_TYPE_INTERPOLATED = 2;

    private static final int DEFAULT_CAPACITY = GestureStrokeDrawingPoints.PREVIEW_CAPACITY;

    // These three {@link ResizableIntArray}s should be synchronized by {@link #mEventTimes}.
    private final ResizableIntArray mXCoordinates = new ResizableIntArray(DEFAULT_CAPACITY);
    private final ResizableIntArray mYCoordinates = new ResizableIntArray(DEFAULT_CAPACITY);
    private final ResizableIntArray mEventTimes = new ResizableIntArray(DEFAULT_CAPACITY);
    private final ResizableIntArray mPointTypes = new ResizableIntArray(
            DEBUG_SHOW_POINTS ? DEFAULT_CAPACITY : 0);
    private int mCurrentStrokeId = -1;
    private long mCurrentTimeBase;
    private int mTrailStartIndex;
    private int mLastInterpolatedDrawIndex;

    private static final int DOWN_EVENT_MARKER = -128;

    private static int markAsDownEvent(final int xCoord) {
        return DOWN_EVENT_MARKER - xCoord;
    }

    private static boolean isDownEventXCoord(final int xCoordOrMark) {
        return xCoordOrMark <= DOWN_EVENT_MARKER;
    }

    private static int getXCoordValue(final int xCoordOrMark) {
        return isDownEventXCoord(xCoordOrMark)
                ? DOWN_EVENT_MARKER - xCoordOrMark : xCoordOrMark;
    }

    public void addStroke(final GestureStrokeDrawingPoints stroke, final long downTime) {
        synchronized (mEventTimes) {
            addStrokeLocked(stroke, downTime);
        }
    }

    private void addStrokeLocked(final GestureStrokeDrawingPoints stroke, final long downTime) {
        final int trailSize = mEventTimes.getLength();
        stroke.appendPreviewStroke(mEventTimes, mXCoordinates, mYCoordinates, mPointTypes);
        if (mEventTimes.getLength() == trailSize) {
            return;
        }
        final int[] eventTimes = mEventTimes.getPrimitiveArray();
        final int strokeId = stroke.getGestureStrokeId();

        final int lastInterpolatedIndex = (strokeId == mCurrentStrokeId)
                ? mLastInterpolatedDrawIndex : trailSize;
        mLastInterpolatedDrawIndex = stroke.interpolateStrokeAndReturnStartIndexOfLastSegment(
                lastInterpolatedIndex, mEventTimes, mXCoordinates, mYCoordinates, mPointTypes);
        if (strokeId != mCurrentStrokeId) {
            final int elapsedTime = (int)(downTime - mCurrentTimeBase);
            for (int i = mTrailStartIndex; i < trailSize; i++) {
                eventTimes[i] -= elapsedTime;
            }
            final int[] xCoords = mXCoordinates.getPrimitiveArray();
            final int downIndex = trailSize;
            xCoords[downIndex] = markAsDownEvent(xCoords[downIndex]);
            mCurrentTimeBase = downTime - eventTimes[downIndex];
            mCurrentStrokeId = strokeId;
        }
    }

    private static int getAlpha(final int elapsedTime, final GestureTrailDrawingParams params) {
        if (elapsedTime < params.mFadeoutStartDelay) {
            return Constants.Color.ALPHA_OPAQUE;
        }
        final int decreasingAlpha = Constants.Color.ALPHA_OPAQUE
                * (elapsedTime - params.mFadeoutStartDelay)
                / params.mFadeoutDuration;
        return Constants.Color.ALPHA_OPAQUE - decreasingAlpha;
    }

    private static float getWidth(final int elapsedTime, final GestureTrailDrawingParams params) {
        final float deltaWidth = params.mTrailStartWidth - params.mTrailEndWidth;
        return params.mTrailStartWidth - (deltaWidth * elapsedTime) / params.mTrailLingerDuration;
    }

    private final RoundedLine mRoundedLine = new RoundedLine();
    private final Rect mRoundedLineBounds = new Rect();

    public boolean drawGestureTrail(final Canvas canvas, final Paint paint,
            final Rect outBoundsRect, final GestureTrailDrawingParams params) {
        synchronized (mEventTimes) {
            return drawGestureTrailLocked(canvas, paint, outBoundsRect, params);
        }
    }

    private boolean drawGestureTrailLocked(final Canvas canvas, final Paint paint,
            final Rect outBoundsRect, final GestureTrailDrawingParams params) {
        outBoundsRect.setEmpty();
        final int trailSize = mEventTimes.getLength();
        if (trailSize == 0) {
            return false;
        }

        final int[] eventTimes = mEventTimes.getPrimitiveArray();
        final int[] xCoords = mXCoordinates.getPrimitiveArray();
        final int[] yCoords = mYCoordinates.getPrimitiveArray();
        final int[] pointTypes = mPointTypes.getPrimitiveArray();
        final int sinceDown = (int)(SystemClock.uptimeMillis() - mCurrentTimeBase);
        int startIndex;
        for (startIndex = mTrailStartIndex; startIndex < trailSize; startIndex++) {
            final int elapsedTime = sinceDown - eventTimes[startIndex];
            if (elapsedTime < params.mTrailLingerDuration) {
                break;
            }
        }
        mTrailStartIndex = startIndex;

        if (startIndex < trailSize) {
            paint.setColor(params.mTrailColor);
            paint.setStyle(Paint.Style.FILL);
            final RoundedLine roundedLine = mRoundedLine;
            int p1x = getXCoordValue(xCoords[startIndex]);
            int p1y = yCoords[startIndex];
            final int lastTime = sinceDown - eventTimes[startIndex];
            float r1 = getWidth(lastTime, params) / 2.0f;
            for (int i = startIndex + 1; i < trailSize; i++) {
                final int elapsedTime = sinceDown - eventTimes[i];
                final int p2x = getXCoordValue(xCoords[i]);
                final int p2y = yCoords[i];
                final float r2 = getWidth(elapsedTime, params) / 2.0f;
                if (!isDownEventXCoord(xCoords[i])) {
                    final float body1 = r1 * params.mTrailBodyRatio;
                    final float body2 = r2 * params.mTrailBodyRatio;
                    final Path path = roundedLine.makePath(p1x, p1y, body1, p2x, p2y, body2);
                    if (!path.isEmpty()) {
                        roundedLine.getBounds(mRoundedLineBounds);
                        if (params.mTrailShadowEnabled) {
                            final float shadow2 = r2 * params.mTrailShadowRatio;
                            paint.setShadowLayer(shadow2, 0.0f, 0.0f, params.mTrailColor);
                            final int shadowInset = -(int)Math.ceil(shadow2);
                            mRoundedLineBounds.inset(shadowInset, shadowInset);
                        }
                        // Take union for the bounds.
                        outBoundsRect.union(mRoundedLineBounds);
                        final int alpha = getAlpha(elapsedTime, params);
                        paint.setAlpha(alpha);
                        canvas.drawPath(path, paint);
                    }
                }
                p1x = p2x;
                p1y = p2y;
                r1 = r2;
            }
            if (DEBUG_SHOW_POINTS) {
                debugDrawPoints(canvas, startIndex, trailSize, paint);
            }
        }

        final int newSize = trailSize - startIndex;
        if (newSize < startIndex) {
            mTrailStartIndex = 0;
            if (newSize > 0) {
                System.arraycopy(eventTimes, startIndex, eventTimes, 0, newSize);
                System.arraycopy(xCoords, startIndex, xCoords, 0, newSize);
                System.arraycopy(yCoords, startIndex, yCoords, 0, newSize);
                if (DEBUG_SHOW_POINTS) {
                    System.arraycopy(pointTypes, startIndex, pointTypes, 0, newSize);
                }
            }
            mEventTimes.setLength(newSize);
            mXCoordinates.setLength(newSize);
            mYCoordinates.setLength(newSize);
            if (DEBUG_SHOW_POINTS) {
                mPointTypes.setLength(newSize);
            }
            mLastInterpolatedDrawIndex = Math.max(mLastInterpolatedDrawIndex - startIndex, 0);
        }
        return newSize > 0;
    }

    private void debugDrawPoints(final Canvas canvas, final int startIndex, final int endIndex,
            final Paint paint) {
        final int[] xCoords = mXCoordinates.getPrimitiveArray();
        final int[] yCoords = mYCoordinates.getPrimitiveArray();
        final int[] pointTypes = mPointTypes.getPrimitiveArray();
        paint.setAntiAlias(false);
        paint.setStrokeWidth(0);
        for (int i = startIndex; i < endIndex; i++) {
            final int pointType = pointTypes[i];
            if (pointType == POINT_TYPE_INTERPOLATED) {
                paint.setColor(Color.RED);
            } else if (pointType == POINT_TYPE_SAMPLED) {
                paint.setColor(0xFFA000FF);
            } else {
                paint.setColor(Color.GREEN);
            }
            canvas.drawPoint(getXCoordValue(xCoords[i]), yCoords[i], paint);
        }
        paint.setAntiAlias(true);
    }
}
