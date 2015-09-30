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

    private static ArrayList<ArrayList<String>> profileItems;

    public Profile()
    {
        profileItems = new ArrayList<>(2);

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
