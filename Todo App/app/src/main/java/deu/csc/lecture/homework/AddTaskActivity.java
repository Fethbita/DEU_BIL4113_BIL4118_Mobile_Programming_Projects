package deu.csc.lecture.homework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AddTaskActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
    }

    public void ekle_onClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("task", new ToDo(R.drawable.varda_xmas, ((TextView)findViewById(R.id.editText)).getText().toString()));
        setResult(RESULT_OK, intent);
        finish();
    }
}
