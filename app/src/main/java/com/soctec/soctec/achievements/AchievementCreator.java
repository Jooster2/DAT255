package com.soctec.soctec.achievements;

import android.content.Context;

import com.soctec.soctec.core.FileHandler;

import java.util.ArrayList;
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
        //Arguments are (String Name, int points, String img, String id, String type)
        CounterAchievement achievement = new CounterAchievement(data[0], Integer.parseInt(data[1]), data[2], data[3], data[4]);
        if(data[4].equals("SIN"))
            for(int i=5; i<data.length; i++)
            {
                String[] demand = data[i].split(":");
                achievement.createDemand(demand[0], Integer.parseInt(demand[1]));
            }
        else if(data[4].equals("INF"))
        {
            for(int i = 5; i < data.length; i++)
            {
                String[] demand = data[i].split(":");
                achievement.createDemand(demand[0], Integer.parseInt(demand[1]), demand[2]);
            }
        }
        return achievement;
    }


    /**
     * Creates an Achievement object of type CollectionAchievement
     * @param data the data describing the Achievement
     * @return the newly created Achievement
     */
    public CollectionAchievement createCollectionAchievement(String[] data)
    {
        CollectionAchievement achievement = new CollectionAchievement(data[0],
                Integer.parseInt(data[1]), data[2], data[3]);
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
        FileHandler fH = FileHandler.getInstance();
        ArrayList<String> fromFile = fH.readFile(fH.getResourceID("counterAchievements", "values"));
        for(String item : fromFile)
        {
            String[] data = item.split(", ");
            CounterAchievement achievement = createCounterAchievement(data);
            setChanged();
            notifyObservers(achievement);
        }

        fromFile = fH.readFile(fH.getResourceID("collectionAchievements", "values"));
        for(String item : fromFile)
        {
            String[] data = item.split(", ");
            CollectionAchievement achievement = createCollectionAchievement(data);
            setChanged();
            notifyObservers(achievement);
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
     * Used to create a new Achievement based on an old Achievement
     * This can also be used to recreate a copy of the old Achievement
     * @param achievement an old Achievement
     */
    public void createAchievement (Achievement achievement)
    {
        String[] data = achievement.getAllData();
        Achievement achi;
        if(achievement instanceof CounterAchievement)
            achi = createCounterAchievement(data);
        else //if(achievement instanceof CollectionAchievement)
            achi = createCollectionAchievement(data);
        setChanged();
        notifyObservers(achi);

    }
}
