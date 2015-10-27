package com.soctec.soctec.profile;

import com.soctec.soctec.R;
import com.soctec.soctec.utils.FileHandler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Activity for easy configuration of your profile through TextFields
 * @author Joakim Schmidt
 * @version 1.0
 */
public class ProfileActivity extends Activity
{
    private ArrayList<String> profileItems;
    private EditText[] textFields = new EditText[Profile.NR_OF_CATEGORIES];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textFields[0] = (EditText) findViewById(R.id.text_field1);
        textFields[1] = (EditText) findViewById(R.id.text_field2);
        textFields[2] = (EditText) findViewById(R.id.text_field3);
        textFields[3] = (EditText) findViewById(R.id.text_field4);
        textFields[4] = (EditText) findViewById(R.id.text_field5);
        //TODO add more

        createListFromFile();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    /**
     * Saves the current state of the profile into the Profile class.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        //Save to profileItems
        profileItems = new ArrayList<>();
        for(EditText text : textFields)
        {
            profileItems.add(text.getText().toString());
        }
        //Save to Profile class
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
        FileHandler.getInstance().writeObject("profile.prof", profileItems);
    }

    /**
     * Reads data from file and stores it in the local list.
     */
    @SuppressWarnings("unchecked")
    private void createListFromFile()
    {
        ArrayList<String> tmpList = (ArrayList<String>)
                FileHandler.getInstance().readObject("profile.prof");

        //When app is launched for the first time, or when testing with new categories
        if(tmpList == null || tmpList.size() < Profile.NR_OF_CATEGORIES)
        {
            profileItems = new ArrayList<>();
            for(int i = 0; i < Profile.NR_OF_CATEGORIES; i++)
                profileItems.add("");
        }
        else
            profileItems = tmpList;

        for(int i = 0; i < Profile.NR_OF_CATEGORIES; i++)
        {
            textFields[i].setText(profileItems.get(i));
        }
    }

    public void onFinishedClick(View view){ finish(); }
}
