package com.german.keyboard.app.free.latin.utils;

import com.german.keyboard.app.free.latin.about.AboutPreferences;
import com.german.keyboard.app.free.latin.settings.AdvancedSettingsFragment;

import com.german.keyboard.app.free.latin.settings.CorrectionSettingsFragment;
import com.german.keyboard.app.free.latin.settings.CustomInputStyleSettingsFragment;
import com.german.keyboard.app.free.latin.settings.DebugSettingsFragment;
import com.german.keyboard.app.free.latin.settings.GestureSettingsFragment;
import com.german.keyboard.app.free.latin.settings.PreferencesSettingsFragment;
import com.german.keyboard.app.free.latin.settings.SettingsFragment;
import com.german.keyboard.app.free.latin.settings.ThemeSettingsFragment;
import com.german.keyboard.app.free.latin.spellcheck.SpellCheckerSettingsFragment;
import com.german.keyboard.app.free.latin.userdictionary.UserDictionaryAddWordFragment;
import com.german.keyboard.app.free.latin.userdictionary.UserDictionaryList;
import com.german.keyboard.app.free.latin.userdictionary.UserDictionaryLocalePicker;
import com.german.keyboard.app.free.latin.userdictionary.UserDictionarySettings;

import java.util.HashSet;

public class FragmentUtils {
    private static final HashSet<String> sLatinImeFragments = new HashSet<>();
    static {
        sLatinImeFragments.add(AboutPreferences.class.getName());
        sLatinImeFragments.add(PreferencesSettingsFragment.class.getName());
        sLatinImeFragments.add(ThemeSettingsFragment.class.getName());
        sLatinImeFragments.add(CustomInputStyleSettingsFragment.class.getName());
        sLatinImeFragments.add(GestureSettingsFragment.class.getName());
        sLatinImeFragments.add(CorrectionSettingsFragment.class.getName());
        sLatinImeFragments.add(AdvancedSettingsFragment.class.getName());
        sLatinImeFragments.add(DebugSettingsFragment.class.getName());
        sLatinImeFragments.add(SettingsFragment.class.getName());
        sLatinImeFragments.add(SpellCheckerSettingsFragment.class.getName());
        sLatinImeFragments.add(UserDictionaryAddWordFragment.class.getName());
        sLatinImeFragments.add(UserDictionaryList.class.getName());
        sLatinImeFragments.add(UserDictionaryLocalePicker.class.getName());
        sLatinImeFragments.add(UserDictionarySettings.class.getName());
    }

    public static boolean isValidFragment(String fragmentName) {
        return sLatinImeFragments.contains(fragmentName);
    }
}
