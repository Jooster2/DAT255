package com.soctec.soctec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Created by Jooster on 2015-09-29.
 */
public class ProfileAdapter extends ArrayAdapter
{
    public ProfileAdapter(Context context, ArrayList<String> contents)
    {
        super(context, R.layout.row_profile, contents);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.row_profile, parent, false);
        String item = (String)getItem(position);
        TextView title = (TextView)customView.findViewById(R.id.titleText);
        EditText content = (EditText)customView.findViewById(R.id.contentText);
        String[] strings = item.split(":");

        title.setText(strings[0]);
        content.setText(strings[1]);

        return customView;
    }
}
