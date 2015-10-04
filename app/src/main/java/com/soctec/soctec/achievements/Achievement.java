package com.soctec.soctec.achievements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Carl-Henrik Hult on 2015-09-22.
 * An abstract superclass for all achievements.
 */
public class Achievement
{
    private String name;
    private int points;
    private String imageName;
    private String id;
    private String type;
    private ArrayList<Demand> demands;


    /**
     *
     * @param name  The name of the achievement.
     * @param points    The points that the achievement is worth.
     * @param imageName The filename/name of the image that goes with the achievement.
     * @param id    An internal ID
     * @param type the type of the achievement (SIN/INF)
     */
    public Achievement(String name, int points, String imageName, String id, String type)
    {
        this.name = name;
        this.points = points;
        this.imageName = imageName;
        this.id = id;
        this.type = type;
        demands = new ArrayList<>();
    }
    /**
     * Method used by {@link AchievementCreator createCounterAchievement(String[] data)} to create
     * a demand for an achievement.
     * @param type the type of the demand
     * @param amount    the amount of counts before unlocked.
     */
    public void createDemand(String type, int amount)
    {
        demands.add(new Demand(type, amount));
    }

    /**
     * Method used by {@link AchievementCreator createCounterAchievement(String[] data)} to create
     * a demand for an achievement.
     * @param type the type of the demand
     * @param amount the amount of counts before unlocked
     * @param equation equation used for to calculate next demand in infinite achievements
     */
    public void createDemand(String type, int amount, String equation, int cycle)
    {
        demands.add(new Demand(type, amount, equation, cycle));
    }

    /**
     * Method used by {@link AchievementCreator createCounterAchievement(String[] data)} to create
     * a demand for an achievement.
     * @param req Requirement for completion
     */
    public void createDemand(String type, String req)
    {
        demands.add(new Demand(type, req));
    }

    /**
     * Returns a list of all Demands
     * @return a list of all Demands
     */
    public ArrayList<Demand> getDemands()
    {
        return demands;
    }




    /**
     * Returns the name of the achievement.
     * @return  name of the achievement.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the points that the achievement is worth.
     * @return  points that the achievement is worth.
     */
    public int getPoints()
    {
        return points;
    }

    /**
     * Returns the filename/name of the image that goes with the achievement.
     * @return filename/name of the image that goes with the achievement.
     */
     public String getImageName()
    {
        return imageName;
    }

    /**
     * Returns the internal ID for the achievement.
     * @return  internal ID for the achievement.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the type of the Achievement (SIN/INF)
     * @return the type of the Achievement (SIN/INF)
     */
    public String getType()
    {
        return type;
    }

    /**
     * Returns an Array containing basic information used to build this Achievement
     * @return an Array containing basic information used to build this Achievement
     */
    /*public String[] getAllData()
    {
        String[] data = {name, String.valueOf(points), imageName, id, type};
        return data;
    }*/
    /**
     * Returns an Array containing all information used to build this Achievement
     * @return an Array containing all information used to build this Achievement
     */
    public String[] getAllData()
    {
        //ArrayList<String> data = new ArrayList<>( Arrays.asList(super.getAllData()));
        ArrayList<String> data = new ArrayList<>();
        Collections.addAll(data, name, String.valueOf(points), imageName, id, type);
        for(Demand demand : demands)
        {
            //data.add(demand.type + ":" + demand.amount + ":" + demand.equation);
            if(type.equals("SIN"))
                data.add(demand.type + ":" + demand.amount);
            else if(type.equals("INF"))
                data.add(demand.type + ":" + demand.amount + ":" + demand.equation);
            else
                data.add(demand.type + ":" + demand.requirement);
        }
        return data.toArray(new String[data.size()]);
    }

}
