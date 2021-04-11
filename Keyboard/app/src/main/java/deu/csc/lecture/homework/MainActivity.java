package deu.csc.lecture.homework;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void writeLetter(View v) {
        Button letter = (Button)v;
        TextView myTextview = (TextView)findViewById(R.id.myText);
        myTextview.setText(myTextview.getText().toString() + letter.getText().toString());
    }

    public void onShiftClick(View v) {
        for (int i = 0; i < 26; i++) {
            Button letter = (Button) findViewById(getResources().getIdentifier("button" + i,
                    "id", this.getPackageName()));
            if (Character.isUpperCase(letter.getText().charAt(0)))
                letter.setText(letter.getText().toString().toLowerCase());
            else
                letter.setText(letter.getText().toString().toUpperCase());
        }
    }

    public void onDelClick(View v) {
        TextView myTextview = (TextView)findViewById(R.id.myText);
        String myText = myTextview.getText().toString();
        if (myText != null && myText.length() > 0) {
            myTextview.setText(myText.substring(0, myText.length() - 1));
        }
    }

    public void onSpaceClick(View v) {
        TextView myTextview = (TextView)findViewById(R.id.myText);
        myTextview.setText(myTextview.getText().toString() + " ");
    }

    public void onEnterClick(View v) {
        TextView myTextbox = (TextView)findViewById(R.id.myText);
        myTextbox.setText(myTextbox.getText().toString() + System.getProperty("line.separator"));
    }
}
