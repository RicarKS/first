package com.example.ksmusic_md;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    public final static String SHOW_GUIDE = "show_guide";
    public final static String FIRST_START = "first_start";

    public static void setBoolean(Context context, String key, boolean value){
        SharedPreferences preferences = context.getSharedPreferences("preference",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key,boolean defValue){
        SharedPreferences preferences = context.getSharedPreferences("preference",Context.MODE_PRIVATE);
        return preferences.getBoolean(key,defValue);
    }
}
