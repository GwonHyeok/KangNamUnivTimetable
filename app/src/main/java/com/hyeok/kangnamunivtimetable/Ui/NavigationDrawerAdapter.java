package com.hyeok.kangnamunivtimetable.Ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 9. 5..
 */
public class NavigationDrawerAdapter extends ArrayAdapter<String> {
    private ArrayList<String> objects;

    public NavigationDrawerAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @Override
    public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getContext().getResources().getColor(android.R.color.white));
        textView.setText(objects.get(position));
        return convertView;
    }

}
