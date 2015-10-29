package com.soctec.soctec.achievements;

import com.soctec.soctec.core.MainActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
/**
 * Stats contains information about game progress
 * @author Joakim Schmidt
 * @version 1.3
 */
public class Stats implements Serializable
{
    private static final long serialVersionUID = 8L;

    private int points;
    private int scanCount;
    private int ratingPos;
    private int ratingNeg;
    private int timeTalked;
    private int longestTalkStreak;
    private int currentTalkStreak;
    private String lastScanned;
    private LinkedList<Achievement> lastCompletedAchievements;
    private ArrayList<Achievement> completedAchievements;
    private LinkedList <UserPair> listRecentScans;
    private long lastScannedTime;
    private boolean canNotRate = true;

    /**
     * Constructs a Stats object
     */
    public Stats()
    {
        lastScannedTime = 0;
        longestTalkStreak = 0;
        currentTalkStreak = 0;
        lastScanned = "";
        lastCompletedAchievements = new LinkedList<>();
        completedAchievements = new ArrayList<>();
        listRecentScans = new LinkedList<>();
        //TODO read db
    }

    /**
     * Returns true if user has rated the last meeting
     * @return true if user has rated the last meeting
     */
    public boolean hasRated()
    {
        return canNotRate;
    }

    /**
     * Sets whether the user has rated to parameter
     * @param rate set to true if user has rated
     */
    public void setCanNotRate(boolean rate)
    {
        canNotRate = rate;
    }

    /**
     * Returns the total time talked
     * @return the total time talked
     */
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
    public boolean setLastScanned(String newScan)
    {
        removeOldScans();
        long systemTime = System.currentTimeMillis();
        //Must be saved here, because the else{addRecent} messes with it
        boolean scannedRecently = isScannedRecently(newScan);
        if(lastScanned.equals(newScan))
        {
            int thisTalkTime = (int)(systemTime - lastScannedTime) / 1000;
            timeTalked += thisTalkTime;
            currentTalkStreak += thisTalkTime;
            if(currentTalkStreak > longestTalkStreak)
                longestTalkStreak = currentTalkStreak;
            lastScannedTime = systemTime;
        }
        else
        {
            lastScanned = newScan;
            lastScannedTime = systemTime;
            currentTalkStreak = 0;
            addRecentScan(systemTime, newScan);
        }
        return scannedRecently;
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
     * Returns the longest time the user has talked to another user in one go
     * @return int of the time talked
     */
    public int getLongestTalkStreak()
    {
        return longestTalkStreak;
    }

    /**
     * Returns the time since the users last scan
     * @return the time since the users last scan
     */
    public int getCurrentTalkStreak()
    {
        return currentTalkStreak;
    }

    /**
     * Used only for testing purposes
     * @param time time to set
     */
    public void setLongestTalkStreak(int time)
    {
        if(MainActivity.TESTING)
            longestTalkStreak = time;
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

    /**
     * Returns a list of the last completed Achievements (since last call to this method)
     * and clears the list
     * @return list of the last completed Achievements
     */
    public LinkedList<Achievement> getLastCompleted()
    {
        LinkedList<Achievement> temp = new LinkedList<>();
        temp.addAll(lastCompletedAchievements);
        lastCompletedAchievements.clear();
        return temp;
    }

    /**
     * Cleans the list of recent scans
     */
    public void removeOldScans()
    {
        int resetTime = 7200000;
        if(MainActivity.TESTING)
            resetTime = 1000;
        Iterator it = listRecentScans.iterator();
        while (it.hasNext())
        {
            UserPair element = (UserPair) it.next();
            //two hours :7200000
            if((System.currentTimeMillis() - element.getScanTime()) >= resetTime)
                it.remove();
            else
                break;
        }
    }

    /**
     * Checks whether user has recently been scanned
     * @param lastScanned usercode
     * @return true if scanned recently
     */
    public boolean isScannedRecently(String lastScanned)
    {
        for(UserPair pair : listRecentScans)
        {
            if(pair.getScannedUser().equals(lastScanned))
                return true;
        }
        return false;
    }

    /**
     * Adds a user to the list of recently scanned
     * @param lastScannedTime the time of the scan
     * @param lastScanned usercode
     */
    public void addRecentScan(long lastScannedTime, String lastScanned)
    {
        if (!isScannedRecently(lastScanned))
            listRecentScans.addLast(new UserPair(lastScannedTime, lastScanned));
    }

    /**
     * A UserPair is used by the recently scanned list in Stats, to pair a usercode and a scantime
     * together
     */
    public class UserPair implements Serializable
    {
        private static final long serialVersionUID = 7L;
        private long scanTime;
        private String scannedUser;

        /**
         * Constructs a new UserPair with time and usercode as parameters
         * @param scanTime
         * @param scannedUser
         */
        public UserPair(long scanTime, String scannedUser)
        {
            this.scanTime = scanTime;
            this.scannedUser = scannedUser;
        }

        /**
         * Returns the time of this pair
         * @return the time of this pair
         */
        public long getScanTime()
        {
            return scanTime;
        }

        /**
         * Returns the usercode of this pair
         * @return the usercode of this pair
         */
        public String getScannedUser()
        {
            return scannedUser;
        }
    }

}
