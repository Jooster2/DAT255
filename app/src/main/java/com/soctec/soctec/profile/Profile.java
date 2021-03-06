package com.soctec.soctec.profile;

import com.soctec.soctec.utils.FileHandler;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class with static access to all profile-related data
 * @author Joakim Schmidt, David Johnsson
 * @version 2.0
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
    @SuppressWarnings("unchecked")
    public static void initProfile()
    {
        profileItems = new ArrayList<>();
        ArrayList<String> tmpList = (ArrayList<String>)
                FileHandler.getInstance().readObject("profile.prof");
        //Check if app is started for the first time
        if(tmpList == null)
        {
            for(int i = 0; i < NR_OF_CATEGORIES; i++)
            {
                profileItems.add(new ArrayList<String>());
            }
        }
        else
        {
            for (int i = 0; i < NR_OF_CATEGORIES; i ++)
            {
                profileItems.add(new ArrayList<>(Arrays.asList(tmpList.get(i).split(","))));
            }
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
