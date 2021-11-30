package com.german.keyboard.app.free.changes;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.akexorcist.localizationactivity.ui.LocalizationActivity;

import com.german.keyboard.app.free.R;

public class PrivacyPolicy extends LocalizationActivity {
    Button accept, cancel;
    SharedPreferences preferences;
    TextView translate_txt;
    TextView title_txt;
    RadioButton radio_ninjas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
        setContentView(R.layout.privacypolicy);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        radio_ninjas = findViewById(R.id.radio_ninjas);

    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radio_btn1:
                if (radio_ninjas.isChecked()) {
                    setLanguage("de");
                }
                setLanguage("en");
                break;
            case R.id.radio_ninjas:
                if (checked)
                    setLanguage("de");
                break;
        }
    }
}
