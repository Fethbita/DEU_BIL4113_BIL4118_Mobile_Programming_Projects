package deu.csc.lecture.homework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Besle extends Activity {

    int hunger = -1;
    int thirst = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_besle);

        Intent intent = getIntent();
        hunger = intent.getIntExtra("hunger", -1);
        thirst = intent.getIntExtra("thirst", -1);
    }

    public void foodAndWater(View v) {
        switch (v.getId()) {
            case R.id.feed1:
                hunger += 5;
                break;
            case R.id.feed2:
                hunger += 10;
                break;
            case R.id.water1:
                thirst += 5;
                break;
            case R.id.water2:
                thirst += 10;
                break;
            default:
                break;
        }
        if (hunger > 100) {
            hunger = 100;
        }
        if (thirst > 100) {
            thirst = 100;
        }
        Intent intent = new Intent();
        intent.putExtra("hunger", hunger);
        intent.putExtra("thirst", thirst);
        setResult(RESULT_OK, intent);
        finish();
    }
}
