package com.example.abozyigit.homework;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.abozyigit.homework.TimeTableContract.TimeEntry;

// https://developer.android.com/training/data-storage/sqlite#java
public class TimeTableDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TimeTable.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TimeEntry.TABLE_NAME + " (" +
                    TimeEntry._ID + " INTEGER PRIMARY KEY," +
                    TimeEntry.COLUMN_NAME_UNIXTIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    TimeEntry.COLUMN_NAME_SCREENUNLOCKED + " BIT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TimeEntry.TABLE_NAME;

    TimeTableDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
