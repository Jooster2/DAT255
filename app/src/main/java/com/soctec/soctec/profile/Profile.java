package com.soctec.soctec.profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Created by jooster on 9/29/15.
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

    public static void setUserCode(String code)
    {
        userCode = code;
    }

    public static String getUserCode()
    {
        return userCode;
    }

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
