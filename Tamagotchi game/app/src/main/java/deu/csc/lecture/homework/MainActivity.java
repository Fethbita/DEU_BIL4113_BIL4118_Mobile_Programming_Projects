package deu.csc.lecture.homework;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    static final int GET_WATER_AND_FOOD = 1;
    int hunger = -1;
    int thirst = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null){
            hunger = savedInstanceState.getInt("hunger");
            thirst = savedInstanceState.getInt("thirst");
        }
        else {
            loadState();
        }
        updateProgress();

        // lower hunger and thirst every 5 seconds
        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                hunger--;
                thirst--;
                if (hunger < 0) {
                    hunger = 0;
                }
                if (thirst < 0) {
                    thirst = 0;
                }
                updateProgress();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    public void onPause() {
        super.onPause();
        saveState();
    }
    public void onStop() {
        super.onStop();
        saveState();
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("hunger", hunger);
        savedInstanceState.putInt("thirst", thirst);
    }
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        hunger = savedInstanceState.getInt("hunger");
        thirst = savedInstanceState.getInt("thirst");
        updateProgress();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_WATER_AND_FOOD) {
            if (resultCode == RESULT_OK) {
                hunger = data.getIntExtra("hunger", 50);
                thirst = data.getIntExtra("thirst", 50);
                updateProgress();
            }
        }
    }

    private void updateProgress() {
        // happy face
        if (hunger >= 80 && thirst >= 80)
        {
            ((ImageView)findViewById(R.id.pet)).setImageResource(R.drawable.happy);
        }
        // neutral face
        else if (hunger >= 40 && thirst >= 40)
        {
            ((ImageView)findViewById(R.id.pet)).setImageResource(R.drawable.neutral);
        }
        // sad face
        else
        {
            ((ImageView)findViewById(R.id.pet)).setImageResource(R.drawable.sad);
        }
        ((ProgressBar)findViewById(R.id.hunger)).setProgress(hunger);
        ((TextView)findViewById(R.id.hungerText)).setText(Integer.toString(hunger));
        ((ProgressBar)findViewById(R.id.thirst)).setProgress(thirst);
        ((TextView)findViewById(R.id.thirstText)).setText(Integer.toString(thirst));
    }

    private void saveState() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt("hunger", hunger);
        prefsEditor.putInt("thirst", thirst);
        prefsEditor.apply();
    }
    private void loadState() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        hunger = prefs.getInt("hunger", 50);
        thirst = prefs.getInt("thirst", 50);
    }

    // start a new window
    public void feed_onClick(View v) {
        Intent intent = new Intent(this, Besle.class);
        intent.putExtra("hunger", hunger);
        intent.putExtra("thirst", thirst);
        startActivityForResult(intent, GET_WATER_AND_FOOD);
    }
}
