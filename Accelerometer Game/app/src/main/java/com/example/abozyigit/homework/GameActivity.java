package com.example.abozyigit.homework;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

// Heavily updated from SensorApp example from Moodle
// and https://androidkennel.org/android-sensors-game-tutorial/
public class GameActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorManager;
    SQLiteDatabase db;
    float strokeWidth = 10;
    MyDraw myDraw;
    boolean startGame = false;
    Date startDate;
    private TimeTableDbHelper dbHelper;
    private float xPos, yPos;
    private float xMax, yMax;
    private float xAccel, xVel = 0.0f;
    private float yAccel, yVel = 0.0f;
    private float ballRadius;
    private float[] obstacles;

    private boolean hardMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        hardMode = intent.getBooleanExtra(MainActivity.EXTRA_MESSAGE, false);

        dbHelper = new TimeTableDbHelper(this);
        db = dbHelper.getWritableDatabase();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        myDraw = new MyDraw(this);
        setContentView(myDraw);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    // Sensor Operations
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (startGame) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    xAccel = event.values[0];
                    yAccel = -event.values[1];
                    updateBall();
                    break;
            }
        }
    }

    // Ball update, this also checks for collisions
    private void updateBall() {
        float frameTime = 0.666f;
        float maxVel = 25;

        xVel += (xAccel * frameTime);
        if (xVel > maxVel) {
            xVel = maxVel;
        } else if (xVel < -maxVel) {
            xVel = -maxVel;
        }

        yVel += (yAccel * frameTime);
        if (yVel > maxVel) {
            yVel = maxVel;
        } else if (yVel < -maxVel) {
            yVel = -maxVel;
        }

        float xS = (xVel / 2) * frameTime;
        float yS = (yVel / 2) * frameTime;

        xPos -= xS;
        yPos -= yS;

        // Collision detection part
        for (int i = 0; i < obstacles.length; i += 4) {
            float x1 = obstacles[i];
            float y1 = obstacles[i + 1];
            float x2 = obstacles[i + 2];
            if (yVel < 0 && Math.abs(yPos - y1) < ballRadius + strokeWidth / 2 && (xPos + ballRadius > x1 && xPos - ballRadius < x2) && yPos + ballRadius > y1 - strokeWidth / 2) {
                yPos = obstacles[i + 1] - strokeWidth / 2 - ballRadius;
                yVel = 0;
                yAccel = 0;
            } else if (yVel > 0 && Math.abs(yPos - y1) < ballRadius + strokeWidth / 2 && (xPos + ballRadius > x1 && xPos - ballRadius < x2) && yPos - ballRadius < y1 + strokeWidth / 2) {
                yPos = obstacles[i + 1] + strokeWidth / 2 + ballRadius;
                yVel = 0;
                yAccel = 0;
            }
        }

        if (xPos + ballRadius >= xMax) {
            xPos = xMax - ballRadius;
            xVel = 0;
            xAccel = 0;
        } else if (xPos - ballRadius <= 0) {
            xPos = 0 + ballRadius;
            xVel = 0;
            xAccel = 0;
        }

        if (yPos + ballRadius >= yMax) {
            yPos = yMax - ballRadius;
            yVel = 0;
            yAccel = 0;
        } else if (yPos - ballRadius <= 0) {
            yPos = 0 + ballRadius;
            yVel = 0;
            yAccel = 0;
        }
        // Game Completed, write to database
        if (Math.pow((xMax / 2) - xPos, 2) + Math.pow(ballRadius - yPos, 2) <= Math.pow(2 * ballRadius, 2)) {
            ContentValues values = new ContentValues();
            Date endDate = Calendar.getInstance().getTime();
            long diffInMs = endDate.getTime() - startDate.getTime();
            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
            Log.i("Time", " " + diffInSec);
            values.put(TimeTableContract.TimeEntry.COLUMN_NAME_TIMETAKENINSECONDS, diffInSec);
            values.put(TimeTableContract.TimeEntry.COLUMN_NAME_HARDMODE, hardMode);
            db.insert(TimeTableContract.TimeEntry.TABLE_NAME, null, values);

            sensorManager.unregisterListener(this);
            Toast.makeText(this, "Congratulations! Game completed in " + diffInSec + " seconds!", Toast.LENGTH_LONG).show();

            // https://stackoverflow.com/a/10032406/6077951
            new CountDownTimer(3500, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }.start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    class MyDraw extends View {
        private Paint p;
        private Paint bluePaint;
        private Paint redPaint;
        private Paint grayPaint;

        public MyDraw(Context context) {
            super(context);
            p = new Paint();

            bluePaint = new Paint();
            bluePaint.setColor(Color.BLUE);

            redPaint = new Paint();
            redPaint.setColor(Color.RED);
            redPaint.setStrokeWidth(strokeWidth);

            grayPaint = new Paint();
            grayPaint.setColor(Color.GRAY);
            grayPaint.setTextSize(50);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            xMax = w;
            yMax = h;

            float center_x = xMax / 2;

            xPos = center_x;
            yPos = h;
            ballRadius = center_x / 17;

            float lefthalfcenter_x = center_x / 2;
            float lefthalfcenterplus_x = lefthalfcenter_x + (center_x / 8);
            float lefthalfcenterminus_x = lefthalfcenter_x - (center_x / 8);
            float righthalfcenter_x = center_x + lefthalfcenter_x;
            float righthalfcenterplus_x = righthalfcenter_x + (center_x / 8);
            float righthalfcenterminus_x = righthalfcenter_x - (center_x / 8);

            float center_y = yMax / 2;
            float upperhalfcenter_y = center_y / 2;
            float upperhalfcenterplus_y = upperhalfcenter_y + (center_y / 8);
            float upperhalfcenterminus_y = upperhalfcenter_y - (center_y / 8);
            float lowerhalfcenter_y = center_y + upperhalfcenter_y;
            float lowerhalfcenterplus_y = lowerhalfcenter_y + (center_y / 8);
            float lowerhalfcenterminus_y = lowerhalfcenter_y - (center_y / 8);

            // I first tried to add better obstacles but it was too much manual work for what I had in mind.
            /*possibleObstacles = new float[]{0, upperhalfcenterplus_y, center_x, upperhalfcenterplus_y,
                    lefthalfcenter_x, upperhalfcenterplus_y, lefthalfcenter_x, center_y,
                    xMax, upperhalfcenterminus_y, center_x, upperhalfcenterminus_y,
                    righthalfcenter_x, upperhalfcenterminus_y, righthalfcenter_x, center_y,
                    0, lowerhalfcenterminus_y, righthalfcenterminus_x, lowerhalfcenterminus_y,
                    righthalfcenterminus_x, lowerhalfcenterminus_y, righthalfcenterminus_x, lowerhalfcenter_y,
                    xMax, lowerhalfcenterplus_y, lefthalfcenterplus_x, lowerhalfcenterplus_y,
                    lefthalfcenterplus_x, lowerhalfcenterplus_y, lefthalfcenterplus_x, lowerhalfcenter_y};*/

            if (hardMode) {
                obstacles = new float[4 * 4];
            } else {
                obstacles = new float[2 * 4];
            }

            List<Float> yList = new ArrayList<>();
            // At least a ball size difference between lines
            //Log.i("Tag2", lowerhalfcenter_y + " " + upperhalfcenter_y + " " + ballRadius);
            for (float j = upperhalfcenter_y; j < lowerhalfcenter_y; j += (ballRadius * 2 + 16)) {
                yList.add(j);
            }
            Collections.shuffle(yList);

            for (int i = 0; i < obstacles.length; i += 4) {
                // Line comes from left
                if (Math.random() < 0.5) {
                    obstacles[i] = 0;
                    float xEnd = center_x + (float) Math.random() * (lefthalfcenterminus_x);
                    obstacles[i + 2] = xEnd;
                }
                // Line comes from right
                else {
                    float xEnd = center_x - (float) Math.random() * (lefthalfcenterminus_x);
                    obstacles[i] = xEnd;
                    obstacles[i + 2] = xMax;
                }
                //Log.i("Tag", i + " " + yList.size());
                obstacles[i + 1] = yList.get(i / 4);
                obstacles[i + 3] = yList.get(i / 4);
            }
            startGame = true;
            startDate = Calendar.getInstance().getTime();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawCircle(xMax / 2, ballRadius, ballRadius, bluePaint);
            canvas.drawText("HEDEF -> ", xMax / 4, ballRadius * 1.5f, grayPaint);
            canvas.drawCircle(xPos, yPos, ballRadius, p);
            canvas.drawLines(obstacles, redPaint);

            //Log.i("X info = ", String.format("X Max = %f, X Pos = %f, X Velocity = %f, X Acceleration = %f", xMax, xPos, xVel, xAccel));
            //Log.i("Y info = ", String.format("Y Max = %f, Y Pos = %f, Y Velocity = %f, Y Acceleration = %f", yMax, yPos, yVel, yAccel));

            invalidate();
        }
    }
}