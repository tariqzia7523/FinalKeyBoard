package com.german.keyboard.app.free.changes

import android.content.Context
import android.content.SharedPreferences
import com.german.keyboard.app.free.changes.PrefManager

class PrefManager(var _context: Context) {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    private val pref2: SharedPreferences
    private val prefInApp: SharedPreferences
    private val editor2: SharedPreferences.Editor
    private val editorInApp: SharedPreferences.Editor
    private val preferences: SharedPreferences

    // shared pref mode
    var PRIVATE_MODE = 0
    var isBurmeseKeyboard: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }
    var isBurmeseSymbol: Boolean
        get() = prefInApp.getBoolean(IS_Purchased, false)
        set(Purchased) {
            editorInApp.putBoolean(IS_Purchased, Purchased)
            editorInApp.commit()
        }
    var isEnglishSymbol: Boolean
        get() = pref2.getBoolean(IS_Accepted, false)
        set(isAccepted) {
            editor2.putBoolean(IS_Accepted, isAccepted)
            editor2.commit()
        }
    var isEnglishKeyboard: Boolean
        get() = pref2.getBoolean(IS_AcceptedLanguage, false)
        set(isAccepted) {
            editor2.putBoolean(IS_AcceptedLanguage, isAccepted)
            editor2.commit()
        }

    fun enabledKeyboard(isAccepted: Boolean) {
        editor2.putBoolean(IS_FROM_MAIN, isAccepted)
        editor2.commit()
    }

    val isEnabledKeyboard: Boolean
        get() = pref2.getBoolean(IS_FROM_MAIN, false)

    companion object {
        private lateinit var editor1: SharedPreferences.Editor

        // Shared preferences file name
        private const val PREF_NAME2 = "privacyPolicy"
        private const val PREF_name_inAppPurchases = "inAppPurchases"
        private const val IS_Accepted = "isAccepted"
        private const val IS_AcceptedLanguage = "isAcceptedLanguage"
        private const val IS_FROM_MAIN = "isFromMain"
        private const val IS_Purchased = "isPurchased"

        // Shared preferences file name
        private const val PREF_NAME = "androidhive-welcome"
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
        private const val MY_PREFERENCES = "my_preferences"
        fun isFirst(context: Context): Boolean {
            val reader = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE)
            val first = reader.getBoolean("is_first", true)
            if (first) {
                editor1.putBoolean("is_first", false)
                editor1.commit()
            }
            return first
        }

        fun setToFirstTimeLaunch(isFirstTime: Boolean) {
            editor1.putBoolean("is_first", isFirstTime)
            editor1.commit()
        }
    }

    // shared pref mode
    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
        val PRIVATE_MODE = 0
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
        preferences = _context.getSharedPreferences(MY_PREFERENCES, PRIVATE_MODE)
        editor1 = preferences.edit()
        pref2 = _context.getSharedPreferences(PREF_NAME2, PRIVATE_MODE)
        editor2 = pref2.edit()
        prefInApp = _context.getSharedPreferences(PREF_name_inAppPurchases, PRIVATE_MODE)
        editorInApp = prefInApp.edit()
    }
}