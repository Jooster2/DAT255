package com.soctec.soctec;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
 * Created by jooster on 9/22/15.
 */
public class AchievementCreator
{
    private AchievementUnlocker unlocker;
    public AchievementCreator(AchievementUnlocker unlocker)
    {
        this.unlocker = unlocker;
        createFromFile("filename");
    }

    public CounterAchievement createCounterAchievement(String[] data)
    {
        //Arguments are (String Name, int points, String img, String id)
        CounterAchievement achievement = new CounterAchievement(data[1], Integer.parseInt(data[2]), data[3], data[4]);
        if(data[5].equals("SIN"))
            for(int i=6; i<data.length; i++)
            {
                String[] demand = data[i].split(":");
                achievement.createDemand(demand[0], Integer.parseInt(demand[1]));
            }
        else if(data[5].equals("INF"))
            return achievement;
        //TODO create demand of infinite type
        return achievement;
    }

    public CollectionAchievement createCollectionAchievement(String[] data)
    {
        CollectionAchievement achievement = new CollectionAchievement(data[1], Integer.parseInt(data[2]), data[3],
                data[4]);
        //TODO everything...
        return achievement;
    }

    private void createFromFile(String filename)
    {
        try
        {
            BufferedReader buffer = new BufferedReader(new FileReader(filename));
            String line = buffer.readLine();
            while(line != null)
            {
                String[] data = line.split(", ");
                if(data[0].equals("CNT"))
                {
                    CounterAchievement achievement = createCounterAchievement(data);
                    unlocker.insertAchievement(achievement);
                }
                else if(data[0].equals("COL"))
                {
                    CollectionAchievement achievement = createCollectionAchievement(data);
                    unlocker.insertAchievement(achievement);
                }

                line = buffer.readLine();
            }
            buffer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }
}
