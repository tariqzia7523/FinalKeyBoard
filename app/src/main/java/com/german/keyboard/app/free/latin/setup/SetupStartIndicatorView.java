package com.german.keyboard.app.free.latin.setup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import com.german.keyboard.app.free.R;
public final class SetupStartIndicatorView extends LinearLayout {
    public SetupStartIndicatorView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.setup_start_indicator_label, this);

    }

    @SuppressLint("AppCompatCustomView")
    public static final class LabelView extends TextView {
        private View mIndicatorView;

        public LabelView(final Context context, final AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void drawableStateChanged() {
            super.drawableStateChanged();
            for (final int state : getDrawableState()) {
                if (state == android.R.attr.state_pressed) {
                    updateIndicatorView(true /* pressed */);
                    return;
                }
            }
            updateIndicatorView(false /* pressed */);
        }

        private void updateIndicatorView(final boolean pressed) {
            if (mIndicatorView != null) {
                mIndicatorView.setPressed(pressed);
                mIndicatorView.invalidate();
            }
        }
    }

    public static final class IndicatorView extends View {
        private final Path mIndicatorPath = new Path();
        private final Paint mIndicatorPaint = new Paint();
        private final ColorStateList mIndicatorColor;

        public IndicatorView(final Context context, final AttributeSet attrs) {
            super(context, attrs);
            mIndicatorColor = getResources().getColorStateList(
                    R.color.setup_step_action_background);
            mIndicatorPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        protected void onDraw(final Canvas canvas) {
            super.onDraw(canvas);
            final int layoutDirection = ViewCompat.getLayoutDirection(this);
            final int width = getWidth();
            final int height = getHeight();
            final float halfHeight = height / 2.0f;
            final Path path = mIndicatorPath;
            path.rewind();
            if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL) {
                // Left arrow
                path.moveTo(width, 0.0f);
                path.lineTo(0.0f, halfHeight);
                path.lineTo(width, height);
            } else { // LAYOUT_DIRECTION_LTR
                // Right arrow
                path.moveTo(0.0f, 0.0f);
                path.lineTo(width, halfHeight);
                path.lineTo(0.0f, height);
            }
            path.close();
            final int[] stateSet = getDrawableState();
            final int color = mIndicatorColor.getColorForState(stateSet, 0);
            mIndicatorPaint.setColor(color);
            canvas.drawPath(path, mIndicatorPaint);
        }
    }
}
