package com.wroclaw.citygames.citygamesapp.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;

public class Gameplay {
    public static void registerGame(Long gameId,Long teamId){
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong("gameId",gameId);
        editor.putLong("teamId",teamId);
        editor.putInt("penaltyPoints", 0);
        editor.commit();
    }

    public static Long getCurrentGame(){
        SharedPreferences sharedpreferences =  App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        return sharedpreferences.getLong("gameId",-1);
    }

    public static Long getCurrentGameTeam(){
        SharedPreferences sharedpreferences =  App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        return sharedpreferences.getLong("teamId",-1);
    }

    public static void addPenaltyPoints(int amount){
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        int current = sharedpreferences.getInt("penaltyPoints", 0);
        current+=amount;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("penaltyPoints",current);
        editor.commit();
    }

    public static int getPenaltyPoints(){
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        return sharedpreferences.getInt("penaltyPoints", 0);
    }

    public static void endGame(){
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences( App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong("gameId",Long.valueOf(-1));
        editor.putLong("teamId",Long.valueOf(-1));
        editor.putInt("penaltyPoints", 0);
        editor.commit();
    }
}
