package com.german.keyboard.app.free.latin.setup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public final class SetupActivity extends Activity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent();
        intent.setClass(this, SetupWizardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (!isFinishing()) {
            finish();
        }
    }
}
