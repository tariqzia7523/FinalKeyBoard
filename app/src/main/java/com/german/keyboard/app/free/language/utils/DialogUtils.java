package com.german.keyboard.app.free.language.utils;

import android.content.Context;
import android.view.ContextThemeWrapper;

import com.german.keyboard.app.free.R;

public final class DialogUtils {
    private DialogUtils() {
        // This utility class is not publicly instantiable.
    }

    public static Context getPlatformDialogThemeContext(final Context context) {
        // Because {@link AlertDialog.Builder.create()} doesn't honor the specified theme with
        // createThemeContextWrapper=false, the result dialog box has unneeded paddings around it.
        return new ContextThemeWrapper(context, R.style.platformDialogTheme);
    }
}
