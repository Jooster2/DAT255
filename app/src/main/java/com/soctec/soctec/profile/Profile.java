package com.soctec.soctec.profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Created by jooster on 9/29/15.
 * Static class for all profile-related data
 */
public class Profile
{

    private static ArrayList<ArrayList<String>> profileItems = new ArrayList<>(2);
    private static String userCode = "";

    /*public Profile(String code)
    {
        profileItems = new ArrayList<>(2);
        userCode = code;

    }*/

    /**
     * Sets the User Code to parameter
     * @param code the new User Code
     */
    public static void setUserCode(String code)
    {
        userCode = code;
    }

    /**
     * @return the current User Code
     */
    public static String getUserCode()
    {
        return userCode;
    }

    /**
     * Sets
     * @param profile
     */
    public static void setProfile(ArrayList<ArrayList<String>> profile)
    {
        profileItems = profile;
    }

    public static ArrayList<ArrayList<String>> getProfile()
    {
        return profileItems;
    }

    public static String getProfileString()
    {
        return profileItems.toString();
    }
}
