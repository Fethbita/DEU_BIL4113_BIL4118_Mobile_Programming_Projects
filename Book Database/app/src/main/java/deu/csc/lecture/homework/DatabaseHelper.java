package deu.csc.lecture.homework;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// https://stackoverflow.com/a/12015869
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DBName";
    private static final int DATABASE_VERSION = 1;
    private static String TABLE_NAME = "BOOKS";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String command = "CREATE TABLE " + TABLE_NAME + "( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "NAME TEXT,"
                + "PAGES INTEGER,"
                + "AUTHOR TEXT)";
        database.execSQL(command);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}

