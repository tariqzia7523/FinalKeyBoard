package com.german.keyboard.app.free.latin.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.view.inputmethod.InputMethodSubtype;

import com.german.keyboard.app.free.latin.RichInputMethodManager;
import com.german.keyboard.app.free.latin.RichInputMethodSubtype;

import javax.annotation.Nonnull;

/**
 * Utility class for managing additional features settings.
 */
@SuppressWarnings("unused")
public class AdditionalFeaturesSettingUtils {
    public static final int ADDITIONAL_FEATURES_SETTINGS_SIZE = 0;

    private AdditionalFeaturesSettingUtils() {
        // This utility class is not publicly instantiable.
    }

    public static void addAdditionalFeaturesPreferences(
            final Context context, final PreferenceFragment settingsFragment) {
        // do nothing.
    }

    public static void readAdditionalFeaturesPreferencesIntoArray(final Context context,
            final SharedPreferences prefs, final int[] additionalFeaturesPreferences) {
        // do nothing.
    }

    @Nonnull
    public static RichInputMethodSubtype createRichInputMethodSubtype(
            @Nonnull final RichInputMethodManager imm,
            @Nonnull final InputMethodSubtype subtype,
            final Context context) {
        return new RichInputMethodSubtype(subtype);
    }
}
