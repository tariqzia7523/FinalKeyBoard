package com.german.keyboard.app.free.changes

import android.annotation.SuppressLint
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.german.keyboard.app.free.R
import com.module.ads.AddInitilizer
import java.lang.Exception
import java.util.*

class Premium : LocalizationActivity() {
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)
        try {
            supportActionBar!!.hide()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        findViewById<View>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<View>(R.id.go_premium).setOnClickListener {
            AddInitilizer(
                applicationContext,
                this@Premium,
                null
            ).goAddFree()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}