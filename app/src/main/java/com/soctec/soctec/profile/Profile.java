package com.soctec.soctec.profile;

import com.soctec.soctec.core.FileHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jooster on 9/29/15.
 * Static class for all profile-related data
 */
public class Profile
{
    private static ArrayList<ArrayList<String>> profileItems;
    private static String userCode = "";
    public static final int NR_OF_CATEGORIES = 5;

    /**
     * Sets the User Code to argument
     * @param code the new User Code
     */
    public static void setUserCode(String code)
    {
        userCode = code;
    }

    /**
     * Returns the current User Code
     * @return the current User Code
     */
    public static String getUserCode()
    {
        return userCode;
    }

    /**
     * Sets the profile data to argument
     * @param profile the new profile data
     */
    public static void setProfile(ArrayList<ArrayList<String>> profile)
    {
        profileItems = profile;
    }

    /**
     * Initializes the profile with data from the saved file
     */
    public static void initProfile()
    {
        profileItems = new ArrayList<>();
        ArrayList<String> tmpList = FileHandler.getInstance().readFile("profile");
        //Check if app is started for the first time
        if(tmpList.size() == 0)
            return;
        for(int i = 1; i < NR_OF_CATEGORIES*2; i+=2)
        {
            profileItems.add(new ArrayList<>(Arrays.asList(tmpList.get(i).split(","))));
        }
    }

    /**
     * Returns the current profile as an ArrayList
     * @return the current profile ArrayList
     */
    public static ArrayList<ArrayList<String>> getProfile()
    {
        return profileItems;
    }

    /**
     * Returns the current profile as a String
     * @return the current profile as a String
     */
    public static String getProfileString()
    {
        return profileItems.toString();
    }
}
