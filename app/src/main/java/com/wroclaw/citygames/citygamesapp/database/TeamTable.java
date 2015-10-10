package com.wroclaw.citygames.citygamesapp.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class TeamTable {

        public static final String TABLE_NAME="TEAM";
        public static final String[] ALL_COLUMNS={TeamColumns._ID,TeamColumns.COLUMN_NAME,
                TeamColumns.COLUMN_PASSWORD,TeamColumns.COLUMN_SINGULAR};

        public interface TeamColumns extends BaseColumns {
            String COLUMN_PASSWORD="PASSWORD";
            String COLUMN_NAME="NAME";
            String COLUMN_SINGULAR="SINGULAR";
        }

        public static void onCreate(SQLiteDatabase db){
            String sql="CREATE TABLE "+TABLE_NAME + "( ";
            sql+=BaseColumns._ID + " INTEGER NOT NULL PRIMARY KEY, ";
            sql+= TeamColumns.COLUMN_NAME + " text, ";
            sql+= TeamColumns.COLUMN_PASSWORD + " text ,";
            sql+= TeamColumns.COLUMN_SINGULAR + " integer );";
            db.execSQL(sql);
        }
}

