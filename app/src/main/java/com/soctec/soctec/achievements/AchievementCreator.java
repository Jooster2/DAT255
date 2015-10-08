package com.soctec.soctec.achievements;

import com.soctec.soctec.utils.FileHandler;

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
        // Arguments are: Name, FlavorText, Points, Image-name, ID, Type-name
        Achievement achievement = new Achievement(def[0], def[1], Integer.parseInt(def[2]),
                def[3], def[4], def[5]);
        for(int i=6; i<def.length; i++)
        {
            String[] demand = def[i].split(":");
            // def[5] is type (SIN/INF/COL)
            switch(def[5])
            {
                case "SIN":
                    achievement.createDemand(demand[0], Integer.parseInt(demand[1]));
                    break;
                case "INF":
                    achievement.createDemand(demand[0], Integer.parseInt(demand[1]),
                            demand[2], getIDNumber(def[4]));
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
    }

    /**
     * Creates a basic Achievement for testing purposes
     */
    public void createTestAch(String line)
    {
        String[] data = line.split(", ");
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
        // data[4] is ID
        if(achievement.getType().equals("INF"))
            data[4] = incID(data[4]);
        Achievement achi = createAchievement(data);
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
