package deu.csc.lecture.homework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ToDoAdapter extends ArrayAdapter<ToDo> {
    private Context Context;
    private ArrayList<ToDo> todoList = new ArrayList<ToDo>();

    public ToDoAdapter(Context context, ArrayList<ToDo> list) {
        super(context, 0, list);
        Context = context;
        todoList = list;
    }

    @Override
    public View getView(int i, View row, ViewGroup parent) {
        if (row == null)
            row = LayoutInflater.from(Context).inflate(R.layout.todolayout, parent, false);

        ToDo currentMovie = todoList.get(i);

        ImageView image = (ImageView) row.findViewById(R.id.imageView);
        image.setImageResource(currentMovie.gettoDoImage());

        TextView name = (TextView) row.findViewById(R.id.textView);
        name.setText(currentMovie.getTask());

        return row;
    }
}
