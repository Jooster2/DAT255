package com.soctec.soctec.achievements;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Carl-Henrik Hult on 2015-09-22.
 * A subclass to Achievement that contains information of the achievements of the counter type.
 */
public class CounterAchievement extends Achievement
{
    private ArrayList<CounterDemand> demands;

    /**
     * Directs the parameters to the superclass.
     * @param name  The name of the achievement.
     * @param points    The points that the achievement is worth.
     * @param imageName The filename/name of the image that goes with the achievement.
     * @param id    An internal ID
     */
    public CounterAchievement(String name, int points, String imageName, String id, String type)
    {
        super (name, points,imageName, id, type);
        demands = new ArrayList<CounterDemand>();
    }

    /**
     * Method used by {@link AchievementCreator createCounterAchievement(String[] data)} to create
     * a demand for an achievement.
     * @param type the type of the demand
     * @param amount    the amount of counts before unlocked.
     */
    public void createDemand (String type, int amount)
    {
        demands.add(new CounterDemand(type, amount));
    }

    /**
     * Method used by {@link AchievementCreator createCounterAchievement(String[] data)} to create
     * a demand for an achievement.
     * @param type the type of the demand
     * @param amount the amount of counts before unlocked
     * @param equation equation used for to calculate next demand in infinite achievements
     */
    public void createDemand (String type, int amount, String equation)
    {
        demands.add(new CounterDemand(type, amount, equation));
    }
    public ArrayList<CounterDemand> getDemands ()
    {
        return demands;
    }

    @Override
    public String[] getAllData()
    {
        ArrayList<String> data = (ArrayList) Arrays.asList(super.getAllData());
        for(CounterDemand demand : demands)
        {
            data.add(demand.type + ":" + demand.amount + ":" + demand.equation);
        }
        return (String[])data.toArray();
    }
}
