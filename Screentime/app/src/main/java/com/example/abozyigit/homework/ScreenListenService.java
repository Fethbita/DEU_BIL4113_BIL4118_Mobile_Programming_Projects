package com.example.abozyigit.homework;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.example.abozyigit.homework.TimeTableContract.TimeEntry;

public class ScreenListenService extends Service {
    private static BroadcastReceiver screenReceiver;
    private TimeTableDbHelper dbHelper;

    public ScreenListenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        dbHelper = new TimeTableDbHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        screenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // https://developer.android.com/reference/android/content/Intent.html#ACTION_SCREEN_ON
                // For historical reasons, the name of this broadcast action refers to the power state
                // of the screen but it is actually sent in response to changes in the overall interactive state of the device.
                //
                //This broadcast is sent when the device becomes interactive which may have
                // nothing to do with the screen turning on. To determine the actual state of the screen, use Display.getState().
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    // https://stackoverflow.com/a/16864230
                    ContentValues values = new ContentValues();
                    values.put(TimeEntry.COLUMN_NAME_SCREENUNLOCKED, "true");
                    long newRowId = db.insert(TimeEntry.TABLE_NAME, null, values);
                }
                // https://developer.android.com/reference/android/content/Intent.html#ACTION_SCREEN_OFF
                // For historical reasons, the name of this broadcast action refers to the power state
                // of the screen but it is actually sent in response to changes in the overall interactive state of the device.
                //
                // This broadcast is sent when the device becomes non-interactive which may have
                // nothing to do with the screen turning off. To determine the actual state of the screen, use Display.getState().
                else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    ContentValues values = new ContentValues();
                    values.put(TimeEntry.COLUMN_NAME_SCREENUNLOCKED, "false");
                    long newRowId = db.insert(TimeEntry.TABLE_NAME, null, values);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        registerReceiver(screenReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        dbHelper.close();
        unregisterReceiver(screenReceiver);
        screenReceiver = null;
    }

}
