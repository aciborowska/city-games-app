package com.wroclaw.citygames.citygamesapp.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class GameTable {
    public static final String TABLE_NAME = "GAME";

    public static final String[] ALL_COLUMNS = {GameColumns._ID, GameColumns.COLUMN_POINTS,
            GameColumns.COLUMN_SCENARIO_ID, GameColumns.COLUMN_TEAM_ID,GameColumns.COLUMN_TIME_START,
            GameColumns.COLUMN_TIME_END};

    public interface GameColumns extends BaseColumns {
        String COLUMN_SCENARIO_ID = "SCENARIO_ID";
        String COLUMN_TEAM_ID = "TEAM_ID";
        String COLUMN_POINTS = "POINTS";
        String COLUMN_TIME_START = "TIME_START";
        String COLUMN_TIME_END = "TIME_END";
    }

    public static void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + "( ";
        sql += BaseColumns._ID + " INTEGER(10) NOT NULL PRIMARY KEY, ";
        sql += GameColumns.COLUMN_SCENARIO_ID + " integer(10), ";
        sql += GameColumns.COLUMN_TEAM_ID + " integer(10), ";
        sql+=GameColumns.COLUMN_POINTS +" integer(10), ";
        sql+=GameColumns.COLUMN_TIME_START + " real(10), ";
        sql+=GameColumns.COLUMN_TIME_END + " real(10), ";
        sql += "FOREIGN KEY(" + GameColumns.COLUMN_SCENARIO_ID + ") REFERENCES " + ScenarioTable.TABLE_NAME + " (_id), ";
        sql += "FOREIGN KEY(" + GameColumns.COLUMN_TEAM_ID + ") REFERENCES " + TeamTable.TABLE_NAME + " (_id));";
        db.execSQL(sql);
    }
}
