package com.example.abozyigit.homework;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {
    TimeTableDbHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ArrayList<Score> results = new ArrayList<>();

        dbHelper = new TimeTableDbHelper(this);
        db = dbHelper.getReadableDatabase();

        String sortOrder =
                TimeTableContract.TimeEntry.COLUMN_NAME_UNIXTIME + " ASC";

        Cursor cursor = db.query(
                TimeTableContract.TimeEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        // Get results from the database
        while (cursor.moveToNext()) {
            String time = cursor.getString(
                    cursor.getColumnIndexOrThrow(TimeTableContract.TimeEntry.COLUMN_NAME_UNIXTIME)) + "+00";
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX", Locale.getDefault());
            Date date = null;
            try {
                date = parser.parse(time);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            time = parser.format(date);

            boolean hardMode = cursor.getString(cursor.getColumnIndexOrThrow(TimeTableContract.TimeEntry.COLUMN_NAME_HARDMODE)).equals("1");
            int timeTakenInSeconds = cursor.getInt(cursor.getColumnIndexOrThrow(TimeTableContract.TimeEntry.COLUMN_NAME_TIMETAKENINSECONDS));

            String text = "\t" + (hardMode ? "Hard Mode " : "Easy Mode ") + "\tCompleted in " + timeTakenInSeconds + " seconds";
            Score result = new Score(time, text);
            results.add(result);
        }
        cursor.close();

        // Show them on the screen
        ListView resultsList = findViewById(R.id.resultsList);
        ScoreAdapter adapter = new ScoreAdapter(this, results);
        resultsList.setAdapter(adapter);
    }

    class Score {
        String date;
        String text;

        Score(String date, String text) {
            this.date = date;
            this.text = text;
        }
    }
}
