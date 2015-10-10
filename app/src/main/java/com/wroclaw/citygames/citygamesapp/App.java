package com.wroclaw.citygames.citygamesapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.wroclaw.citygames.citygamesapp.dao.GameDao;
import com.wroclaw.citygames.citygamesapp.dao.ScenarioDao;
import com.wroclaw.citygames.citygamesapp.dao.TeamDao;
import com.wroclaw.citygames.citygamesapp.database.DatabaseHelper;

public class App extends Application {

    private static final String TAG = App.class.getName();

    /**
     * Globalnie dostępny kontekst aplikacji
     */
    private static Context ctx = null;
    private static TeamDao teamDao = null;
    private static ScenarioDao scenarioDao = null;
    private static GameDao gameDao = null;
    private DatabaseHelper db;

    public static TeamDao getTeamDao(){return teamDao;}

    public static GameDao getGameDao() { return gameDao;}

    public static ScenarioDao getScenarioDao() { return scenarioDao;}

    public static Context getCtx() {
        return ctx;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.ctx = getApplicationContext();
        db = new DatabaseHelper(ctx);
        teamDao = TeamDao.getInstance(db);
        scenarioDao = ScenarioDao.getInstance(db);
        gameDao = GameDao.getInstance(db);
        Log.d(TAG, "onCreate");
    }

}
