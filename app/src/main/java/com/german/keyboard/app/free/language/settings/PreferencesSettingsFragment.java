package com.german.keyboard.app.free.language.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;

import com.german.keyboard.app.free.language.AudioAndHapticFeedbackManager;
import com.german.keyboard.app.free.R;
import com.german.keyboard.app.free.language.RichInputMethodManager;

/**
 * "Preferences" settings sub screen.
 *
 * This settings sub screen handles the following input preferences.
 * - Auto-capitalization
 * - Double-space period
 * - Vibrate on keypress
 * - Sound on keypress
 * - Popup on keypress
 * - Voice input key
 */
public final class PreferencesSettingsFragment extends SubScreenFragment {

    private static final boolean VOICE_IME_ENABLED =
            true;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs_screen_preferences);

        final Resources res = getResources();
        final Context context = getActivity();

        // When we are called from the Settings application but we are not already running, some
        // singleton and utility classes may not have been initialized.  We have to call
        // initialization method of these classes here. See {@link LatinIME#onCreate()}.
        RichInputMethodManager.init(context);

        final boolean showVoiceKeyOption = res.getBoolean(
                R.bool.config_enable_show_voice_key_option);
        if (!showVoiceKeyOption) {
            removePreference(Settings.PREF_VOICE_INPUT_KEY);
        }
        if (!AudioAndHapticFeedbackManager.getInstance().hasVibrator()) {
            removePreference(Settings.PREF_VIBRATE_ON);
        }
        if (!Settings.readFromBuildConfigIfToShowKeyPreviewPopupOption(res)) {
            removePreference(Settings.PREF_POPUP_ON);
        }

        refreshEnablingsOfKeypressSoundAndVibrationSettings();
    }

    @Override
    public void onResume() {
        super.onResume();
        final Preference voiceInputKeyOption = findPreference(Settings.PREF_VOICE_INPUT_KEY);
        if (voiceInputKeyOption != null) {
            RichInputMethodManager.getInstance().refreshSubtypeCaches();
            voiceInputKeyOption.setEnabled(VOICE_IME_ENABLED);
            voiceInputKeyOption.setSummary(VOICE_IME_ENABLED
                    ? null : getText(R.string.voice_input_disabled_summary));
        }
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences prefs, final String key) {
        refreshEnablingsOfKeypressSoundAndVibrationSettings();
    }

    private void refreshEnablingsOfKeypressSoundAndVibrationSettings() {
        final SharedPreferences prefs = getSharedPreferences();
        final Resources res = getResources();
        setPreferenceEnabled(Settings.PREF_VIBRATION_DURATION_SETTINGS,
                Settings.readVibrationEnabled(prefs, res));
        setPreferenceEnabled(Settings.PREF_KEYPRESS_SOUND_VOLUME,
                Settings.readKeypressSoundEnabled(prefs, res));
    }
}
