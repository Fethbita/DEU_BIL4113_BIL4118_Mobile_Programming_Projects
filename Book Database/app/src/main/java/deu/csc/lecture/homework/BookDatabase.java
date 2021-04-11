package deu.csc.lecture.homework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;

// https://stackoverflow.com/a/12015869
public class BookDatabase implements Serializable {
    public final static String BOOK_TABLE = "BOOKS"; // name of table
    public final static String BOOK_NAME = "NAME";  // name of book
    public final static String BOOK_PAGES = "PAGES";  // page number of book
    public final static String AUTHOR_NAME = "AUTHOR";  // author name of book
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public BookDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long InsertBook(String bookNameame, int pages, String authorName) {
        ContentValues values = new ContentValues();
        values.put(BOOK_NAME, bookNameame);
        values.put(BOOK_PAGES, pages);
        values.put(AUTHOR_NAME, authorName);

        return database.insert(BOOK_TABLE, null, values);
    }

    public ArrayList<String> SelectRecords() {
        ArrayList<String> list=new ArrayList<String>();
        Cursor  cursor = database.rawQuery("select * from " + BOOK_TABLE,null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(BOOK_NAME)) + ", " +
                        cursor.getString(cursor.getColumnIndex(BOOK_PAGES)) + ", " +
                        cursor.getString(cursor.getColumnIndex(AUTHOR_NAME));
                list.add(name);
                cursor.moveToNext();
            }
        }
        return list;
    }

    public void dropTable() {
        database.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
        dbHelper.onCreate(database);
    }
}

