package com.example.abozyigit.homework;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    Button playButton;
    Button viewScoresButton;
    Switch hardMode;

    public static final String EXTRA_MESSAGE = "hardMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.playButton);
        viewScoresButton = findViewById(R.id.viewScoresButton);
        hardMode = findViewById(R.id.hardMode);
    }

    // Nothing much to see here, really.
    public void playButton_onClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(EXTRA_MESSAGE, hardMode.isChecked());
        startActivity(intent);
    }

    public void viewScoresButton_onClick(View view) {
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }
}
