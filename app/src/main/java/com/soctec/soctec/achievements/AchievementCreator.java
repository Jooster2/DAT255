package com.soctec.soctec.achievements;

import com.soctec.soctec.core.FileHandler;

import java.util.ArrayList;
import java.util.Observable;
/**
 * An AchievementCreator creates Achievement objects on request.
 * @author Joakim Schmidt
 * @version 2.0
 */
public class AchievementCreator extends Observable
{

    /**
     * Constructs an AchievementCreator
     */
    public AchievementCreator()
    {
    }

    public Achievement createAchievement(String[] def)
    {
        // Arguments are: Name, Points, Image-name, ID, Type-name
        Achievement achievement = new Achievement(def[0], Integer.parseInt(def[1]),
                def[2], def[3], def[4]);
        for(int i=5; i<def.length; i++)
        {
            String[] demand = def[i].split(":");
            // def[4] is type (SIN/INF/COL)
            switch(def[4])
            {
                case "SIN":
                    achievement.createDemand(demand[0], Integer.parseInt(demand[1]));
                    break;
                case "INF":
                    achievement.createDemand(demand[0], Integer.parseInt(demand[1]),
                            demand[2], getIDNumber(def[3]));
                    break;
                case "COL":
                    FileHandler fH = FileHandler.getInstance();
                    // See achievement_definitions.xml for explanation
                    String[] requirements = demand[1].split("/");
                    for(String req : requirements)
                    {
                        int resID = fH.getResourceID(req, "string");
                        String ID = fH.readString(resID);
                        achievement.createDemand(demand[0], ID);
                    }
                    break;
            }
        }
        return achievement;
    }
    /**
     * Creates an Achievement object of type CounterAchievement
     * @param data the data describing the Achievement
     * @return the newly created Achievement
     */
    /*public CounterAchievement createCounterAchievement(String[] data)
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
    }*/


    /**
     * Creates an Achievement object of type CollectionAchievement
     * @param data the data describing the Achievement
     * @return the newly created Achievement
     */
    /*public CollectionAchievement createCollectionAchievement(String[] data)
    {
        CollectionAchievement achievement = new CollectionAchievement(data[0],
                Integer.parseInt(data[1]), data[2], data[3]);

        return achievement;
    }*/

    /**
     * Attempts to create multiple Achievements from file.
     * New Achievement objects are sent out via Observable.notifyAll()
     * @see Observable
     */
    public void createFromFile()
    {
        FileHandler fH = FileHandler.getInstance();
        ArrayList<String> fromFile = fH.readArray(fH.getResourceID("Achievements", "array"));
        for(String item : fromFile)
        {
            String[] definition = item.split(", ");
            Achievement achievement = createAchievement(definition);
            setChanged();
            notifyObservers(achievement);
        }

        /*fromFile = fH.readFile(fH.getResourceID("collectionAchievements", "array"));
        for(String item : fromFile)
        {
            String[] data = item.split(", ");
            CollectionAchievement achievement = createCollectionAchievement(data);
            setChanged();
            notifyObservers(achievement);
        }*/
    }

    /**
     * Creates a basic Achievement for testing purposes
     */
    public void createTestAch(String line)
    {


        String[] data = line.split(", ");


        //CounterAchievement achievement = createCounterAchievement(data);
        Achievement achievement = createAchievement(data);

        setChanged();
        notifyObservers(achievement);


    }

    /**
     * Used to create a new Achievement based on an old Achievement
     * This can also be used to recreate a copy of the old Achievement
     * @param achievement an old Achievement
     */
    public void recreateAchievement (Achievement achievement)
    {
        String[] data = achievement.getAllData();
        if(achievement.getType().equals("INF"))
            data[3] = incID(data[3]);
        Achievement achi = createAchievement(data);
        /*if(achievement instanceof CounterAchievement)
            achi = createCounterAchievement(data);
        else //if(achievement instanceof CollectionAchievement)
            achi = createCollectionAchievement(data);*/
        setChanged();
        notifyObservers(achi);

    }

    /**
     * Creates a new ID based on the old one, by incrementing the number by one.
     * @param ID the ID to be incremented
     * @return the new, incremented, ID
     */
    private String incID(String ID)
    {
        //Splits into a limited array (max length 2). First letters, then numbers
        String[] splitted = ID.split("(?=[^a-zA-Z])", 2);
        return splitted[0] + String.valueOf(Integer.parseInt(splitted[1])+1);
    }

    /**
     * Extracts the number part of the ID
     * @param ID ID to be extracted from
     * @return the number found in ID
     */
    private int getIDNumber(String ID)
    {
        String[] splitted = ID.split("(?=[^a-zA-Z])", 2);
        return Integer.parseInt(splitted[1]);
    }
}
