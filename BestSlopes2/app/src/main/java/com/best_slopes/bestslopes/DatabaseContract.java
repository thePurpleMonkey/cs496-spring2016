package com.best_slopes.bestslopes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Michael Humphrey on 4/20/2016.
 */
public final class DatabaseContract {
    public DatabaseContract() {}

    public static class Trail extends SQLiteOpenHelper implements BaseColumns  {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "trails.db";
        public static final String TABLE_NAME = "trails";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DIFFICULTY = "difficulty";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_COMMENTS = "comments";

        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + Trail.TABLE_NAME + " (" +
                        Trail._ID + " INTEGER PRIMARY KEY, " +
                        Trail.COLUMN_NAME_TITLE + " TEXT, " +
                        Trail.COLUMN_NAME_DIFFICULTY + " INTEGER, " +
                        Trail.COLUMN_NAME_RATING + " INTEGER, " +
                        Trail.COLUMN_NAME_COMMENTS + " TEXT)";
        private static final String SQL_DELETE_TRAILS =
                "DROP TABLE IF EXISTS " + Trail.TABLE_NAME;


        public Trail(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}