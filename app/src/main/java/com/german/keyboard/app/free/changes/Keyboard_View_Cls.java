package com.german.keyboard.app.free.changes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodSubtype;

import com.german.keyboard.app.free.R;

public class Keyboard_View_Cls extends KeyboardView {
    static final int KEYCODE_OPTIONS = -100;
    public static final int KEYCODE_LANGUAGE_SWITCH = -101;

    public Keyboard_View_Cls(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Keyboard_View_Cls(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        invalidateAllKeys();
    }

    Paint paint = new Paint();

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(getResources().getDimension(R.dimen.canvas_key_TextSize));
        paint.setColor(getResources().getColor(R.color.white));
    }
}