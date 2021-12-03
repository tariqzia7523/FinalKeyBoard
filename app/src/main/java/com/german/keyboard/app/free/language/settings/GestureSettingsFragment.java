package com.german.keyboard.app.free.language.settings;

import android.os.Bundle;

import com.german.keyboard.app.free.R;

/**
 * "Gesture typing preferences" settings sub screen.
 *
 * This settings sub screen handles the following gesture typing preferences.
 * - Enable gesture typing
 * - Dynamic floating preview
 * - Show gesture trail
 * - Phrase gesture
 */
public final class GestureSettingsFragment extends SubScreenFragment {
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs_screen_gesture);
    }
}
