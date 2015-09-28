package com.soctec.soctec;

import com.soctec.soctec.util.SystemUiHider;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ProfileActivity extends Activity
{
    private Context context;
    private ArrayList<ArrayList<String>> profileItems;
    private ListView listView;
    private Button addButton;
    private ProfileAdapter profileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        listView = (ListView)findViewById(R.id.listView);
        addButton = (Button)findViewById(R.id.addButton);
        profileItems = new ArrayList<>(2);
        createFromFile();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        populateListView();

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        writeToFile();
    }

    private void createFromFile()
    {
        try
        {
            BufferedReader buffer = new BufferedReader(new FileReader("profile"));
            String line = buffer.readLine();
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
                line = buffer.readLine();

            }
            buffer.close();
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
            FileOutputStream output = openFileOutput("profile", Context.MODE_PRIVATE);
            int i=0;
            for(ArrayList<String> item : profileItems)
            {
                switch(i)
                {
                    case 0: output.write("music\n".getBytes()); break;
                    case 1: output.write("movie\n".getBytes()); break;
                }
                for(String str : item)
                {
                    output.write((str + '\n').getBytes());
                }
                i++;
            }
            output.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void populateListView()
    {
        int i=0;
        ArrayList<String> showList = new ArrayList<>();
        for(ArrayList<String> item : profileItems)
        {
            StringBuilder sb = new StringBuilder();
            switch(i)
            {
                case 0: sb.append("Musik: ,"); break;
                case 1: sb.append("Film: ,"); break;
            }
            for(String str : item)
            {
                sb.append(str);
            }
            showList.add(sb.toString());
            i++;
        }
        profileAdapter = new ProfileAdapter(this, showList);
        listView.setAdapter(profileAdapter);
    }

    public ArrayList<ArrayList<String>> getProfile()
    {
        return profileItems;
    }
}
