package io.github.otobikb.inputmethod.changes;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.otobikb.inputmethod.latin.R;


public class MySharedPref {
    SharedPreferences sharedPreferences;
    Context context;
    public MySharedPref(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.my_pref),Context.MODE_PRIVATE);
    }

    public void  setPurcheshed(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.is_purchsed),value).apply();
    }
    public boolean isPurshed(){
        return sharedPreferences.getBoolean(context.getString(R.string.is_purchsed),false);
    }

    public void  setUserReviwed(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.user_reviwed),value).apply();
    }
    public boolean isUserReviwed(){
        return sharedPreferences.getBoolean(context.getString(R.string.user_reviwed),false);
    }

    public boolean getUserReview(){
        if((!isUserReviwed()) && getAppCount()>2){
            return true;
        }else
            return false;
    }

    public void  setUserIntro(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.user_intro),value).apply();
    }
    public boolean getUserIntro(){
        return sharedPreferences.getBoolean(context.getString(R.string.user_intro),false);
    }

    public void  setAppCount(int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.app_count),value).apply();
    }
    public int getAppCount(){
        return sharedPreferences.getInt(context.getString(R.string.app_count),0);
    }

}