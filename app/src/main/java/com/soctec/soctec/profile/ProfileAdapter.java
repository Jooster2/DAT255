package com.soctec.soctec.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.soctec.soctec.R;

import java.util.ArrayList;
/**
 * A ProfileAdapter is an object for displaying a TextView and an EditText in a ListView
 * @author Joakim Schmidt
 * @version 1.0
 * @see ArrayAdapter TextView EditText
 */
/*public class ProfileAdapter extends ArrayAdapter
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
        TextView title = (TextView)customView.findViewById(R.id.TitleText);
        EditText content = (EditText)customView.findViewById(R.id.contentText);
        String[] strings = item.split(":");


        title.setText(strings[0]);
        content.setText(strings[1]);

        return customView;
    }
}
*/