package com.develop.childtracking.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class App_SharedPreferences {

    private static final String MY_PREF_NAME = "ChildTracking";
    private static final String USER_FCM_TOKEN = "user_api_token";


    public static String Get_Token(Context context) {

        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(MY_PREF_NAME, MODE_PRIVATE);
            String FCMToken = prefs.getString(USER_FCM_TOKEN, "");

            return FCMToken;
        } else {
            return null;
        }
    }

    public static void Save_Fcm_Token(Context context, String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(USER_FCM_TOKEN, token);
        editor.apply();
    }

    public static void Clear_SharedPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
