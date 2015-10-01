package com.soctec.soctec.achievements;

import android.content.Context;

import com.soctec.soctec.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Observable;
/**
 * An AchievementCreator creates Achievement objects on request.
 * @author Joakim Schmidt
 * @version 1.1
 */
public class AchievementCreator extends Observable
{
    private Context context;

    /**
     * Constructs an AchievementCreator
     * @param context the app's Context object
     */
    public AchievementCreator(Context context)
    {

        this.context = context;

    }

    /**
     * Creates an Achievement object of type CounterAchievement
     * @param data the data describing the Achievement
     * @return the newly created Achievement
     */
    public CounterAchievement createCounterAchievement(String[] data)
    {
        //Arguments are (String Name, int points, String img, String id)
        CounterAchievement achievement = new CounterAchievement(data[1], Integer.parseInt(data[2]), data[3], data[4]);
        if(data[5].equals("SIN"))
            for(int i=6; i<data.length; i++)
            {
                String[] demand = data[i].split(":");
                achievement.createDemand(demand[0], Integer.parseInt(demand[1]));
            }
        else if(data[5].equals("INF"))
            return achievement;
        //TODO create demand of infinite type
        return achievement;
    }

    /**
     * Creates an Achievement object of type CollectionAchievement
     * @param data the data describing the Achievement
     * @return the newly created Achievement
     */
    public CollectionAchievement createCollectionAchievement(String[] data)
    {
        CollectionAchievement achievement = new CollectionAchievement(data[1], Integer.parseInt(data[2]), data[3],
                data[4]);
        //TODO everything...
        return achievement;
    }

    /**
     * Attempts to create multiple Achievements from file.
     * New Achievement objects are sent out via Observable.notifyAll()
     * @see Observable
     */
    public void createFromFile()
    {
        try
        {
            //TODO Input stream gives nullpointerexception (can't read file)
            InputStream is = context.getResources().openRawResource(R.raw.achievement_definitions);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            String line = buffer.readLine();
            while(line != null)
            {
                if(line.charAt(0) == '#')
                    continue;

                String[] data = line.split(", ");
                if(data[0].equals("CNT"))
                {
                    CounterAchievement achievement = createCounterAchievement(data);

                    setChanged();
                    notifyObservers(achievement);
                }
                else if(data[0].equals("COL"))
                {
                    CollectionAchievement achievement = createCollectionAchievement(data);
                    setChanged();
                    notifyObservers(achievement);
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

    /**
     * Creates a basic Achievement for testing purposes
     */
    public void createTestAch()
    {
        String line = "CNT, First Scan!, 50, someimg, S1, SIN, P_SCAN:1";

        String[] data = line.split(", ");
        if(data[0].equals("CNT"))
        {
                CounterAchievement achievement = createCounterAchievement(data);

                setChanged();
                notifyObservers(achievement);
        }
        else if(data[0].equals("COL"))
        {
                CollectionAchievement achievement = createCollectionAchievement(data);
                setChanged();
                notifyObservers(achievement);
        }
    }

    /**
     * Used to create a new Achievement based on argument
     * @param achievement an old Achievement
     */
    public void createAchievement (Achievement achievement)
    {

    }
}
