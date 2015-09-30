package com.soctec.soctec.achievements;

import java.util.List;
/**
 * Created by Carl-Henrik Hult on 2015-09-22.
 */
public class CollectionAchievement extends Achievement
{
    private List<CollectionDemand> remainingList;
    private List <CollectionDemand> finishedList;
    public CollectionAchievement(String name, int points, String imageName, String id)
    {
        super (name, points,imageName, id);
    }

    /**
     * Creates demands for achievements of the collection kind, and adds them to the remainingList.
     * @param item  The item in question.
     */
    public void createDemands (String item)
    {
        remainingList.add(new CollectionDemand (item));
    }
}
