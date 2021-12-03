package com.german.keyboard.app.free.language.utils;

import android.content.Context;

import com.german.keyboard.app.free.inputmethod.annotations.UsedForTesting;

public class ManagedProfileUtils {
    private static ManagedProfileUtils INSTANCE = new ManagedProfileUtils();
    private static ManagedProfileUtils sTestInstance;

    private ManagedProfileUtils() {
        // This utility class is not publicly instantiable.
    }

    @UsedForTesting
    public static void setTestInstance(final ManagedProfileUtils testInstance) {
        sTestInstance = testInstance;
    }

    public static ManagedProfileUtils getInstance() {
        return sTestInstance == null ? INSTANCE : sTestInstance;
    }

    public boolean hasWorkProfile(final Context context) {
        return false;
    }
}