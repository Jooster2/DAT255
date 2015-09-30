package com.soctec.soctec.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.soctec.soctec.R;

import java.util.ArrayList;
/**
 * Created by Jooster on 2015-09-29.
 */
public class ProfileMatchAdapter extends ArrayAdapter
{
    public ProfileMatchAdapter(Context context, ArrayList<String> matchResult)
    {
        super(context, R.layout.row_profilematch, matchResult);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.row_profilematch, parent, false);
        String singleStringItem = (String) getItem (position);
        TextView title = (TextView)customView.findViewById(R.id.matchTitle);
        TextView content = (TextView)customView.findViewById(R.id.matchContent);
        String [] strings = singleStringItem.split(",");

        title.setText(strings[0]);
        content.setText(strings[1]);

        return customView;
    }
}
