package com.soctec.soctec.achievements;

import java.util.ArrayList;
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
    public CounterAchievement(String name, int points, String imageName, String id)
    {
        super (name, points,imageName, id);
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
    public ArrayList<CounterDemand> getDemands ()
    {
        return demands;
    }
}
