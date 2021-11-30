package com.german.keyboard.app.free.changes;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import androidx.annotation.RequiresApi;
import java.util.Locale;
public class ChangeLanguage {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public ChangeLanguage(Context context, String languageCode) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(languageCode));
        res.updateConfiguration(conf, dm);
    }
}