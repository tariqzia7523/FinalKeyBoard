package com.german.keyboard.app.free.latin.userdictionary;

import android.app.Fragment;

import java.util.Locale;

// Caveat: This class is basically taken from
// packages/apps/Settings/src/com/android/settings/inputmethod/UserDictionaryLocalePicker.java
// in order to deal with some devices that have issues with the user dictionary handling

public class UserDictionaryLocalePicker extends Fragment {
    public UserDictionaryLocalePicker() {
        super();
        // TODO: implement
    }

    public interface LocationChangedListener {
        void onLocaleSelected(Locale locale);
    }
}
