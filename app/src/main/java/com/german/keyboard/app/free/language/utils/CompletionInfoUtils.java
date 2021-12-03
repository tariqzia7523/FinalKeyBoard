package com.german.keyboard.app.free.language.utils;

import android.text.TextUtils;
import android.view.inputmethod.CompletionInfo;

import java.util.Arrays;

/**
 * Utilities to do various stuff with CompletionInfo.
 */
public class CompletionInfoUtils {
    private CompletionInfoUtils() {
        // This utility class is not publicly instantiable.
    }

    public static CompletionInfo[] removeNulls(final CompletionInfo[] src) {
        int j = 0;
        final CompletionInfo[] dst = new CompletionInfo[src.length];
        for (int i = 0; i < src.length; ++i) {
            if (null != src[i] && !TextUtils.isEmpty(src[i].getText())) {
                dst[j] = src[i];
                ++j;
            }
        }
        return Arrays.copyOfRange(dst, 0, j);
    }
}
