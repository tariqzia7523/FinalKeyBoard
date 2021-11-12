package io.github.otobikb.changes

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import io.github.otobikb.inputmethod.latin.R

class PrivacyPolicy : AppCompatActivity() {
    var radio_ninjas: RadioButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }
        setContentView(R.layout.activity_privacy_policy)
        if (supportActionBar != null) supportActionBar!!.hide()
        radio_ninjas = findViewById<RadioButton>(R.id.radio_ninjas)
    }

}