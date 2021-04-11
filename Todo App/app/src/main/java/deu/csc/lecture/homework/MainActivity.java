package deu.csc.lecture.homework;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int REQ_CODE = 4568;
    private String TASKS = "ToDoArray";
    private ArrayList<ToDo> todoList = new ArrayList<>();
    private ToDoAdapter toDoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        // https://stackoverflow.com/a/7057858
        try {
            todoList = (ArrayList<ToDo>) ObjectSerializer.deserialize(prefs.getString(TASKS, ObjectSerializer.serialize(new ArrayList<ToDo>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        toDoAdapter = new ToDoAdapter(this, todoList);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(toDoAdapter);

        listView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> list, View row, int index, long rowID) {
                        todoList.remove(index);
                        toDoAdapter.notifyDataSetChanged();
                        return true;
                    }
                }
        );
    }

    public void ekle_onClick(View view) {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(intent, REQ_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQ_CODE) {
            ToDo data = null;

            data = (ToDo) intent.getExtras().get("task");
            if (data == null) {
                return;
            }

            if (todoList.contains(data)) {
                return;
            }
            todoList.add(data);
            toDoAdapter.notifyDataSetChanged();

            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = prefs.edit();
            // https://stackoverflow.com/a/7057858
            try {
                prefsEditor.putString(TASKS, ObjectSerializer.serialize(todoList));
            } catch (IOException e) {
                e.printStackTrace();
            }
            prefsEditor.commit();
        }
    }
}
