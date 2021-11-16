package io.github.otobikb.inputmethod.changes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import io.github.otobikb.inputmethod.intro.FinnishActivity;
import io.github.otobikb.inputmethod.latin.R;

public class Splash_Screen_Activity extends AppCompatActivity {

    private final Handler waitHandler = new Handler();
    private final Runnable waitCallback = () -> {
        Intent intent = new Intent(Splash_Screen_Activity.this, Main_Activity.class);
        startActivity(intent);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Fake wait 2s to simulate some initialization on cold start (never do this in production!)
        waitHandler.postDelayed(waitCallback, 2000);
    }

    @Override
    protected void onDestroy() {
        waitHandler.removeCallbacks(waitCallback);
        super.onDestroy();
    }
}
