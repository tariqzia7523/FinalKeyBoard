package com.german.keyboard.app.free.changes

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.german.keyboard.app.free.language.setup.SetupWizardActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.german.keyboard.app.free.R
import java.lang.Exception

class Splash_Screen_Activity : AppCompatActivity() {
    private val waitHandler = Handler()
    private val waitCallback = Runnable {
        val intent = Intent(this@Splash_Screen_Activity, SetupWizardActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash_screen)
        try {
            supportActionBar!!.hide()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        waitHandler.postDelayed(waitCallback, 2000)
    }

    override fun onDestroy() {
        waitHandler.removeCallbacks(waitCallback)
        super.onDestroy()
    }
}