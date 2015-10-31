package com.wroclaw.citygames.citygamesapp.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.wroclaw.citygames.citygamesapp.database.DatabaseHelper;
import com.wroclaw.citygames.citygamesapp.database.GameTable;
import com.wroclaw.citygames.citygamesapp.model.Game;

import java.util.ArrayList;
import java.util.List;

public class GameDao implements Dao<Game> {

    public static final String TAG = GameDao.class.getName();
    private static GameDao instance = null;

    public static GameDao getInstance(DatabaseHelper dbManager){
        if(instance==null){
            instance= new GameDao(dbManager);
        }
        return instance;
    }
    private DatabaseHelper dbManager;

    public GameDao(DatabaseHelper dbManager){
        this.dbManager=dbManager;
    }
    @Override
    public long save(Game game) {
        Log.d(TAG, game.toString());
        SQLiteDatabase db=dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GameTable.GameColumns._ID, game.getGameId());
        values.put(GameTable.GameColumns.COLUMN_POINTS,game.getPoints());
        values.put(GameTable.GameColumns.COLUMN_SCENARIO_ID,game.getScenario().getScenarioId());
        values.put(GameTable.GameColumns.COLUMN_TEAM_ID,game.getTeamId());
        values.put(GameTable.GameColumns.COLUMN_TIME_START,game.getTimeStart());
        values.put(GameTable.GameColumns.COLUMN_TIME_END,game.getTimeEnd());

        return db.insert(GameTable.TABLE_NAME, // table
                null, //nullColumnHack
                values);
    }

    @Override
    public void update(Game game) {
        SQLiteDatabase db=dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GameTable.GameColumns.COLUMN_POINTS,game.getPoints());
        values.put(GameTable.GameColumns.COLUMN_SCENARIO_ID,game.getScenario().getScenarioId());
        values.put(GameTable.GameColumns.COLUMN_TEAM_ID,game.getTeamId());
        values.put(GameTable.GameColumns.COLUMN_TIME_START,game.getTimeStart());
        values.put(GameTable.GameColumns.COLUMN_TIME_END,game.getTimeEnd());
        db.update(GameTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(game.getGameId())});
    }

    @Override
    public void delete(Game game) {
        SQLiteDatabase db=dbManager.getWritableDatabase();
        if(game.getGameId()>0)
            db.delete(GameTable.TABLE_NAME, BaseColumns._ID + " = ?",new String[]{String.valueOf(game.getGameId())});
    }

    @Override
    public Game get(long id) {
        SQLiteDatabase db=dbManager.getReadableDatabase();
        Game game = null;
        Cursor c = db.query(GameTable.TABLE_NAME,
                GameTable.ALL_COLUMNS,
                BaseColumns._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1");
        if(c.moveToFirst()){
            game = this.buildGameFromCursor(c);
        }
        if(!c.isClosed()) c.close();
        return game;
    }

    @Override
    public List<Game> getAll() {
        SQLiteDatabase db=dbManager.getReadableDatabase();
        List<Game> games = new ArrayList<>();
        Cursor c = db.query(GameTable.TABLE_NAME,
                GameTable.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null,
                null);
        if(c.moveToFirst()){
            do{
                Game game = this.buildGameFromCursor(c);
                if(game!=null){
                    games.add(game);
                }

            }while(c.moveToNext());
        }
        if(!c.isClosed()) c.close();
        return games;
    }

    public Game buildGameFromCursor(Cursor c){
        Game game = null;
        if (c != null){
            game = new Game();
            game.setGameId(c.getLong(0));
            game.setPoints(c.getInt(1));
            game.setScenarioId(c.getLong(2));
            game.setTeamId(c.getLong(3));
            game.setTimeStart(c.getLong(4));
            game.setTimeEnd(c.getLong(5));
        }
        return game;
    }

    public void saveAll(List<Game> games){
        for(Game game:games){
            save(game);
        }
    }

    public void deleteAll(){
        SQLiteDatabase db=dbManager.getWritableDatabase();
        db.delete(GameTable.TABLE_NAME,null,null);
    }
}
