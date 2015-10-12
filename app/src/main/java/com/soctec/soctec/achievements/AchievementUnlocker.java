package com.soctec.soctec.achievements;

import android.util.Pair;

import com.soctec.soctec.utils.FileHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
/**
 * Handles created achievements that are not yet completed, checks for completion
 * @author Carl-Henrik Hult, Joakim Schmidt
 * @version 2.0
 */
public class AchievementUnlocker implements Observer
{
    private static final String SAVE_FILE = "unlockableAchievements";

    ArrayList<Achievement> unlockableAchievements;
    ArrayList<Achievement> recentlyUnlocked;
    Stats stats;
    AchievementCreator creator;
    HashMap<Demand, Achievement> livingDemands;

    /**
     * Constructor that initiates the unlockableAchievements list, saves a reference to Stats and
     * AchievementCreator.
     * @param newStats
     * @param creator
     */
    public AchievementUnlocker(Stats newStats, AchievementCreator creator)
    {
        unlockableAchievements = new ArrayList<>();
        recentlyUnlocked = new ArrayList<>();
        stats = newStats;
        this.creator = creator;
        livingDemands = new HashMap<>();
    }

    /**
     * Registers a new Demand as running, and starts it in a thread
     * @param owner the Achievement the Demand belongs to
     * @param demand the Demand
     */
    public void registerLivingDemand(Achievement owner, Demand demand)
    {
        livingDemands.put(demand, owner);
        demand.addObserver(this);
        Thread demandThread = new Thread(demand);
        demandThread.start();
    }

    public void checkLivingDemand(Demand demand, String value)
    {
        Achievement achievement = livingDemands.get(demand);
        if(achievement.checkDemands(demand.type, value))
        {
            demand.running(false);
            livingDemands.remove(demand);
            if(achievement.isCompleted())
            {
                unlockableAchievements.remove(achievement);
                stats.addCompletedAchievement(achievement);
            }
        }
    }

    /**
     * Loads the current unlockableAchievements-list from file
     */
    public void loadUnlockable()
    {
        FileHandler fH = FileHandler.getInstance();
        unlockableAchievements = (ArrayList<Achievement>)fH.readObject(SAVE_FILE);
    }

    /**
     * Saves the current unlockableAchievements-list to file
     */
    public void saveUnlockable()
    {
        FileHandler fH = FileHandler.getInstance();
        fH.writeObject(SAVE_FILE, unlockableAchievements);
    }

    /**
     * Handles events based on type and causes checks of Demands to be triggered
     * @param type  What kind of event that was triggered.
     * @param content   The information that came with the event.
     */
    public void receiveEvent(int type, String content)
    {
        boolean didUnlock = false;
        switch(type)
        {
            case Demand.PERSON_SCAN:
                stats.setLastScanned(content);
                stats.incScanCount();
                didUnlock = checkUnlockables(Demand.PERSON_SCAN,
                        String.valueOf(stats.getScanCount()));
                break;
            case Demand.BUS_RIDE:
                didUnlock = checkUnlockables(Demand.BUS_RIDE, content);
                break;
        }
        if(didUnlock)
            doRecreate();
    }

    /**
     * Does a check of Demands of all Achievements in unlockableAchievements and removes
     * the Demands that met the requirements specified in parameters. If a Demand is removed,
     * does a check if that Achievement is completed.
     * @param type the type of Demand
     * @param content the requirement of the Demand
     * @return true if an Achievement was unlocked
     */
    private boolean checkUnlockables(int type, String content)
    {
        boolean didUnlock = false;
        Iterator<Achievement> it = unlockableAchievements.iterator();
        while(it.hasNext())
        {
            Achievement achievement = it.next();
            if(achievement.checkDemands(type, content))
            {
                if(achievement.isCompleted())
                {
                    it.remove();
                    stats.addCompletedAchievement(achievement);
                    recentlyUnlocked.add(achievement);
                    didUnlock = true;
                }
            }
        }
        return didUnlock;
    }

    /**
     * Recreates Achievements found in recentlyUnlocked and then clears the list
     */
    private void doRecreate()
    {
        for(Achievement achi : recentlyUnlocked)
        {
            if(achi.getType().equals("INF"))
                creator.recreateAchievement(achi);
        }
        recentlyUnlocked.clear();
    }

    /**
     * Returns the unlockableAcievments list.
     * @return  unlockableAchievements
     */
    public ArrayList<Achievement> getUnlockableAchievements()
    {
        return unlockableAchievements;
    }

    /**
     * Receives data from classes that it observes, and if  that data is class {@link Achievement}
     * the method adds data to {@link AchievementUnlocker unlockableAchievements} -list.
     * @param observable    Is in this case AchievementCreator.
     * @param data  Is data received from classes that {@link AchievementUnlocker} observes.
     */
    @Override
    public void update(Observable observable, Object data)
    {
        if(data instanceof Achievement)
        {
            Achievement achievement = (Achievement)data;
            unlockableAchievements.add(achievement);
            if(achievement.getType().equals("API"))
            {
                for(Demand demand : achievement.getDemands())
                {
                    if(demand.type == Demand.API)
                        registerLivingDemand(achievement, demand);
                }
            }
        }
        else if(observable instanceof Demand && data instanceof Pair)
        {
            Demand demand = (Demand)((Pair)data).first;
            String value = (String)((Pair)data).second;
            checkLivingDemand(demand, value);
        }
    }
}
