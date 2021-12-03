package com.german.keyboard.app.free.language.utils;

import com.german.keyboard.app.free.language.settings.AdvancedSettingsFragment;

import com.german.keyboard.app.free.language.settings.CorrectionSettingsFragment;
import com.german.keyboard.app.free.language.settings.CustomInputStyleSettingsFragment;
import com.german.keyboard.app.free.language.settings.DebugSettingsFragment;
import com.german.keyboard.app.free.language.settings.GestureSettingsFragment;
import com.german.keyboard.app.free.language.settings.PreferencesSettingsFragment;
import com.german.keyboard.app.free.language.settings.SettingsFragment;
import com.german.keyboard.app.free.language.settings.ThemeSettingsFragment;
import com.german.keyboard.app.free.language.spellcheck.SpellCheckerSettingsFragment;
import com.german.keyboard.app.free.language.userdictionary.UserDictionaryAddWordFragment;
import com.german.keyboard.app.free.language.userdictionary.UserDictionaryList;
import com.german.keyboard.app.free.language.userdictionary.UserDictionaryLocalePicker;
import com.german.keyboard.app.free.language.userdictionary.UserDictionarySettings;

import java.util.HashSet;

public class FragmentUtils {
    private static final HashSet<String> sLatinImeFragments = new HashSet<>();
    static {
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
