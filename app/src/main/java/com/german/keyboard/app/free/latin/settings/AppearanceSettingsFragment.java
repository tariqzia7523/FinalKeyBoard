package com.german.keyboard.app.free.latin.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Locale;

import com.german.keyboard.app.free.R;
import com.german.keyboard.app.free.latin.common.Constants;
import com.german.keyboard.app.free.latin.define.ProductionFlags;

/**
 * "Appearance" settings sub screen.
 */
public final class AppearanceSettingsFragment extends SubScreenFragment {
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs_screen_appearance);
        if (!ProductionFlags.IS_SPLIT_KEYBOARD_SUPPORTED ||
                Constants.isPhone(Settings.readScreenMetrics(getResources()))) {
            removePreference(Settings.PREF_ENABLE_SPLIT_KEYBOARD);
        }
        setupKeyboardHeight(
                Settings.PREF_KEYBOARD_HEIGHT_SCALE, SettingsValues.DEFAULT_SIZE_SCALE);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setupKeyboardHeight(final String prefKey, final float defaultValue) {
        final SharedPreferences prefs = getSharedPreferences();
        final SeekBarDialogPreference pref = (SeekBarDialogPreference)findPreference(prefKey);
        if (pref == null) {
            return;
        }
        pref.setInterface(new SeekBarDialogPreference.ValueProxy() {
            private static final float PERCENTAGE_FLOAT = 100.0f;
            private float getValueFromPercentage(final int percentage) {
                return percentage / PERCENTAGE_FLOAT;
            }

            private int getPercentageFromValue(final float floatValue) {
                return (int)(floatValue * PERCENTAGE_FLOAT);
            }

            @Override
            public void writeValue(final int value, final String key) {
                prefs.edit().putFloat(key, getValueFromPercentage(value)).apply();
            }

            @Override
            public void writeDefaultValue(final String key) {
                prefs.edit().remove(key).apply();
            }

            @Override
            public int readValue(final String key) {
                return getPercentageFromValue(Settings.readKeyboardHeight(prefs, defaultValue));
            }

            @Override
            public int readDefaultValue(final String key) {
                return getPercentageFromValue(defaultValue);
            }

            @Override
            public String getValueText(final int value) {
                return String.format(Locale.ROOT, "%d%%", value);
            }

            @Override
            public void feedbackValue(final int value) {}
        });
    }
}
