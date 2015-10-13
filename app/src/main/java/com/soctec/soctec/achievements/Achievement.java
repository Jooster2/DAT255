package com.soctec.soctec.achievements;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
/**
 * Class for creating Achievements with certain properties
 * @author Carl-Henrik Hult, Joakim Schmidt
 * @version 2.2
 */
public class Achievement implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String name;
    private String flavorText;
    private int points;
    private String imageName;
    private String id;
    private String type;
    private ArrayList<Demand> demands;
    private ArrayList<Demand> completedDemands;


    /**
     * @param name The name of the achievement.
     * @param points The points that the achievement is worth.
     * @param imageName The filename/name of the image that goes with the achievement.
     * @param id An internal ID
     * @param type the type of the achievement (SIN/INF)
     */
    public Achievement(String name, String flavorText, int points, String imageName, String id, String type)
    {
        this.name = name;
        this.flavorText = flavorText;
        this.points = points;
        this.imageName = imageName;
        this.id = id;
        this.type = type;
        demands = new ArrayList<>();
        completedDemands = new ArrayList<>();
    }

    /**
     * Method used by {@link AchievementCreator createCounterAchievement(String[] data)} to create
     * a demand for an achievement.
     * @param type type of demand
     * @param requirement requirement for unlocking
     * @param extraPrimary extra data for constructing demand (equations, sensors etc)
     * @param extraSecondary extra data for constructing demand (mostly Vin Number)
     * @param detail numerical extra, mostly used for equations and API type demands
     */
    public void createDemand(int type, String requirement, String extraPrimary,
                             String extraSecondary, int detail)
    {
        demands.add(new Demand(type, requirement, extraPrimary, extraSecondary, detail));
    }



    /**
     * Returns a list of all remaining Demands
     * @return a list of all remaining Demands
     */
    public ArrayList<Demand> getDemands()
    {
        return demands;
    }

    /**
     * Returns a list of all completed Demands
     * @return a list of all completed Demands
     */
    public ArrayList<Demand> getCompletedDemands()
    {
        return completedDemands;
    }

    /**
     * Checks if the Achievements Demands are completed
     * @return true if there are no more Demands
     */
    public boolean isCompleted()
    {
        return demands.size() == 0;
    }

    /**
     * Checks and removes Demands that meet the requirements specified in parameters
     * @param demandType type of Demand
     * @param demandContent requirement of Demand
     * @return true if Demand was found and completed
     */
    public boolean checkDemands(int demandType, String demandContent)
    {
        Iterator<Demand> it = getDemands().iterator();
        while(it.hasNext())
        {
            Demand element = it.next();
            if(element.type == demandType &&
                    element.requirement.equals(demandContent))
            {
                Log.i("icomera", "demand equaled");
                it.remove();
                completedDemands.add(element);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the name of the achievement.
     * @return the name of the achievement.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the flavortext of the achievement
     * @return the flavortext of the achievement
     */
    public String getFlavorText()
    {
        return flavorText;
    }

    /**
     * Returns the points that the achievement is worth.
     * @return the points that the achievement is worth.
     */
    public int getPoints()
    {
        return points;
    }

    /**
     * Returns the filename/name of the image that goes with the achievement.
     * @return the filename/name of the image that goes with the achievement.
     */
     public String getImageName()
    {
        return imageName;
    }

    /**
     * Returns the internal ID for the achievement.
     * @return the internal ID for the achievement.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the type of the Achievement (SIN/INF/COL)
     * @return the type of the Achievement (SIN/INF/COL)
     */
    public String getType()
    {
        return type;
    }
    /**
     * Returns an Array containing all information used to build this Achievement
     * @return an Array containing all information used to build this Achievement
     */
    public String[] getAllData()
    {
        ArrayList<String> data = new ArrayList<>();
        Collections.addAll(data, name, flavorText, String.valueOf(points), imageName, id, type);
        // Add any remaining Demands
        for(Demand demand : demands)
        {
            if(type.equals("SIN"))
                data.add(demand.type + ":" + demand.requirement);
            else if(type.equals("INF"))
                data.add(demand.type + ":" + demand.requirement + ":" + demand.extraPrimary);
            else
                data.add(demand.type + ":" + demand.requirement);
        }
        // Add any completed Demands
        for(Demand demand : completedDemands)
        {
            if(type.equals("SIN"))
                data.add(demand.type + ":" + demand.requirement);
            else if(type.equals("INF"))
                data.add(demand.type + ":" + demand.requirement + ":" + demand.extraPrimary);
            else
                data.add(demand.type + ":" + demand.requirement);
        }
        return data.toArray(new String[data.size()]);
    }

}
