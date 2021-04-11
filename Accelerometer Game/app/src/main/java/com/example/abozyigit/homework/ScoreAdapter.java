package com.example.abozyigit.homework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// Taken from https://guides.codepath.com/android/Using-a-BaseAdapter-with-ListView
public class ScoreAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ResultsActivity.Score> results;

    public ScoreAdapter(Context context, ArrayList<ResultsActivity.Score> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.list_result, parent, false);
        }

        // get current item to be displayed
        ResultsActivity.Score currentItem = (ResultsActivity.Score) getItem(position);

        // get the TextView for item name and item description
        TextView date = convertView.findViewById(R.id.textView);
        TextView text = convertView.findViewById(R.id.textView2);

        //sets the text for item name and item description from the current item object
        date.setText(currentItem.date);
        text.setText(currentItem.text);

        // returns the view for the current row
        return convertView;
    }
}
