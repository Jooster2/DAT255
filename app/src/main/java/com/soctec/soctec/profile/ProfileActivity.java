package com.soctec.soctec.profile;

import com.soctec.soctec.R;
import com.soctec.soctec.core.FileHandler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A ProfileActivity is an Activity that shows and allows edits to the profile data
 * @author Joakim Schmidt
 * @version 1.0
 */
public class ProfileActivity extends Activity
{
    private ArrayList<String> profileItems;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        createListFromFile();
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ProfileAdapter(getApplicationContext()));
    }

    /**
     * Saves the current state of the profile into the Profile class.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        ArrayList<ArrayList<String>> profileList = new ArrayList<>();
        for(int i = 0; i < Profile.NR_OF_CATEGORIES; i++)
        {
            profileList.add(new ArrayList<>(Arrays.asList(profileItems.get(i).split(","))));
        }
        Profile.setProfile(profileList);
    }

    /**
     * Saves the current state of the profile to a file
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        writeListToFile();
    }

    /**
     * Reads data from file and stores it in the local list.
     */
    private void createListFromFile()
    {
        profileItems = new ArrayList<>();
        ArrayList<String> tmpList = FileHandler.getInstance().readFile("profile");
        //Check if app is started for the first time
        if(tmpList.size() == 0)
        {
            for(int i = 1; i < Profile.NR_OF_CATEGORIES*2; i+=2)
            {
                profileItems.add("");
            }
            return;
        }

        for(int i = 1; i < Profile.NR_OF_CATEGORIES*2; i+=2)
        {
            profileItems.set(((i-1)/2), tmpList.get(i));
        }
    }

    /**
     * Saves the current profile to file
     */
    private void writeListToFile()
    {
        ArrayList<String> tmpList = new ArrayList<>();
        for (int i = 0; i < Profile.NR_OF_CATEGORIES; i++)
        {
            tmpList.add(intToCategory(i));
            tmpList.add(profileItems.get(i));
        }
        FileHandler.getInstance().writeFile("profile", tmpList);
    }

    /**
     * Converts an integer to a category String. (0 -> "Music", 1 -> "Movies" ... etc)
     * @param i The index of the category
     * @return The name of the category
     */
    private static String intToCategory(int i)
    {
        switch(i)
        {
            case 0:
                return "Music";
            case 1:
                return "Movies";

            //TODO more cases here
            default:
                return "";
        }
    }

    /**
     * This class is used as an Adapter for the profile activity.
     */
    private class ProfileAdapter extends BaseAdapter
    {
        Context context;

        /**
         * Sets the context of the Activity
         * @param context The context of the Activity
         */
        public ProfileAdapter(Context context)
        {
            super();
            this.context = context;
        }

        /**
         * Simple struct-like class for holding a view
         */
        private class ViewHolder{EditText editText; TextView textView;}

        /**
         * Returns the View at the given position.
         * @param position The position of the View
         * @param convertView The view
         * @param parent The viewGroup
         * @return The view
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder viewHolder;
            if (convertView == null)
            {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.row_profile, parent, false);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.titleText);
                viewHolder.editText = (EditText) convertView.findViewById(R.id.contentText);
                viewHolder.editText.addTextChangedListener(new ProfileTextWatcher(position)
                {
                    @Override
                    public void afterTextChanged(Editable s)
                    {
                        profileItems.set(position, s.toString());
                    }
                });
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(intToCategory(position));
            viewHolder.editText.setText(getItem(position).toString());

            return convertView;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public int getCount()
        {
            return Profile.NR_OF_CATEGORIES;
        }

        @Override
        public Object getItem(int position)
        {
            return profileItems.get(position);
        }
    }

    /**
     * This class is used as an abstract "TextChangedListener" for the profile list.
     */
    private abstract class ProfileTextWatcher implements TextWatcher
    {
        int position;
        public ProfileTextWatcher(int pos){position = pos;}
        @Override
        public void afterTextChanged(Editable s){}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){}
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    }
}
