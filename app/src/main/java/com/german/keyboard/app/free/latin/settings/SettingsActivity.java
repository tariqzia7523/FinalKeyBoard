package com.german.keyboard.app.free.latin.settings;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.german.keyboard.app.free.changes.ChangeLanguage;
import com.german.keyboard.app.free.R;
import com.german.keyboard.app.free.latin.utils.FragmentUtils;
import com.module.ads.AddInitilizer;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public final class SettingsActivity extends PreferenceActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String DEFAULT_FRAGMENT = SettingsFragment.class.getName();



    private boolean mShowHomeAsUp;

    AddInitilizer addInitilizer;
    @Override
    protected void onCreate(final Bundle savedState) {
        super.onCreate(savedState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //
//        try {
//            supportActionBar!!.hide()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        new ChangeLanguage(SettingsActivity.this, Settings.getSelectedLanguage(PreferenceManager.getDefaultSharedPreferences(this)));
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        RelativeLayout nativeContainer = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.native_add_layout_small, root, false);
        addInitilizer = new AddInitilizer(getApplicationContext(),this,null);
        addInitilizer.adInitializer(nativeContainer.findViewById(R.id.nativeTemplateView),nativeContainer.findViewById(R.id.temp_add_text),nativeContainer.findViewById(R.id.add_container));
        root.addView(bar, 0);
        root.addView(nativeContainer);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//
//        try {
//            supportActionBar!!.hide()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }




    }

    @Override
    public Intent getIntent() {
        final Intent intent = super.getIntent();
        final String fragment = intent.getStringExtra(EXTRA_SHOW_FRAGMENT);
        if (fragment == null) {
            intent.putExtra(EXTRA_SHOW_FRAGMENT, DEFAULT_FRAGMENT);
        }
        intent.putExtra(EXTRA_NO_HEADERS, true);
        return intent;
    }

    @Override
    public boolean isValidFragment(final String fragmentName) {
        return FragmentUtils.isValidFragment(fragmentName);
    }


}
