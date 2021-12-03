package com.german.keyboard.app.free.language;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.SharedPreferences;
import android.os.ParcelFileDescriptor;

import com.german.keyboard.app.free.language.settings.LocalSettingsConstants;

import java.io.IOException;

/**
 * Backup/restore agent for LatinIME.
 * Currently it backs up the default shared preferences.
 */
public final class BackupAgent extends BackupAgentHelper {
    private static final String PREF_SUFFIX = "_preferences";

    @Override
    public void onCreate() {
        addHelper("shared_pref", new SharedPreferencesBackupHelper(this,
                getPackageName() + PREF_SUFFIX));
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState)
            throws IOException {
        // Let the restore operation go through
        super.onRestore(data, appVersionCode, newState);

        // Remove the preferences that we don't want restored.
        final SharedPreferences.Editor prefEditor = getSharedPreferences(
                getPackageName() + PREF_SUFFIX, MODE_PRIVATE).edit();
        for (final String key : LocalSettingsConstants.PREFS_TO_SKIP_RESTORING) {
            prefEditor.remove(key);
        }
        // Flush the changes to disk.
        prefEditor.commit();
    }
}
