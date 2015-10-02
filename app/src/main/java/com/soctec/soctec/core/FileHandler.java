package com.soctec.soctec.core;

import android.content.Context;
import android.content.res.Resources;
import android.media.Image;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * FileHandler handles all reading and writing of files.
 * @singleton true
 * @author Joakim Schmidt
 * @version 1.0
 */
public class FileHandler
{
    private static FileHandler ourInstance = new FileHandler();
    public static FileHandler getInstance()
    {
        return ourInstance;
    }

    private Context context;

    /**
     * Constructs an empty FileHandler
     */
    private FileHandler()
    {

    }

    /**
     * Set FileHandlers Context to argument
     * @param context Context to use for operations
     */
    public void setContext(Context context)
    {
        this.context = context;
    }

    /**
     * Reads and returns all lines from argument file
     * Lines beginning with # are ignored
     * @param filename name of file to read
     * @return list containing all lines read from file
     */
    public ArrayList<String> readFile(String filename)
    {
        ArrayList<String> fromFile = new ArrayList<>();
        try
        {
            BufferedReader readBuffer = new BufferedReader(new FileReader(filename));
            String line = readBuffer.readLine();
            while(line != null)
            {
                //For commenting in the file
                if(line.charAt(0) == '#')
                    continue;
                fromFile.add(line);
                line = readBuffer.readLine();
            }
            readBuffer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return fromFile;
    }

    /**
     * Reads a String-Array from a resource
     * @param resID ID of the resource to read from
     * @return list of Strings found in resource
     */
    public ArrayList<String> readFile(int resID)
    {
        ArrayList<String> fromFile = new ArrayList<>();

        Resources res = context.getResources();
        String[] items = res.getStringArray(resID);
        fromFile.addAll(Arrays.asList(items));

        return fromFile;
    }

    /**
     * Writes data to file
     * @param filename name of file to read
     * @param data data to write
     * @param <T> type of data
     * @return true if successfully wrote data
     */
    public <T> boolean writeFile(String filename, ArrayList<T> data)
    {
        try
        {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(filename));

            for(T item : data)
            {
                if(item instanceof String)
                {
                    buffer.write((String)item);
                }
                else
                {
                    for(String subItem : (ArrayList<String>)item)
                    {
                        buffer.write(subItem);
                    }
                }
            }
            buffer.close();
            return true;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns an ID for the wanted resource
     * @param name name of the resource
     * @param type type of resource
     * @return ID of the resource
     */
    public int getResourceID(String name, String type)
    {
        return context.getResources().getIdentifier(name, type,context.getPackageName());
    }
}
