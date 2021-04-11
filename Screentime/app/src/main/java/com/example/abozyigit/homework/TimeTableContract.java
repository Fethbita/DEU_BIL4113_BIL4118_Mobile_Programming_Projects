package com.example.abozyigit.homework;

import android.provider.BaseColumns;

final class TimeTableContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TimeTableContract() {}

    /* Inner class that defines the table contents */
    static class TimeEntry implements BaseColumns {
        static final String TABLE_NAME = "times";
        static final String COLUMN_NAME_UNIXTIME = "unixtime";
        static final String COLUMN_NAME_SCREENUNLOCKED = "screenunlocked";
    }
}
