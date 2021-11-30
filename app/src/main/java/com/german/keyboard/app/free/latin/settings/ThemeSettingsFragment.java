package com.german.keyboard.app.free.latin.settings;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.german.keyboard.app.free.keyboard.KeyboardTheme;
import com.german.keyboard.app.free.R;

/**
 * "Keyboard theme" settings sub screen.
 */
public final class ThemeSettingsFragment extends SubScreenFragment
        implements RadioButtonPreference.OnRadioButtonClickedListener {
    private int mSelectedThemeId;

    static class KeyboardThemePreference extends RadioButtonPreference {
        final int mThemeId;

        KeyboardThemePreference(final Context context, final String name, final int id) {
            super(context);
            setTitle(name);
            mThemeId = id;
        }
    }



    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs_screen_theme);
        final PreferenceScreen screen = getPreferenceScreen();
        final Context context = getActivity();
        final Resources res = getResources();
        final String[] keyboardThemeNames = res.getStringArray(R.array.keyboard_theme_names);
        final int[] keyboardThemeIds = res.getIntArray(R.array.keyboard_theme_ids);
        for (int index = 0; index < keyboardThemeNames.length; index++) {
            final KeyboardThemePreference pref = new KeyboardThemePreference(
                    context, keyboardThemeNames[index], keyboardThemeIds[index]);
            screen.addPreference(pref);
            pref.setOnRadioButtonClickedListener(this);
        }
        final KeyboardTheme keyboardTheme = KeyboardTheme.getKeyboardTheme(context);
        mSelectedThemeId = keyboardTheme.mThemeId;
    }

    @Override
    public void onRadioButtonClicked(final RadioButtonPreference preference) {
        if (preference instanceof KeyboardThemePreference) {
            final KeyboardThemePreference pref = (KeyboardThemePreference)preference;
            mSelectedThemeId = pref.mThemeId;
            updateSelected();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSelected();
    }

    @Override
    public void onPause() {
        super.onPause();
        KeyboardTheme.saveKeyboardThemeId(mSelectedThemeId, getSharedPreferences());
    }

    private void updateSelected() {
        final PreferenceScreen screen = getPreferenceScreen();
        final int count = screen.getPreferenceCount();
        for (int index = 0; index < count; index++) {
            final Preference preference = screen.getPreference(index);
            if (preference instanceof KeyboardThemePreference) {
                final KeyboardThemePreference pref = (KeyboardThemePreference)preference;
                final boolean selected = (mSelectedThemeId == pref.mThemeId);
                pref.setSelected(selected);
            }
        }
    }
}
