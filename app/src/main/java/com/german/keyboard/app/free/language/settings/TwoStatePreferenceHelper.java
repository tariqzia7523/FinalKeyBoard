package com.german.keyboard.app.free.language.settings;

import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.SwitchPreference;

import java.util.ArrayList;

public class TwoStatePreferenceHelper {
    private static final String EMPTY_TEXT = "";

    private TwoStatePreferenceHelper() {
        // This utility class is not publicly instantiable.
    }

    public static void replaceCheckBoxPreferencesBySwitchPreferences(final PreferenceGroup group) {
        // The keyboard settings keeps using a CheckBoxPreference on KitKat or previous.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            return;
        }
        // The keyboard settings starts using a SwitchPreference without switch on/off text on
        // API versions newer than KitKat.
        replaceAllCheckBoxPreferencesBySwitchPreferences(group);
    }

    private static void replaceAllCheckBoxPreferencesBySwitchPreferences(
            final PreferenceGroup group) {
        final ArrayList<Preference> preferences = new ArrayList<>();
        final int count = group.getPreferenceCount();
        for (int index = 0; index < count; index++) {
            preferences.add(group.getPreference(index));
        }
        group.removeAll();
        for (int index = 0; index < count; index++) {
            final Preference preference = preferences.get(index);
            if (preference instanceof CheckBoxPreference) {
                addSwitchPreferenceBasedOnCheckBoxPreference((CheckBoxPreference)preference, group);
            } else {
                group.addPreference(preference);
                if (preference instanceof PreferenceGroup) {
                    replaceAllCheckBoxPreferencesBySwitchPreferences((PreferenceGroup)preference);
                }
            }
        }
    }

    static void addSwitchPreferenceBasedOnCheckBoxPreference(final CheckBoxPreference checkBox,
            final PreferenceGroup group) {
        final SwitchPreference switchPref = new SwitchPreference(checkBox.getContext());
        switchPref.setTitle(checkBox.getTitle());
        switchPref.setKey(checkBox.getKey());
        switchPref.setOrder(checkBox.getOrder());
        switchPref.setPersistent(checkBox.isPersistent());
        switchPref.setEnabled(checkBox.isEnabled());
        switchPref.setChecked(checkBox.isChecked());
        switchPref.setSummary(checkBox.getSummary());
        switchPref.setSummaryOn(checkBox.getSummaryOn());
        switchPref.setSummaryOff(checkBox.getSummaryOff());
        switchPref.setSwitchTextOn(EMPTY_TEXT);
        switchPref.setSwitchTextOff(EMPTY_TEXT);
        group.addPreference(switchPref);
        switchPref.setDependency(checkBox.getDependency());
    }
}
