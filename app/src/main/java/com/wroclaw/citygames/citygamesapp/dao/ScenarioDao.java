package com.wroclaw.citygames.citygamesapp.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.provider.BaseColumns;
import android.util.Log;

import com.wroclaw.citygames.citygamesapp.database.DatabaseHelper;
import com.wroclaw.citygames.citygamesapp.database.ScenarioTable;
import com.wroclaw.citygames.citygamesapp.model.Scenario;
import com.wroclaw.citygames.citygamesapp.util.ImageConverter;

import java.util.ArrayList;
import java.util.List;

public class ScenarioDao implements Dao<Scenario> {

    public static final String TAG = ScenarioDao.class.getName();
    private static ScenarioDao instance = null;

    public static ScenarioDao getInstance(DatabaseHelper dbManager){
        if(instance==null){
            instance= new ScenarioDao(dbManager);
        }
        return instance;
    }
    private DatabaseHelper dbManager;

    public ScenarioDao(DatabaseHelper dbManager){
        this.dbManager=dbManager;
    }
    @Override
    public long save(Scenario scenario) {
        Log.d(TAG, scenario.toString());
        saveImages(scenario);
        SQLiteDatabase db=dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ScenarioTable.ScenarioColumns._ID, scenario.getScenarioId());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_USERS_AMOUNT,scenario.getUsersAmount());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_DESCRIPTION,scenario.getDescription());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_TIME,scenario.getTime());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_DISTANCE_KM,scenario.getDistanceKm());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_LEVEL,scenario.getLevel());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_NAME,scenario.getName());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_PICTURE,scenario.getPicture());
        return db.insert(ScenarioTable.TABLE_NAME, // table
                null, //nullColumnHack
                values);

    }

    @Override
    public void update(Scenario scenario) {
        SQLiteDatabase db=dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ScenarioTable.ScenarioColumns.COLUMN_USERS_AMOUNT,scenario.getUsersAmount());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_DESCRIPTION,scenario.getDescription());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_TIME,scenario.getTime());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_DISTANCE_KM,scenario.getDistanceKm());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_LEVEL,scenario.getLevel());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_NAME,scenario.getName());
        values.put(ScenarioTable.ScenarioColumns.COLUMN_PICTURE,scenario.getPicture());

        db.update(ScenarioTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(scenario.getScenarioId())});
    }

    @Override
    public void delete(Scenario scenario) {
        SQLiteDatabase db=dbManager.getWritableDatabase();
        if(scenario.getScenarioId()>0)
            db.delete(ScenarioTable.TABLE_NAME, BaseColumns._ID + " = ?",new String[]{String.valueOf(scenario.getScenarioId())});
    }

    @Override
    public Scenario get(long id) {
        SQLiteDatabase db=dbManager.getReadableDatabase();
        Scenario scenario = null;
        Cursor c = db.query(ScenarioTable.TABLE_NAME,
                ScenarioTable.ALL_COLUMNS,
                BaseColumns._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1");
        if(c.moveToFirst()){
            scenario = this.buildScenarioFromCursor(c);
        }
        if(!c.isClosed()) c.close();
        return scenario;
    }

    @Override
    public List<Scenario> getAll() {
        SQLiteDatabase db=dbManager.getReadableDatabase();
        List<Scenario> scenarios = new ArrayList<>();
        Cursor c = db.query(ScenarioTable.TABLE_NAME,
                ScenarioTable.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null,
                null);
        if(c.moveToFirst()){
            do{
                Scenario scenario = this.buildScenarioFromCursor(c);
                if(scenario!=null){
                    scenarios.add(scenario);
                }

            }while(c.moveToNext());
        }
        if(!c.isClosed()) c.close();
        return scenarios;
    }

    public Scenario buildScenarioFromCursor(Cursor c){
        Scenario scenario = null;
        if (c != null){
            scenario = new Scenario();
            scenario.setScenarioId(c.getLong(0));
            scenario.setUsersAmount(c.getInt(1));
            scenario.setDescription(c.getString(2));
            scenario.setTime(c.getFloat(3));
            scenario.setDistanceKm(c.getFloat(4));
            scenario.setLevel(c.getString(5));
            scenario.setName(c.getString(6));
            scenario.setPicture(c.getString(7));
        }
        return scenario;
    }

    public void saveAll(List<Scenario> scenarios){
        for(Scenario scenario:scenarios){
            save(scenario);
        }
    }

    public void deleteAll(){
        SQLiteDatabase db=dbManager.getWritableDatabase();
        db.delete(ScenarioTable.TABLE_NAME,null,null);
    }

    public void saveImages(Scenario scenario){
        String image =  scenario.getPicture();
        if(image!=null && !image.isEmpty()){
            Bitmap  scenarioImage = ImageConverter.decode(image);
            String filename = ImageConverter.saveBitmap("scenario"+String.valueOf(scenario.getScenarioId()),scenarioImage);
            scenario.setPicture(filename);
        }
    }
}
