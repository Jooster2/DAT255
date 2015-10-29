package com.soctec.soctec.test;

import android.test.AndroidTestCase;
import com.soctec.soctec.profile.Profile;
import com.soctec.soctec.profile.ProfileMatchActivity;
import java.util.ArrayList;
import java.util.Arrays;
/**
 Basic test cases for the APIHeader class
 * @author Carl-Henrik
 * @version 1.0
 */
public class ProfileTest extends AndroidTestCase
{
    /**
     * Tests the matching algorithm.
     * @throws Exception
     */
    public void testProfileMatch ()throws Exception{
        ProfileMatchActivity profileMatchActivity = new ProfileMatchActivity();
        String myTextFields[] = new String[Profile.NR_OF_CATEGORIES];
        ArrayList<String> myProfileItems;


        myTextFields[0] ="Hej" ;
        myTextFields[1] = "på";
        myTextFields[2] = "dig";
        myTextFields[3] = "fuling";
        myTextFields[4] = "saft";

        myProfileItems = new ArrayList<>();
        for(String text : myTextFields)
        {
            myProfileItems.add(text);
        }
        //Save to Profile class
        ArrayList<ArrayList<String>> myProfileList = new ArrayList<>();
        for(int i = 0; i < Profile.NR_OF_CATEGORIES; i++)
        {
            myProfileList.add(new ArrayList<>(Arrays.asList(myProfileItems.get(i).split(","))));
        }

        String otherTextFields[] = new String[Profile.NR_OF_CATEGORIES];
        ArrayList<String> otherProfileItems;


        otherTextFields[0] ="Hej" ;
        otherTextFields[1] = "på";
        otherTextFields[2] = "dig";
        otherTextFields[3] = "fuling,hopp";
        otherTextFields[4] = "öl";

        otherProfileItems = new ArrayList<>();
        for(String text : otherTextFields)
        {
            otherProfileItems.add(text);
        }
        //Save to Profile class
        ArrayList<ArrayList<String>> otherProfileList = new ArrayList<>();
        for(int i = 0; i < Profile.NR_OF_CATEGORIES; i++)
        {
            otherProfileList.add(new ArrayList<>(Arrays.asList(otherProfileItems.get(i).split(","))));
        }
        ArrayList<String>listresult =profileMatchActivity.match2(myProfileList,otherProfileList);
        int result = listresult.size();
        assertEquals(result, 4);

    }
}
