package com.soctec.soctec;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
/**
 * Created by Carl-Henrik Hult on 2015-09-22.
 */


public class AchievementUnlocker extends Observable
{
    public static final int SCAN_PERSON = 1;

    ArrayList<Achievement> unlockableAchievements;
    //Stats currentStats;

    public AchievementUnlocker()
    {
        unlockableAchievements = new ArrayList<Achievement>();

    }
    public void insertAchievement(Achievement achievement)
    {
        unlockableAchievements.add(achievement);
    }
    public void receiveEvent (int type, String content)
    {
        if (type == SCAN_PERSON)
        {
            Stats.setLastScanned (content);
            Stats.incScanCount();

            for (Achievement achievement : unlockableAchievements)
            {
                CounterAchievement cAchievement = (CounterAchievement) achievement;
                boolean achievementUnlocked = true;
                for(CounterDemand demand : cAchievement.getDemands())
                {
                    if(demand.type.equals("SCAN"))
                    {
                        if(demand.amount > Stats.getScanCount())
                        {
                            achievementUnlocked = false;
                            break;
                        }
                    }
                }
                if (achievementUnlocked == true)
                    Stats.addUnlockedAchievement (cAchievement);
            }
        }
    }
}
