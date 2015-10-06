package com.wroclaw.citygames.citygamesapp.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class ScenarioTable {

    public static final String TABLE_NAME="SCENARIO";

    public static final String[] ALL_COLUMNS = {ScenarioColumns._ID, ScenarioColumns.COLUMN_USERS_AMOUNT,
            ScenarioColumns.COLUMN_DESCRIPTION, ScenarioColumns.COLUMN_TIME, ScenarioColumns.COLUMN_DISTANCE_KM,
            ScenarioColumns.COLUMN_LEVEL};

    public interface ScenarioColumns extends BaseColumns {
        String COLUMN_USERS_AMOUNT ="USERS_AMOUNT";
        String COLUMN_DESCRIPTION="DESCRIPTION";
        String COLUMN_TIME="TIME";
        String COLUMN_DISTANCE_KM="DISTANCE_KM";
        String COLUMN_LEVEL="LEVEL";
    }

    public static void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + "( ";
        sql += BaseColumns._ID + " INTEGER(10) NOT NULL PRIMARY KEY, ";
        sql += ScenarioColumns.COLUMN_USERS_AMOUNT + " integer(2), ";
        sql +=ScenarioColumns.COLUMN_DESCRIPTION + " text, ";
        sql += ScenarioColumns.COLUMN_TIME + " real(10), ";
        sql += ScenarioColumns.COLUMN_DISTANCE_KM + " real(10), ";
        sql += ScenarioColumns.COLUMN_LEVEL + " text;";

        db.execSQL(sql);
    }
}
