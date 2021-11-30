package com.german.keyboard.app.free.latin.utils;

import android.content.Intent;
import android.text.TextUtils;

public final class IntentUtils {
    private static final String EXTRA_INPUT_METHOD_ID = "input_method_id";
    // TODO: Can these be constants instead of literal String constants?
    private static final String INPUT_METHOD_SUBTYPE_SETTINGS =
            "android.settings.INPUT_METHOD_SUBTYPE_SETTINGS";

    private IntentUtils() {
        // This utility class is not publicly instantiable.
    }

    public static Intent getInputLanguageSelectionIntent(final String inputMethodId,
            final int flagsForSubtypeSettings) {
        // Refer to android.provider.Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS
        final String action = INPUT_METHOD_SUBTYPE_SETTINGS;
        final Intent intent = new Intent(action);
        if (!TextUtils.isEmpty(inputMethodId)) {
            intent.putExtra(EXTRA_INPUT_METHOD_ID, inputMethodId);
        }
        if (flagsForSubtypeSettings > 0) {
            intent.setFlags(flagsForSubtypeSettings);
        }
        return intent;
    }
}
