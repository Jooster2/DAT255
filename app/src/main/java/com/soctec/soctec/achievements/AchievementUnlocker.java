package com.soctec.soctec.achievements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
/**
 * Created by Carl-Henrik Hult on 2015-09-22.
 */


public class AchievementUnlocker implements Observer
{
    public static final int SCAN_PERSON = 1;

    ArrayList<Achievement> unlockableAchievements;
    Stats currentStats;
    AchievementCreator creator;

    public AchievementUnlocker(Stats newStats, AchievementCreator creator)
    {
        unlockableAchievements = new ArrayList<Achievement>();
        currentStats = newStats;
        this.creator = creator;


    }
    public void invokeTestAchievement()
    {
        creator.createTestAch();
    }

    /**
     * Method that receives an event and handles whether the event should unlock an achievement.
     *
     * @param type  What kind of event that was triggered.
     * @param content   The information that came with the event.
     */
    public void receiveEvent (int type, String content)
    {
        if (type == SCAN_PERSON)
        {
            currentStats.setLastScanned (content);
            currentStats.incScanCount();
            /**
             * This while-loop goes through the list of unlockable achievement, and checks the
             * demands for each. If the demands for an achievement is met, that achievement gets
             * unlocked. It also gets removed from the list of unlockableAchievements
             */
            Iterator it = unlockableAchievements.iterator();
            while (it.hasNext())
            {
                CounterAchievement element =(CounterAchievement) it.next();
                boolean achievementUnlocked = true;
                for(CounterDemand demand : element.getDemands())
                {
                    if(demand.type.equals("P_SCAN"))
                    {
                        if(demand.amount > currentStats.getScanCount())
                        {
                            achievementUnlocked = false;
                            break;
                        }
                    }
                }
                if (achievementUnlocked == true)
                {
                    currentStats.addCompletedAchievement(element);
                    creator.createAchievement(element);
                    it.remove();
                }
            }
        }
    }
    public ArrayList<Achievement> getUnlockableAchievements()
    {
        return unlockableAchievements;
    }

    @Override
    public void update(Observable observable, Object data)
    {
        if (data instanceof Achievement )
        {
            unlockableAchievements.add((Achievement)data);
        }
    }
}
