package com.german.keyboard.app.free.changes;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.module.ads.AddInitilizer;

import java.util.Objects;

import com.german.keyboard.app.free.R;

public class Premium extends LocalizationActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.go_premium).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddInitilizer(getApplicationContext(),Premium.this,null).goAddFree();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}