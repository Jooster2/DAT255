package com.soctec.soctec.achievements;

import java.util.LinkedList;
/**
 * Created by jooster on 9/23/15.
 */
public class Stats
{
    private int points;
    private int scanCount;
    private int ratingPos;
    private int ratingNeg;
    private int timeTalked;
    private String lastScanned;
    private LinkedList<Achievement> unlockedAchievements;
    private long lastScannedTime;
    private boolean talkDone = false;

    public Stats()
    {
        lastScanned = "";
        unlockedAchievements = new LinkedList<Achievement>();
        //TODO read db
    }

    public int getPoints()
    {
        return points;
    }
    public int getRatingPos()
    {
        return ratingPos;
    }
    public int getRatingNeg()
    {
        return ratingNeg;
    }
    public float getRating()
    {
        return (float)ratingPos/(float)(ratingPos+ratingNeg);
    }
    public LinkedList<Achievement> getAchievements()
    {
        return unlockedAchievements;
    }
    public int getScanCount()
    {
        return scanCount;
    }
    public String getlastScanned (){return lastScanned;}

    public void incScanCount()
    {
        scanCount++;
    }
    public void setLastScanned(String newScan)
    {
        if(lastScanned.equals(newScan) && talkDone == false)
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
    public void incRatingPos()
    {
        ratingPos++;
    }
    public void incRatingNew()
    {
        ratingNeg++;
    }
    private void addPoints(int pts)
    {
        points += pts;
    }
    public void addUnlockedAchievement(Achievement achievement)
    {
        unlockedAchievements.add(achievement);
        addPoints(achievement.getPoints());
    }

}
