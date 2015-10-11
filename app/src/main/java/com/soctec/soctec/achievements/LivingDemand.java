package com.soctec.soctec.achievements;

import android.util.Pair;

import com.soctec.soctec.utils.APIHandler;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Class for Demands that require reading the API on regular basis to check for
 * specific conditions. Notifies via notifyObservers when a value has been read
 * and sends with it a Pair, containing the Demand being handled as well as the value read.
 */
public class LivingDemand extends Observable implements Runnable
{
    private Demand demand;
    private String requirement;
    private String sensor;
    private String vinNumber;
    private int time;
    private boolean isRunning = true;
    ArrayList<ArrayList<String>> fromAPI;

    public LivingDemand(Demand demand)
    {
        this.demand = demand;
        sensor = demand.requirement;
        vinNumber = demand.extras;
        time = demand.detail;
    }


    //TODO This method needs lots of work, can be made much better with some tweaks to APIHandler.
    //Tweaks include such things as returning a single String, and handle specific resources
    //not just sensors
    //TODO Must add another extrasString to Demands, requirement is NOT sensor.

    @Override
    public void run()
    {
        while(isRunning)
        {
            APIHandler aH = APIHandler.getInstance();
            fromAPI = aH.readElectricity(vinNumber, sensor, time);
            for(ArrayList<String> line : fromAPI)
            {
                for(String data : line)
                {
                    if(data.contains("resourceSpec"))
                    {
                        String value = data.substring(data.indexOf(":")+1);
                        //TODO decide if this really belongs here, or if unlocker should handle it
                        if(value.equals(requirement))
                        {
                            setChanged();
                            notifyObservers(new Pair<>(demand, value));
                            isRunning = false;
                        }
                    }
                }
            }
        }
    }
}
