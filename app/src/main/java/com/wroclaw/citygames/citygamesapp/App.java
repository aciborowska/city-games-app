package com.wroclaw.citygames.citygamesapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class App extends Application {

    private static final String TAG = App.class.getName();

    /**
     * Globalnie dostępny kontekst aplikacji
     */
    private static Context ctx = null;


    public static Context getCtx() {
        return ctx;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.ctx = getApplicationContext();
        Log.d(TAG, "onCreate");
    }

}
