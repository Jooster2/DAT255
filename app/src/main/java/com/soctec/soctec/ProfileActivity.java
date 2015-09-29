package com.soctec.soctec;

import com.soctec.soctec.util.SystemUiHider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
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
