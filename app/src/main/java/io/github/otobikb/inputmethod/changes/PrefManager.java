package io.github.otobikb.inputmethod.changes;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    private static SharedPreferences.Editor editor1;

    private final SharedPreferences pref2;
    private final SharedPreferences prefInApp;
    private final SharedPreferences.Editor editor2;
    private final SharedPreferences.Editor editorInApp;
    private final SharedPreferences preferences;
    // Shared preferences file name
    private static final String PREF_NAME2 = "privacyPolicy";
    private static final String PREF_name_inAppPurchases = "inAppPurchases";

    private static final String IS_Accepted = "isAccepted";
    private static final String IS_AcceptedLanguage = "isAcceptedLanguage";
    private static final String IS_FROM_MAIN = "isFromMain";
    private static final String IS_Purchased = "isPurchased";

    // shared pref mode
    int PRIVATE_MODE = 0;
    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String MY_PREFERENCES = "my_preferences";
    // shared pref mode

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        preferences = context.getSharedPreferences(MY_PREFERENCES, PRIVATE_MODE);
        editor1 = preferences.edit();

        pref2 = context.getSharedPreferences(PREF_NAME2, PRIVATE_MODE);
        editor2 = pref2.edit();

        prefInApp = context.getSharedPreferences(PREF_name_inAppPurchases, PRIVATE_MODE);
        editorInApp = prefInApp.edit();



    }

    public void setBurmeseKeyboard(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isBurmeseKeyboard() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public static boolean isFirst(Context context){
        final SharedPreferences reader = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        final boolean first = reader.getBoolean("is_first", true);
        if(first){
            editor1.putBoolean("is_first", false);
            editor1.commit();
        }
        return first;
    }
    public static void setToFirstTimeLaunch(boolean isFirstTime) {
        editor1.putBoolean("is_first", isFirstTime);
        editor1.commit();
    }

    public void setBurmeseSymbol(boolean Purchased) {
        editorInApp.putBoolean(IS_Purchased, Purchased);
        editorInApp.commit();
    }

    public boolean isBurmeseSymbol() {
        return prefInApp.getBoolean(IS_Purchased, false);
    }


    public void setEnglishSymbol(boolean isAccepted) {
        editor2.putBoolean(IS_Accepted, isAccepted);
        editor2.commit();
    }

    public boolean isEnglishSymbol() {
        return pref2.getBoolean(IS_Accepted, false);
    }

    public void setEnglishKeyboard(boolean isAccepted) {
        editor2.putBoolean(IS_AcceptedLanguage, isAccepted);
        editor2.commit();
    }

    public boolean isEnglishKeyboard() {
        return pref2.getBoolean(IS_AcceptedLanguage, false);
    }


    public void enabledKeyboard(boolean isAccepted) {
        editor2.putBoolean(IS_FROM_MAIN, isAccepted);
        editor2.commit();
    }

    public boolean isEnabledKeyboard() {
        return pref2.getBoolean(IS_FROM_MAIN, false);
    }



}