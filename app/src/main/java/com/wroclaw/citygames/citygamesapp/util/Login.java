package com.wroclaw.citygames.citygamesapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Login {

    public static final String TAG = Login.class.getName();

    public static void login(Long userId, String email) {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("ifLogin", true);
        editor.putLong("playerId", userId);
        editor.putString("email", email);
        editor.commit();
    }

    public static void logout() {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("ifLogin", false);
        editor.putLong("playerId", (long) (-1));
        editor.putString("email", "");
        editor.commit();
    }

    public static boolean ifLogin() {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean("ifLogin", false);
    }

    public static Long getPlayerId() {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean("ifLogin", false))
            return sharedpreferences.getLong("playerId", -1);
        else return (long) (-1);
    }

    public static String getEmail() {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        return sharedpreferences.getString("email", "");
    }

    public static String MD5Encryption(String encTarget) {
        if(encTarget==null || encTarget.isEmpty()) return null;
        MessageDigest md5password = null;
        try {
            md5password = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "brak algorytmu MD5");
        }
        assert md5password != null;
        md5password.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, md5password.digest()).toString(16);
        while (md5.length() < 32) {
            md5 = "0" + md5;
        }
        return md5;
    }

}
