package com.soctec.soctec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Created by Carl-Henrik Hult on 2015-09-25.
 */
public class ProfileAdapter extends ArrayAdapter
{
    public ProfileAdapter(Context context, ArrayList<String> matchResult)
    {
        super(context, R.layout.row_profile);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.row_profile, parent, false);
        String singleStringItem = (String) getItem (position);
        TextView title = (TextView)customView.findViewById(R.id.matchWindowTitle);
        TextView content = (TextView)customView.findViewById(R.id.matchWindowContent);
        String [] strings = singleStringItem.split(",");

        title.setText(strings[0]);
        content.setText(strings[1]);

        return customView;
    }
}
