package com.german.keyboard.app.free.changes

import com.akexorcist.localizationactivity.ui.LocalizationActivity
import android.annotation.SuppressLint
import android.os.Bundle
import com.german.keyboard.app.free.R
import android.os.Build
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.util.*

class TestActivity : LocalizationActivity() {
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        try {
            supportActionBar!!.hide()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }
        findViewById<View>(R.id.backBtn).setOnClickListener { view: View? -> finish() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}