package com.soctec.soctec;

import com.soctec.soctec.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
    private ArrayList<ArrayList<String>> profile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        profile = new ArrayList<>(2);
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
            InputStream is = context.getResources().openRawResource(R.raw.Profile);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            String line = buffer.readLine();
            ArrayList<String> currentList = null;
            while(line != null)
            {
                //Easy solution, may exist nicer ones
                switch(line)
                {
                    case "music": currentList = profile.get(0); break;
                    case "movie": currentList = profile.get(1); break;
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
            //TODO for each element in listview write to file, seperate with "music", "movie" etc
            OutputStreamWriter output = context.getResources().openRawResource(R.raw.Profile);
            output.write();
            output.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void populateListView()
    {
        //TODO this is called onStart and should put the info from the profile list into the
        //TODO listview
    }

    public ArrayList<ArrayList<String>> getProfile()
    {
        return profile;
    }
}
