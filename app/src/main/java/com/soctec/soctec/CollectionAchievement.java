package com.soctec.soctec;

import java.util.List;
/**
 * Created by MSI on 2015-09-22.
 */
public class CollectionAchievement extends Achievement
{
    private List<CollectionDemand> remainingList;
    private List <CollectionDemand> finishedList;
    public CollectionAchievement(String name, int points, String imageName, String id)
    {
        super (name, points,imageName, id);
    }

    public void createDemands (String item)
    {
        remainingList.add(new CollectionDemand (item));
    }
}
