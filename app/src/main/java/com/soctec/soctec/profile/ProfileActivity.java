package com.soctec.soctec.profile;

import com.soctec.soctec.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A ProfileActivity is an Activity that shows and allows edits to the profile data
 * @author Joakim Schmidt
 * @version 1.0
 */
public class ProfileActivity extends Activity
{
    private Context context;
    private ListView listView;
    private ProfileAdapter profileAdapter;
    private ArrayList<ArrayList<String>> profileItems;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        listView = (ListView)findViewById(R.id.listView);
        profileItems = new ArrayList<>(2);
        createFromFile();
        populateListView();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        updateProfileItems();
        Profile.setProfile(profileItems);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        writeToFile();
    }

    /**
     * Updates the local profile list with data from the GUI
     */
    private void updateProfileItems()
    {
        profileItems = new ArrayList<>();
        for(int i=0; i<profileAdapter.getCount(); i++)
        {
            profileItems.add(new ArrayList<String>());
            String item = (String)profileAdapter.getItem(i);
            String[] subItem = item.split(":");
            profileItems.get(i).add(subItem[0]);
            String[] contents = subItem[1].split(", ");
            Collections.addAll(profileItems.get(i), contents);
        }
    }

    /**
     * Attempts to load the old profile from file
     */
    private void createFromFile()
    {
        try
        {
            BufferedReader readBuffer = new BufferedReader(new FileReader("profile"));
            String line = readBuffer.readLine();
            ArrayList<String> currentList = null;
            while(line != null)
            {
                //Easy solution, may exist nicer ones
                switch(line)
                {
                    case "music": currentList = profileItems.get(0); break;
                    case "movie": currentList = profileItems.get(1); break;
                    //TODO more cases here
                    default: currentList.add(line); break;
                }
                line = readBuffer.readLine();

            }
            readBuffer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to save the current profile to file
     */
    private void writeToFile()
    {
        try
        {
            BufferedWriter writeBuffer = new BufferedWriter(new FileWriter("profile"));
            //FileOutputStream output = openFileOutput("profile", Context.MODE_PRIVATE);
            int i=0;
            for(ArrayList<String> item : profileItems)
            {
                switch(i)
                {
                    case 0: writeBuffer.write("music\n"); break;
                    case 1: writeBuffer.write("movie\n"); break;
                }
                for(String str : item)
                {
                    writeBuffer.write(str + '\n');
                }
                i++;
            }
            writeBuffer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Inserts the local profile into the GUI
     */
    public void populateListView()
    {
        int i=0;
        ArrayList<String> showList = new ArrayList<>();
        for(ArrayList<String> item : profileItems)
        {
            StringBuilder sb = new StringBuilder();
            switch(i)
            {
                case 0: sb.append("Musik:"); break;
                case 1: sb.append("Film:"); break;
            }
            for(String str : item)
            {
                sb.append(str);
                sb.append(", ");
            }
            showList.add(sb.toString());
            i++;
        }
        profileAdapter = new ProfileAdapter(this, showList);
        listView.setAdapter(profileAdapter);
    }

    /*public void editList(View currentView)
    {
        TextView titleText = (TextView)currentView.findViewById(R.id.titleText);
        String title = (String)titleText.getText();
        TextView contentText = (TextView)currentView.findViewById(R.id.contentText);
        String content = (String)contentText.getText();
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putString("title", title);
        fragmentBundle.putString("content", content);



    }*/


}
