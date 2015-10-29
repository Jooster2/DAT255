package com.soctec.soctec.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * FileHandler handles all reading and writing of files including Resources
 * @author Joakim Schmidt, David Johnsson
 * @version 1.4
 */
public class FileHandler
{
    private static FileHandler instance;
    private Context context;
    private static File path;

    /**
     * Returns the instance of FileHandler
     * @return the instance of FileHandler
     */
    public static synchronized FileHandler getInstance()
    {
        if(instance == null)
            return instance = new FileHandler();
        else
            return instance;
    }

    /**
     * Set FileHandlers Context to argument
     * @param context Context to use for operations
     */
    public void setContext(Context context)
    {
        this.context = context;
        path = context.getFilesDir();
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
            BufferedReader readBuffer = new BufferedReader(
                    new FileReader(new File(path, filename)));
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
    public ArrayList<String> readArray(int resID)
    {
        ArrayList<String> fromFile = new ArrayList<>();

        Resources res = context.getResources();
        String[] items = res.getStringArray(resID);
        fromFile.addAll(Arrays.asList(items));

        return fromFile;
    }

    /**
     * Reads a string from a resource
     * @param resID ID of the resource to read from
     * @return String found in resource
     */
    public String readString(int resID)
    {
        Resources res = context.getResources();
        return res.getString(resID);
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
            File myFile = new File(path, filename);
            if(!myFile.exists()) {
                myFile.createNewFile();
                Log.i("myTag", "New file created: " + myFile.toString());
            }
            BufferedWriter buffer = new BufferedWriter(new FileWriter(myFile));

            for(T item : data)
            {
                if(item instanceof String)
                {
                    buffer.write((String) item);
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

    /**
     * Serializes a object into a file
     * @param fileName the filename to write to
     * @param obj the object to write
     */
    public void writeObject(String fileName, Serializable obj)
    {
        File myFile = new File(context.getFilesDir(), fileName);
        try
        {
            myFile.createNewFile();

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(myFile));
            oos.writeObject(obj);
            oos.flush();
            oos.close();
        }catch(IOException e)
        {
            e.printStackTrace();
            Log.e("Error", e.getMessage());
        }
    }

    /**
     * This method reads and returns a serializable object from file
     * @param fileName the filename to read from
     * @return the object read
     */
    public Object readObject(String fileName)
    {
        File myFile = new File(context.getFilesDir(), fileName);
        Object obj = null;
        try
        {
            if (!myFile.exists())
                return null;

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(myFile));
            obj = ois.readObject();
            ois.close();
        } catch(IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
            Log.e("Error", "" + e.getMessage());
        }
        return obj;
    }
}
