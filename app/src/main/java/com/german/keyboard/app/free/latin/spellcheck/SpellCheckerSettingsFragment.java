

package com.german.keyboard.app.free.latin.spellcheck;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.TextUtils;

import com.german.keyboard.app.free.R;
import com.german.keyboard.app.free.latin.settings.SubScreenFragment;
import com.german.keyboard.app.free.latin.settings.TwoStatePreferenceHelper;
import com.german.keyboard.app.free.latin.utils.ApplicationUtils;

/**
 * Preference screen.
 */
public final class SpellCheckerSettingsFragment extends SubScreenFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener
        {

    private SwitchPreference mLookupContactsPreference;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.spell_checker_settings);
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.setTitle(ApplicationUtils.getActivityTitleResId(
                getActivity(), SpellCheckerSettingsActivity.class));
        TwoStatePreferenceHelper.replaceCheckBoxPreferencesBySwitchPreferences(preferenceScreen);

        mLookupContactsPreference = (SwitchPreference) findPreference(
                AndroidSpellCheckerService.PREF_USE_CONTACTS_KEY);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!TextUtils.equals(key, AndroidSpellCheckerService.PREF_USE_CONTACTS_KEY)) {
            return;
        }

        if (!sharedPreferences.getBoolean(key, false)) {
            // don't care if the preference is turned off.
            return;
        }

        // Check for permissions.
    }


}
