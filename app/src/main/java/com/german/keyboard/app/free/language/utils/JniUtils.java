package com.german.keyboard.app.free.language.utils;

import android.util.Log;

import com.german.keyboard.app.free.language.define.JniLibName;


public final class JniUtils {
    private static final String TAG = JniUtils.class.getSimpleName();

    public static boolean sHaveGestureLib = false;
    static {
        try {
            System.loadLibrary(JniLibName.JNI_LIB_NAME);
        } catch (UnsatisfiedLinkError ule) {
            Log.e(TAG, "Could not load native library " + JniLibName.JNI_LIB_NAME, ule);
        }

    }

    private JniUtils() {
        // This utility class is not publicly instantiable.
    }

    public static void loadNativeLibrary() {
        // Ensures the static initializer is called
    }
}
