package com.soctec.soctec.achievements;

import android.util.Pair;

import com.soctec.soctec.utils.APIHandler;
import com.soctec.soctec.utils.FileHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Observable;

/**
 * Demand used by Achievements to determine if it is earned or not
 * @author Carl-Henrik Hult, Joakim Schmidt
 * @version 2.0
 */
public class Demand extends Observable implements Runnable, Serializable
{
    public static final int PERSON_SCAN = 1;
    public static final int BUS_RIDE = 2;
    public static final int API = 3;

    private static final long serialVersionUID = 2L;
    public volatile boolean running;
    //Type of requirement
    public int type;
    //The actual requirement
    public String requirement;
    //Some extra data for doing different things
    public String extraPrimary = null;
    //Some more extra data, mostly used for API type Demands
    public String extraSecondary = null;
    //Numerical extra, for equations and API type Demands
    public int detail;

    /**
     * Constructor that sets class variables to received parameters.
     * @param type type of demand
     * @param requirement requirement for unlocking
     * @param extraPrimary extra data for constructing demand
     * @param detail comes from Achievement-ID, how many times it has been created (inclusive)
     */
    public Demand(int type, String requirement,
                  String extraPrimary, String extraSecondary, int detail)
    {
        running = false;
        this.type = type;
        if(type == PERSON_SCAN && extraPrimary != null)
            this.requirement = calculateRequirement(requirement, extraPrimary, detail);
        else
            this.requirement = requirement;
        this.extraPrimary = extraPrimary;
        this.extraSecondary = extraSecondary;
        this.detail = detail;
    }

    /**
     * An extremely simple parser for calculating equations. Supports ^,*,/,+,- but not parenthesis
     * @param requirement the base of the equation
     * @param equation the equation to be applied
     * @return calculated requirement
     */
    private String calculateRequirement(String requirement, String equation, int detail)
    {
        //TODO might want to migrate eq to LinkedList<Character> for optimization, but maybe not worth it
        LinkedList<String> eq = new LinkedList<>(Arrays.asList(equation.split("")));
        // In Java 7, the above code will result in an empty element at first place, which we remove
        if(eq.get(0).equals(""))
            eq.remove(0);
        // Replace all instances of 'a' with amount
        while(eq.contains("a"))
            eq.set(eq.indexOf("a"), requirement);
        // Replace all instances of 'c' with detail
        while(eq.contains("c"))
            eq.set(eq.indexOf("c"), String.valueOf(detail));
        while(eq.size() > 1)
        {

            if(eq.contains("^"))
                calc(eq, "^");
            else if(eq.contains("*"))
                calc(eq, "*");
            else if(eq.contains("/"))
                calc(eq, "/");
            else if(eq.contains("+"))
                calc(eq, "+");
            else if(eq.contains("-"))
                calc(eq, "-");
        }
        return eq.getFirst();
    }

    /**
     * Performs calculations and equation cleanup for calculateRequirement()
     * @param eq equation to be calculated
     * @param op operation to perform
     */
    private void calc(LinkedList<String> eq, String op)
    {
        int i = eq.indexOf(op);
        double x = Double.parseDouble(eq.get(i-1));
        double y = Double.parseDouble(eq.get(i+1));
        switch(op)
        {
            case "^": eq.set(i, String.valueOf((int)Math.pow(x, y))); break;
            case "*": eq.set(i, String.valueOf((int)(x*y))); break;
            case "/": eq.set(i, String.valueOf((int)(x/y))); break;
            case "+": eq.set(i, String.valueOf((int)(x+y))); break;
            case "-": eq.set(i, String.valueOf((int)(x-y))); break;
        }
        eq.remove(i+1);
        eq.remove(i-1);
    }

    public void shutdown()
    {
        running = false;
    }

    public void start()
    {
        running = true;
    }

    public boolean isRunning()
    {
        return running;
    }


    @Override
    public void run()
    {
        running = true;
        while(running)
        {
            APIHandler aH = APIHandler.getInstance();
            FileHandler fH = FileHandler.getInstance();
            String vinNumber = extraPrimary;
            if(extraPrimary.equals("CURRENT_BUS"))
            {
                /*
                Retreive the Icomera bus-id from APIHandler, then retreive the
                resource-id from FileHandler for that particular string, then read the string,
                which is the vinNumber that is inserted into APIHandler.readSingle.
                 */
                String icomeraID = aH.readIcomera();
                int resourceID = fH.getResourceID("SID" + icomeraID, "string");
                vinNumber = fH.readString(resourceID);
            }

            String fromAPI = aH.readSingle("resource", vinNumber, extraSecondary, detail);
           //TODO decide if this really belongs here, or if unlocker should handle it
            if(fromAPI != null)
            {
                setChanged();
                notifyObservers(new Pair<>(this, fromAPI));
            }

            try
            {
                Thread.sleep(10000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
