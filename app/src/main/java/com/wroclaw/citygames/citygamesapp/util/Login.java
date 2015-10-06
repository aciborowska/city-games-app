package com.wroclaw.citygames.citygamesapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;


public class Login {

    public static void login(Long userId, Context context){
        if(context!=null){
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(context.getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("ifLogin",true);
        editor.putLong("userId",userId);
        editor.commit();
        }

    }

    public static void logout(){
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("ifLogin", false);
        editor.putLong("userId", Long.valueOf((-1)));
        editor.commit();
    }

    public static Long getCredentials(Context context){
        if(context!=null){
        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        if(sharedpreferences.getBoolean("ifLogin",false)) return sharedpreferences.getLong("userId",-1);
        else return Long.valueOf((-1));}
        Log.d("CREDENTIALS","context==null");
        return Long.valueOf((-1));
    }
}
