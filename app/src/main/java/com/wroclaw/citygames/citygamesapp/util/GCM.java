package com.wroclaw.citygames.citygamesapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;

public class GCM {

    public static void saveGcmID(String gcmId){
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("gcmId",gcmId);
        editor.commit();
    }

    public static String getGcmId(){
        SharedPreferences sharedpreferences =  App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        return sharedpreferences.getString("gcmId","");
    }
}
