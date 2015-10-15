package com.soctec.soctec.achievements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
/**
 * Stats contains information about game progress
 * @author Joakim Schmidt
 * @version 1.0
 */
public class Stats implements Serializable
{
    private static final long serialVersionUID = 8L;

    private int points;
    private int scanCount;
    private int ratingPos;
    private int ratingNeg;
    private int timeTalked;
    private String lastScanned;
    private LinkedList<Achievement> lastCompletedAchievements;
    private ArrayList<Achievement> completedAchievements;
    private LinkedList <UserPair> listRecentScans;
    private long lastScannedTime;
    private boolean talkDone = false;
    private boolean canNotRate = true;

    /**
     * Constructs an empty Stats object
     */
    public Stats()
    {
        lastScannedTime = 0;
        lastScanned = "";
        lastCompletedAchievements = new LinkedList<>();
        completedAchievements = new ArrayList<>();
        listRecentScans = new LinkedList<>();
        //TODO read db
    }

    public boolean hasRated()
    {
        return canNotRate;
    }

    public void setCanNotRate(boolean rate)
    {
        canNotRate = rate;
    }

    public int getTimeTalked()
    {
        return timeTalked;
    }
    /**
     * Returns the users points
     * @return the users points
     */
    public int getPoints()
    {
        return points;
    }

    /**
     * Returns the users positive rating
     * @return the users positive rating
     */
    public int getRatingPos()
    {
        return ratingPos;
    }

    /**
     * Returns the users negative rating
     * @return the users negative rating
     */
    public int getRatingNeg()
    {
        return ratingNeg;
    }

    /**
     * Returns the users number of times rated
     * @return the users number of times rated
     */
    public int getTimesRated()
    {
        return ratingNeg+ratingPos;
    }

    /**
     * Returns the users calculated rating
     * @return the users calculated rating
     */
    public float getRating()
    {
        return (float)ratingPos/(float)(ratingPos+ratingNeg);
    }

    /**
     * Returns a list of the users completed Achievements
     * @return a list containing the users completed Achievements
     */
    public ArrayList<Achievement> getAchievements()
    {
        return completedAchievements;
    }

    /**
     * Returns the total number of scans
     * @return the total number of scans
     */
    public int getScanCount()
    {
        return scanCount;
    }

    /**
     * Returns the last scanned User Code
     * @return the last scanned User Code
     */
    public String getlastScanned (){return lastScanned;}

    /**
     * Increases the total number of scans by one
     */
    public void incScanCount()
    {
        scanCount++;
    }

    /**
     * Sets the last scanned User Code to argument.
     * Calulates time talked if argument and last scanned User Code are equal.
     * @param newScan newly scanned User Code
     */
    public void setLastScanned(String newScan)
    {
        removeOldScans();

        if(lastScanned.equals(newScan) && talkDone == false && lastScannedTime != 0)
        {
            timeTalked += (System.currentTimeMillis() - lastScannedTime) / 1000;
            talkDone = true;
            lastScannedTime = System.currentTimeMillis();
        }
        else if(!lastScanned.equals(newScan))
        {
            lastScannedTime = System.currentTimeMillis();
            lastScanned = newScan;
            talkDone = false;
        }
        else if (lastScanned.equals(newScan) && talkDone == true)
        {
            timeTalked+= (System.currentTimeMillis() - lastScannedTime) / 1000;
        }
        addRecentScan(System.currentTimeMillis(), newScan);
    }

    /**
     * Sets the positive rating to rating
     * @param rating The new rating
     */
    public void setRatingPos(int rating)
    {
        ratingPos = rating;
    }

    /**
     * Sets the negative rating to rating
     * @param rating The new rating
     */
    public void setRatingNeg(int rating)
    {
        ratingNeg = rating;
    }

    /**
     * Increases the users points by argument
     * @param pts amount of points to add
     */
    private void addPoints(int pts)
    {
        points += pts;
    }

    /**
     * Adds an Achievement to the users list of completed Achievements
     * @param achievement achievement to be added
     */
    public void addCompletedAchievement(Achievement achievement)
    {
        completedAchievements.add(achievement);
        addPoints(achievement.getPoints());
        lastCompletedAchievements.add(achievement);

    }

    public LinkedList<Achievement> getLastCompleted()
    {
        LinkedList<Achievement> temp = new LinkedList<>();
        temp.addAll(lastCompletedAchievements);
        lastCompletedAchievements.clear();
        return temp;
    }

    public void removeOldScans ()
    {
        Iterator it = listRecentScans.iterator();
        while (it.hasNext())
        {
            UserPair element = (UserPair) it.next();
            //two hours :7200000
            if((System.currentTimeMillis() - element.getScanTime()) >= 60000)
                it.remove();
            else
                break;
        }
    }
    public boolean isScannedRecently (String lastScanned)
    {
        for (UserPair pair :listRecentScans)
        {
            if (pair.getScannedUser().equals(lastScanned))
                return true;
        }
        return false;
    }

    public void addRecentScan(long lastScannedTime, String lastScanned)
    {
        if (!isScannedRecently(lastScanned))
            listRecentScans.addLast (new UserPair(lastScannedTime, lastScanned));
    }



    public class UserPair implements Serializable
    {
        private static final long serialVersionUID = 7L;
        long scanTime;
        String scannedUser;
        public UserPair (long scanTime, String scannedUser)
        {
            this.scanTime = scanTime;
            this.scannedUser = scannedUser;
        }

        public long getScanTime()
        {
            return scanTime;
        }

        public String getScannedUser()
        {
            return scannedUser;
        }
    }

}
