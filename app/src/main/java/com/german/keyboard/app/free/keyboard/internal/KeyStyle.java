package com.german.keyboard.app.free.keyboard.internal;

import android.content.res.TypedArray;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class KeyStyle {
    private final KeyboardTextsSet mTextsSet;

    public abstract @Nullable String[] getStringArray(TypedArray a, int index);
    public abstract @Nullable String getString(TypedArray a, int index);
    public abstract int getInt(TypedArray a, int index, int defaultValue);
    public abstract int getFlags(TypedArray a, int index);

    protected KeyStyle(@Nonnull final KeyboardTextsSet textsSet) {
        mTextsSet = textsSet;
    }

    @Nullable
    protected String parseString(final TypedArray a, final int index) {
        if (a.hasValue(index)) {
            return mTextsSet.resolveTextReference(a.getString(index));
        }
        return null;
    }

    @Nullable
    protected String[] parseStringArray(final TypedArray a, final int index) {
        if (a.hasValue(index)) {
            final String text = mTextsSet.resolveTextReference(a.getString(index));
            return MoreKeySpec.splitKeySpecs(text);
        }
        return null;
    }
}
