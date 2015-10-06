package com.soctec.soctec.achievements;

import java.util.LinkedList;
/**
 * Stats contains information about game progress
 * @author Joakim Schmidt
 * @version 1.0
 */
public class Stats
{
    private int points;
    private int scanCount;
    private int ratingPos;
    private int ratingNeg;
    private int timeTalked;
    private String lastScanned;
    private LinkedList<Achievement> completedAchievements;
    private long lastScannedTime;
    private boolean talkDone = false;

    /**
     * Constructs an empty Stats object
     */
    public Stats()
    {
        lastScannedTime = 0;
        lastScanned = "";
        completedAchievements = new LinkedList<>();
        //TODO read db
    }

    public int getTimeTalked (){
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
    public LinkedList<Achievement> getAchievements()
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
        if(lastScanned.equals(newScan) && talkDone == false && lastScannedTime != 0)
        {
            timeTalked += (System.currentTimeMillis()-lastScannedTime)/1000;
            talkDone = true;
        }
        else if(!lastScanned.equals(newScan) && talkDone == false)
        {
            lastScannedTime = System.currentTimeMillis();
            lastScanned = newScan;
        }
        else //(lastScanned.equals(newScan) && talkDone == true)
        {
            //TODO what happens here? nothing?
        }
    }

    /**
     * Increases positive rating by one
     */
    public void incRatingPos()
    {
        ratingPos++;
    }

    /**
     * Increases negative rating by one
     */
    public void incRatingNew()
    {
        ratingNeg++;
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
    }

}
