package com.german.keyboard.app.free.changes

import com.akexorcist.localizationactivity.ui.LocalizationActivity
import android.content.SharedPreferences
import android.widget.TextView
import android.widget.RadioButton
import android.os.Bundle
import android.view.WindowManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.german.keyboard.app.free.R
import android.annotation.SuppressLint
import android.view.View
import android.widget.Button

class PrivacyPolicy : LocalizationActivity() {
    var accept: Button? = null
    var cancel: Button? = null
    var preferences: SharedPreferences? = null
    var radio_ninjas: RadioButton? = null
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }
        setContentView(R.layout.privacypolicy)
        if (supportActionBar != null) supportActionBar!!.hide()
        radio_ninjas = findViewById(R.id.radio_ninjas)
    }

    @SuppressLint("NonConstantResourceId")
    fun onRadioButtonClicked(view: View) {
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.radio_btn1 -> {
                if (radio_ninjas!!.isChecked) {
                    setLanguage("de")
                }
                setLanguage("en")
            }
            R.id.radio_ninjas -> if (checked) setLanguage("de")
        }
    }
}