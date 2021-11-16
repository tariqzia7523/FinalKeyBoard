package io.github.otobikb.inputmethod.changes;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;


import io.github.otobikb.inputmethod.latin.R;

public class HowtoUse extends LocalizationActivity {


    String TAG ="***HOW";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howto_use);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}