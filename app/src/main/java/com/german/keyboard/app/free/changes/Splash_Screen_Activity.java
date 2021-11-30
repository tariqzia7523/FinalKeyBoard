package com.german.keyboard.app.free.changes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import com.german.keyboard.app.free.R;
import com.german.keyboard.app.free.latin.setup.SetupWizardActivity;


public class Splash_Screen_Activity extends AppCompatActivity {

    private final Handler waitHandler = new Handler();
    private final Runnable waitCallback = () -> {
        Intent intent = new Intent(Splash_Screen_Activity.this, SetupWizardActivity.class);
        startActivity(intent);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }
        waitHandler.postDelayed(waitCallback, 2000);




    }

    @Override
    protected void onDestroy() {
        waitHandler.removeCallbacks(waitCallback);
        super.onDestroy();
    }
}
