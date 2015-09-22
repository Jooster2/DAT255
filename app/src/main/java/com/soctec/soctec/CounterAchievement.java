package com.soctec.soctec;

import java.util.List;
/**
 * Created by MSI on 2015-09-22.
 */
public class CounterAchievement extends Achievement
{
    private List<CounterDemand> demands;
    public CounterAchievement(String name, int points, String imageName, String id)
    {
        super (name, points,imageName, id);
    }

    public void createDemands (String type, int amount)
    {
        demands.add(new CounterDemand (type, amount));
    }

}
