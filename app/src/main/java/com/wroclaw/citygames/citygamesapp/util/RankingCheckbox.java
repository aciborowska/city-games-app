package com.wroclaw.citygames.citygamesapp.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RankingCheckbox {

    public static void save(List<String> scenarios) {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(scenarios);
        editor.putStringSet("scenarios", set);
        editor.commit();
    }

    public static List<String> get() {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(App.getCtx().getResources().getString(R.string.shared_preferences_credentials), Context.MODE_PRIVATE);
        Set<String> scenarios = sharedpreferences.getStringSet("scenarios", null);
        if(scenarios!=null) {
            List<String> result = new ArrayList<>();
            result.addAll(scenarios);
            return result;
        }
        return new ArrayList<>();
    }
}
