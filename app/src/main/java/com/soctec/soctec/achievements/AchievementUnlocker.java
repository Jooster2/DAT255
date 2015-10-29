package com.soctec.soctec.achievements;

import android.util.Log;
import android.util.Pair;

import com.soctec.soctec.core.MainActivity;
import com.soctec.soctec.utils.FileHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
/**
 * Handles created achievements that are not yet completed, checks for completion
 * @author Carl-Henrik Hult, Joakim Schmidt
 * @version 2.3
 */
public class AchievementUnlocker implements Observer
{
    private static final String SAVE_FILE = "unlocker.sav";

    private ArrayList<Achievement> unlockableAchievements;
    private ArrayList<Achievement> recentlyUnlocked;
    private MainActivity main;
    private Stats stats;
    private AchievementCreator creator;
    private HashMap<Demand, Achievement> livingDemands;
    private boolean legalScan;

    /**
     * Constructor that initiates the unlockableAchievements list, sets up the AchievementCreator
     * and saves a reference to Stats and MainActivity
     * @param main used to call runOnUiThread()
     * @param stats stats object created in MainActivity
     */
    public AchievementUnlocker(MainActivity main, Stats stats)
    {
        this.main = main;
        this.stats = stats;
        legalScan = false;
        unlockableAchievements = new ArrayList<>();
        recentlyUnlocked = new ArrayList<>();
        livingDemands = new HashMap<>();
        creator = new AchievementCreator();
        creator.addObserver(this);

        if(!MainActivity.TESTING)
            loadUnlockable();
        creator.createFromFile();
    }

    /**
     * Restores the unlocker from save file
     */
    @SuppressWarnings("unchecked")
    public int loadUnlockable()
    {
        FileHandler fH = FileHandler.getInstance();
        if(fH.readObject(SAVE_FILE) == null)
            return 0;
        else
        {
            LinkedList<Serializable> buffer = (LinkedList<Serializable>)fH.readObject(SAVE_FILE);
            unlockableAchievements = (ArrayList<Achievement>)buffer.removeFirst();
            recentlyUnlocked = (ArrayList<Achievement>)buffer.removeFirst();
            livingDemands = (HashMap<Demand, Achievement>)buffer.removeFirst();
            return unlockableAchievements.size();
        }

    }

    /**
     * Saves the unlocker to file
     */
    public void saveUnlockable()
    {
        LinkedList<Serializable> buffer = new LinkedList<>();
        buffer.add(unlockableAchievements);
        buffer.add(recentlyUnlocked);
        buffer.add(livingDemands);
        FileHandler fH = FileHandler.getInstance();
        fH.writeObject(SAVE_FILE, buffer);
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
                //New user was scanned
                if(!stats.setLastScanned(content))
                {
                    doRecreate();
                    stats.incScanCount();
                    legalScan = true;
                    didUnlock = checkUnlockables(Demand.PERSON_SCAN,
                            String.valueOf(stats.getScanCount()));

                }
                //The same user was scanned again
                else
                {
                    //To make the JUnit tests work, set the second argument to 'content' instead
                    legalScan = false;
                    receiveEvent(Demand.TOTAL_TIME_TALKED,
                            String.valueOf(stats.getTimeTalked()));
                    receiveEvent(Demand.LONGEST_TALK_STREAK,
                            String.valueOf(stats.getLongestTalkStreak()));
                    receiveEvent(Demand.CURRENT_TALK_STREAK,
                            String.valueOf(stats.getCurrentTalkStreak()));

                }
                break;
            case Demand.BUS_RIDE:
                didUnlock = checkUnlockables(Demand.BUS_RIDE, content);
                break;
            case Demand.TOTAL_TIME_TALKED:
                didUnlock = checkUnlockables(Demand.TOTAL_TIME_TALKED, content);
                break;
            case Demand.LONGEST_TALK_STREAK:
                checkUnlockables(Demand.LONGEST_TALK_STREAK, content);
                break;
            case Demand.CURRENT_TALK_STREAK:
                didUnlock = checkUnlockables(Demand.CURRENT_TALK_STREAK, content);
                break;
        }
        if(didUnlock)
        {
            doRecreate();

        }
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
    }

    /**
     * Starts all Living Demands in their own Threads
     */
    public void startLivingDemands()
    {
        for(Demand demand : livingDemands.keySet())
        {
            Thread demandThread = new Thread(demand);
            demandThread.start();
        }
    }

    /**
     * Stops all Living Demand-Threads
     */
    public void stopLivingDemands()
    {
        for(Demand demand : livingDemands.keySet())
            demand.shutdown();
    }

    /**
     * Checks Living Demands for completion (See checkUnlockables for more info).
     * This method always runs on MainActivity's UI Thread.
     * @param demand the Demand to check
     * @param value the value that is compared to the Demands requirement
     */
    public void checkLivingDemand(Demand demand, String value)
    {
        boolean didUnlock = false;
        Achievement achievement = livingDemands.get(demand);
        if(achievement.checkDemands(demand.type, value))
        {
            demand.shutdown();
            livingDemands.remove(demand);
            if(achievement.isCompleted())
            {
                unlockableAchievements.remove(achievement);
                stats.addCompletedAchievement(achievement);
                didUnlock = true;
            }
        }
        if(didUnlock)
            main.checkAchievementChanges();
    }

    /**
     * Does a check of Demands of all Achievements in unlockableAchievements and removes
     * the Demands that met the requirements specified in parameters. If a Demand is removed,
     * does a check if the owning Achievement is completed.
     * Any completed Achievement is added to the recentlyUnlocked list.
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
            Log.i("icomera", "Checking: " + achievement.getName());
            if(achievement.checkDemands(type, content))
            {
                if(achievement.isCompleted())
                {
                    Log.i("icomera", "unlocking");
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
     * Returns whether the last scan made was legal, i.e: the person was not scanned recently before
     * @return true if recently scanned
     */
    public boolean wasLegalScan()
    {
        return legalScan;
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
     * Receives data from classes that it observes, if that data is class {@link Achievement}
     * the method adds data to {@link AchievementUnlocker unlockableAchievements} -list.
     * @param observable is either AchievementCreator or Demand
     * @param data data received from classes that {@link AchievementUnlocker} observes.
     */
    @Override
    public void update(Observable observable, Object data)
    {
        if(data instanceof Achievement)
        {
            Achievement achievement = (Achievement)data;
            if(!unlockableAchievements.contains(achievement) &&
                    !stats.getAchievements().contains(achievement))
            {
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
        }
        else if(observable instanceof Demand && data instanceof Pair)
        {
            // Must be final due to passing to another Thread
            final Pair pair = (Pair)data;
            main.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Demand demand = (Demand)pair.first;
                    String value = (String)pair.second;
                    checkLivingDemand(demand, value);
                }
            });
        }
    }

    /**
     * Only for testing purposes
     * @param achi definition of Achievement to create
     */
    public void createTestAch(String achi)
    {
        creator.createTestAch(achi);
    }
}
