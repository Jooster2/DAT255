package com.soctec.soctec.achievements;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
/**
 * Achievements can be configured many different ways and contains Demands which must be
 * completed to complete the Achievement.
 * @author Carl-Henrik Hult, Joakim Schmidt
 * @version 2.3
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
     * @param name the name of the achievement
     * @param points amount of points that the achievement is worth
     * @param imageName the filename of the image that goes with the achievement
     * @param id an internal ID, must be unique
     * @param type the type of the achievement (SIN/INF/COL/API)
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
     * Method used by {@link AchievementCreator} to create a {@link Demand} for the achievement
     * @param type type of demand
     * @param requirement requirement for completion
     * @param extraPrimary extra data for constructing demand (equations, sensors etc)
     * @param extraSecondary extra data for constructing demand (mostly Vin Number)
     * @param detail numerical extra, mostly used API type demands
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
     * Checks and removes a single Demand that meet the requirements specified in parameters
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
            Log.i("icomera", "Requirement: " + element.requirement);
            boolean timeType = false;
            for(int i : Demand.TIME_TYPES)
                timeType |= i == demandType;
            //Standard check, correct type, and correct value?
            if(!timeType &&
                    element.type == demandType &&
                    element.requirement.equals(demandContent))
            {
                Log.i("icomera", "demand equaled");
                it.remove();
                completedDemands.add(element);
                return true;
            }
            //Check for TIME_TYPES Demands, because they are very rarely equal
            else if(timeType &&
                    element.type == demandType &&
                    Integer.parseInt(demandContent) >= Integer.parseInt(element.requirement))
            {
                Log.i("icomera", "demand equaled");
                it.remove();
                if(demandType == Demand.LONGEST_TALK_STREAK)
                {
                    Log.i("recreation", "Setting up for recreation: " + element.requirement);
                    element.requirement = demandContent;
                    Log.i("recreation", "Set to: " + element.requirement);
                }
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

    @Override
    public boolean equals(Object otherObject)
    {
        boolean isEqual = false;
        if(otherObject instanceof Achievement && otherObject != null)
        {
            Achievement other = (Achievement)otherObject;
            isEqual |= this.id.equals(other.id);
            if(this.type.equals("INF"))
            {
                isEqual |= (getIDNumber(this.id) < getIDNumber(other.id) &&
                        getIDChars(this.id).equals(getIDChars(other.id)));
            }

        }
        return isEqual;
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

    /**
     * Extracts the text part of the ID
     * @param ID ID to be extracted from
     * @return the chars found in ID
     */
    private String getIDChars(String ID)
    {
        String[] splitted = ID.split("(?=[^a-zA-Z])", 2);
        return splitted[0];
    }

}
