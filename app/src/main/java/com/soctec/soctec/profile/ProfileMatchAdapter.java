package com.soctec.soctec.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.soctec.soctec.R;

import java.util.ArrayList;
/**
 * Used to display two TextViews in one element of a ListView
 * @author Joakim Schmidt, Carl-Henrik Hult
 * @version 1.0
 */
public class ProfileMatchAdapter extends ArrayAdapter
{
    /**
     * Constructor that sets the context and what every list item in the list view should contain.
     * @param context       Context of calling class
     * @param matchResult   A list of all the matches that should be put in the list.
     */
    public ProfileMatchAdapter(Context context, ArrayList<String> matchResult)
    {
        super(context, R.layout.row_profilematch, matchResult);
    }

    /**
     * Gets a specific view
     * @param position The position of the view
     * @param convertView The old view to reuse
     * @param parent The partent of the view
     * @return The view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.row_profilematch, parent, false);
        String singleStringItem = (String) getItem (position);
        TextView title = (TextView)customView.findViewById(R.id.matchCategory);
        TextView content = (TextView)customView.findViewById(R.id.matchContent);
        String [] strings = singleStringItem.split(",");

        title.setText(strings[0]);
        content.setText(strings[1]);

        return customView;
    }
}
