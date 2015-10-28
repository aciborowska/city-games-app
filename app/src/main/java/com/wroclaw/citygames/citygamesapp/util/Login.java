package com.wroclaw.citygames.citygamesapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Login {

    public static void login(Long userId, String email, int passwordLength){
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("ifLogin",true);
        editor.putLong("userId",userId);editor.putString("email",email);
        editor.putInt("passwordLength",passwordLength);
        editor.commit();


    }

    public static void logout(){
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("ifLogin", false);
        editor.putLong("userId", Long.valueOf((-1)));
        editor.putString("email","");
        editor.putInt("passwordLength",-1);
        editor.commit();
    }

    public static boolean ifLogin(){
        SharedPreferences sharedpreferences =  App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean("ifLogin",false);
    }

    public static Long getCredentials(){
        SharedPreferences sharedpreferences =  App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        if(sharedpreferences.getBoolean("ifLogin",false)) return sharedpreferences.getLong("userId",-1);
        else return Long.valueOf((-1));
    }

    public static String getEmail(){
        SharedPreferences sharedpreferences =  App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        return sharedpreferences.getString("email", "");
    }

    public static int getPasswordLength(){
        SharedPreferences sharedpreferences =  App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        return sharedpreferences.getInt("passwordLength", 0);
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
