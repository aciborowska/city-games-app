package com.wroclaw.citygames.citygamesapp.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.wroclaw.citygames.citygamesapp.database.DatabaseHelper;
import com.wroclaw.citygames.citygamesapp.database.TeamTable;
import com.wroclaw.citygames.citygamesapp.model.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamDao implements Dao<Team> {
    public static final String TAG = TeamDao.class.getName();
    private static TeamDao instance = null;

    public static TeamDao getInstance(DatabaseHelper dbManager){
        if(instance==null){
            instance= new TeamDao(dbManager);
        }
        return instance;
    }
    private DatabaseHelper dbManager;

    public TeamDao(DatabaseHelper dbManager){
        this.dbManager=dbManager;
    }
    @Override
    public long save(Team team) {
        Log.d(TAG, team.toString());
        SQLiteDatabase db=dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TeamTable.TeamColumns._ID, team.getTeamId());
        values.put(TeamTable.TeamColumns.COLUMN_NAME, team.getName());
        values.put(TeamTable.TeamColumns.COLUMN_PASSWORD, team.getPassword());

        // 3. insert
        return db.insert(TeamTable.TABLE_NAME, // table
                null, //nullColumnHack
                values);
    }

    @Override
    public void update(Team team) {
        SQLiteDatabase db=dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TeamTable.TeamColumns.COLUMN_NAME, team.getName());
        values.put(TeamTable.TeamColumns.COLUMN_PASSWORD, team.getPassword());
        values.put(TeamTable.TeamColumns.COLUMN_SINGULAR,team.getSingular());
        db.update(TeamTable.TABLE_NAME, values, BaseColumns._ID + " = ?",new String[]{String.valueOf(team.getTeamId())});
    }

    @Override
    public void delete(Team team) {
        SQLiteDatabase db=dbManager.getWritableDatabase();
        if(team.getTeamId()>0)
            db.delete(TeamTable.TABLE_NAME, BaseColumns._ID + " = ?",new String[]{String.valueOf(team.getTeamId())});
    }

    @Override
    public Team get(long id) {
        SQLiteDatabase db=dbManager.getReadableDatabase();
        Team team = null;
        Cursor c = db.query(TeamTable.TABLE_NAME,
                TeamTable.ALL_COLUMNS,
                BaseColumns._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1");
        if(c.moveToFirst()){
            team = this.buildTeamFromCursor(c);
        }
        if(!c.isClosed()) c.close();
        return team;
    }

    @Override
    public List<Team> getAll() {
        SQLiteDatabase db=dbManager.getReadableDatabase();
        List<Team> teams = new ArrayList<>();
        Cursor c = db.query(TeamTable.TABLE_NAME,
                TeamTable.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null,
                null);
        if(c.moveToFirst()){
            do{
                Team team = this.buildTeamFromCursor(c);
                if(team!=null){
                    teams.add(team);
                }

            }while(c.moveToNext());
        }
        if(!c.isClosed()) c.close();
        return teams;
    }

    public Team buildTeamFromCursor(Cursor c){
        Team team = null;
        if (c != null){
            team = new Team();
            team.setTeamId(c.getLong(0));
            team.setName(c.getString(1));
            team.setPassword(c.getString(2));
            team.setSingular(c.getInt(3));
        }
        return team;
    }

    public void saveAll(List<Team> teams){
        for(Team team:teams){
            save(team);
        }
    }

    public void deleteAll(){
        SQLiteDatabase db=dbManager.getWritableDatabase();
        db.delete(TeamTable.TABLE_NAME,null,null);
    }
}
