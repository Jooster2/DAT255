package com.soctec.soctec.achievements;

/**
 * Created by Carl-Henrik Hult on 2015-09-22.
 */
public class CounterDemand
{
    public String type;
    public int amount;

    /**
     * Constructor that sets class variables to received parameters.
     * @param type  type of demand
     * @param amount    the amount of times, for example " the number of scans before unlocked".
     */
    public CounterDemand (String type, int amount)
    {
        this.type = type;
        this.amount = amount;
    }
}
