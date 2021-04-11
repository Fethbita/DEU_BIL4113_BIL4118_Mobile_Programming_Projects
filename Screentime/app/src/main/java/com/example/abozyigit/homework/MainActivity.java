package com.example.abozyigit.homework;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TimeTableDbHelper dbHelper;
    SQLiteDatabase db;

    RecyclerView recyclerView;
    TextView unlockedTime;
    TextView averageTime;
    Button dateStart;
    Button dateEnd;

    List<Date> times;
    List<Date> timesAll;
    List<Boolean> unlockeds;
    List<Boolean> unlockedsAll;
    List<String> printed;
    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Boolean parent = intent.getBooleanExtra(LoginActivity.EXTRA_MESSAGE, false);

        if (parent) {
            findViewById(R.id.btnRemoveAll).setVisibility(View.VISIBLE);
        }

        recyclerView = findViewById(R.id.recyclerView);
        unlockedTime = findViewById(R.id.unlockedTime);
        averageTime = findViewById(R.id.averageTime);
        dateStart = findViewById(R.id.dateStart);
        dateEnd = findViewById(R.id.dateEnd);
        bindButtontoPicker(dateStart);
        bindButtontoPicker(dateEnd);

        dbHelper = new TimeTableDbHelper(this);
        db = dbHelper.getReadableDatabase();

        times = new ArrayList<>();
        unlockeds = new ArrayList<>();
        timesAll = new ArrayList<>();
        unlockedsAll = new ArrayList<>();
        printed = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, printed);
        recyclerView.setAdapter(adapter);
    }

    protected void showList() {
        Calendar calendarFirst = Calendar.getInstance();
        Calendar calendarSecond = Calendar.getInstance();
        SimpleDateFormat parsernoTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dateFirst;
        Date dateSecond;

        try {
            dateFirst = parsernoTime.parse(dateStart.getText().toString());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            dateStart.setText(parsernoTime.format(calendarFirst.getTime()));
            dateFirst = calendarFirst.getTime();
        }

        try {
            dateSecond = parsernoTime.parse(dateEnd.getText().toString());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            dateEnd.setText(parsernoTime.format(calendarSecond.getTime()));
            dateSecond = calendarSecond.getTime();
        }
        calendarFirst.setTime(dateFirst);
        calendarSecond.setTime(dateSecond);
        clearTimesFromCalendar(calendarFirst);
        clearTimesFromCalendar(calendarSecond);

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

        times.clear();
        unlockeds.clear();
        timesAll.clear();
        unlockedsAll.clear();
        printed.clear();
        // https://stackoverflow.com/a/26826919
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

            Calendar calendarEvent = Calendar.getInstance();
            calendarEvent.setTime(date);
            clearTimesFromCalendar(calendarEvent);
            Boolean unlocked = cursor.getString(cursor.getColumnIndexOrThrow(TimeTableContract.TimeEntry.COLUMN_NAME_SCREENUNLOCKED)).equals("true");

            timesAll.add(date);
            unlockedsAll.add(unlocked);

            if(!((calendarEvent.equals(calendarFirst) || calendarEvent.after(calendarFirst))
                    && (calendarEvent.equals(calendarSecond) || calendarEvent.before(calendarSecond)))){
                Log.e("test", calendarFirst.getTime().toString() + "\n" + calendarSecond.getTime().toString() + "\n" + calendarEvent.getTime().toString());
                continue;
            }

            unlockeds.add(unlocked);

            times.add(date);
            printed.add(time + (unlocked ? " unlocked" : " locked"));
        }
        cursor.close();
        unlockedTime.setText("");
        adapter.notifyDataSetChanged();
    }

    public void btnCalculate_onClick(View view) {
        showList();
        long totalSeconds = 0;
        long unlockedTimeDuration = -1;

        for (int i = 0; i < times.size(); i++) {
            if (unlockeds.get(i) && unlockedTimeDuration == -1) {
                unlockedTimeDuration = times.get(i).getTime();
            }
            else if (!unlockeds.get(i) && unlockedTimeDuration != -1) {
                totalSeconds += (times.get(i).getTime() - unlockedTimeDuration) / 1000;
                unlockedTimeDuration = -1;
            }
        }
        unlockedTime.setText("In Between The Dates = " + DateUtils.formatElapsedTime(totalSeconds));


        List<Long> secondsPerDate = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = -1;
        int year = -1;
        long unlockedTimeDuration2 = -1;

        for (int i = 0; i < timesAll.size(); i++) {
            calendar.setTime(timesAll.get(i));
            if (unlockedsAll.get(i) && unlockedTimeDuration2 == -1) {
                unlockedTimeDuration2 = timesAll.get(i).getTime();
                calendar.setTime(timesAll.get(i));
                dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                year = calendar.get(Calendar.YEAR);
            }
            else if (unlockedTimeDuration2 != -1) {
                if (dayOfYear == calendar.get(Calendar.DAY_OF_YEAR) && year == calendar.get(Calendar.YEAR)) {
                    if (secondsPerDate.size() == 0) {
                        secondsPerDate.add((timesAll.get(i).getTime() - unlockedTimeDuration2) / 1000);
                    }
                    else {
                        secondsPerDate.set(secondsPerDate.size() - 1, secondsPerDate.get(secondsPerDate.size() - 1)
                                + (timesAll.get(i).getTime() - unlockedTimeDuration2) / 1000);
                    }
                }
                else {
                    Calendar temp = Calendar.getInstance();
                    temp.setTime(timesAll.get(i));
                    clearTimesFromCalendar(temp);

                    secondsPerDate.set(secondsPerDate.size() - 1, secondsPerDate.get(secondsPerDate.size() - 1)
                            + (temp.getTimeInMillis() - unlockedTimeDuration2) / 1000);

                    secondsPerDate.add((timesAll.get(i).getTime() - temp.getTimeInMillis()) / 1000);
                    dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                    year = calendar.get(Calendar.YEAR);
                }
                unlockedTimeDuration2 = -1;
            }
        }
        long dayAverages = 0L;
        for (int i = 0; i < secondsPerDate.size(); i++) {
            dayAverages += secondsPerDate.get(i);
        }
        if (secondsPerDate.size() > 0){
            dayAverages /= secondsPerDate.size();
        }

        averageTime.setText("Daily Average = " + DateUtils.formatElapsedTime(dayAverages));
    }

    public void btnRemoveAll_onClick(View view) {
        String SQL_DELETE_ENTRIES = "DELETE FROM " + TimeTableContract.TimeEntry.TABLE_NAME;
        SQLiteDatabase dbDrop = dbHelper.getWritableDatabase();
        dbDrop.execSQL(SQL_DELETE_ENTRIES);
        btnCalculate_onClick(view);
    }

    // https://stackoverflow.com/a/17670669
    protected void bindButtontoPicker(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Calendar calendar = Calendar.getInstance();
                Dialog mDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(arg1, arg2, arg3);

                                SimpleDateFormat parsernoTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                button.setText(parsernoTime.format(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));

                mDialog.show();
            }
        });
    }

    protected void clearTimesFromCalendar(Calendar calendar) {
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
