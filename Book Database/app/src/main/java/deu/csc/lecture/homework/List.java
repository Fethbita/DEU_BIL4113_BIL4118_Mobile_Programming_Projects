package deu.csc.lecture.homework;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class List extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ArrayList<String> queryResult = getIntent().getStringArrayListExtra("queryResult");

        ArrayAdapter<String> myAdapter =  new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, queryResult);
        ListView list = (ListView) findViewById(R.id.queries);
        list.setAdapter(myAdapter);
    }
}
