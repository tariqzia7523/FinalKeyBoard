

package com.german.keyboard.app.free.language.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.german.keyboard.app.free.R;
import com.german.keyboard.app.free.language.utils.ApplicationUtils;
import com.german.keyboard.app.free.language.utils.FeedbackUtils;
import com.german.keyboard.app.free.language.utils.JniUtils;
import com.german.keyboard.app.free.InputMethodSettingsFragment;

public final class SettingsFragment extends InputMethodSettingsFragment {
    // We don't care about menu grouping.
    private static final int NO_MENU_GROUP = Menu.NONE;
    // The first menu item id and order.
    private static final int MENU_ABOUT = Menu.FIRST;
    // The second menu item id and order.
    private static final int MENU_HELP_AND_FEEDBACK = Menu.FIRST + 1;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        setHasOptionsMenu(true);
        setInputMethodSettingsCategoryTitle(R.string.language_selection_title);
        setSubtypeEnablerTitle(R.string.select_language);
        addPreferencesFromResource(R.xml.prefs);
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.setTitle(
                ApplicationUtils.getActivityTitleResId(getActivity(), SettingsActivity.class));
        if (!JniUtils.sHaveGestureLib) {
            final Preference gesturePreference = findPreference(Settings.SCREEN_GESTURE);
            preferenceScreen.removePreference(gesturePreference);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        if (FeedbackUtils.isHelpAndFeedbackFormSupported()) {
            menu.add(NO_MENU_GROUP, MENU_HELP_AND_FEEDBACK /* itemId */,
                    MENU_HELP_AND_FEEDBACK /* order */, R.string.help_and_feedback);
        }
        final int aboutResId = FeedbackUtils.getAboutKeyboardTitleResId();
        if (aboutResId != 0) {
            menu.add(NO_MENU_GROUP, MENU_ABOUT /* itemId */, MENU_ABOUT /* order */, aboutResId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final Activity activity = getActivity();
        if (!isUserSetupComplete(activity)) {
            // If setup is not complete, it's not safe to launch Help or other activities
            // because they might go to the Play Store.  See b/19866981.
            return true;
        }
        final int itemId = item.getItemId();
        if (itemId == MENU_HELP_AND_FEEDBACK) {
            FeedbackUtils.showHelpAndFeedbackForm(activity);
            return true;
        }
        if (itemId == MENU_ABOUT) {
            final Intent aboutIntent = FeedbackUtils.getAboutKeyboardIntent(activity);
            if (aboutIntent != null) {
                startActivity(aboutIntent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private static boolean isUserSetupComplete(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }
        return Secure.getInt(activity.getContentResolver(), "user_setup_complete", 0) != 0;
    }
}
