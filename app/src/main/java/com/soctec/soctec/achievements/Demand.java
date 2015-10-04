package com.soctec.soctec.achievements;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Demand for Achievements, of the type "requires x of y to complete"
 * @author Carl-Henrik Hult, Joakim Schmidt
 * @version 1.2
 */
public class Demand
{
    public String type;
    public int amount;
    public String equation;
    public String requirement;

    /**
     * Constructor that sets class variables to received parameters.
     * @param type type of demand
     * @param amount the amount of times, for example "the number of scans before unlocked".
     */
    public Demand(String type, int amount)
    {
        this.type = type;
        this.amount = amount;
    }

    /**
     * Constructor that sets class variables to received parameters.
     * @param type type of demand
     * @param amount amount of type-events needed to unlock
     * @param equation equation for infinite demands
     * @param cycle comes from Achievement-ID, how many times it has been created (inclusive)
     */
    public Demand(String type, int amount, String equation, int cycle)
    {
        this.type = type;
        this.amount = calculateDemand(amount, equation, cycle);
        this.equation = equation;
    }

    /**
     * Constructor that sets class variables to received parameters.
     * @param requirement Requirement for completion
     */
    public Demand(String type, String requirement)
    {
        //TODO determine if type is really necessary for this type of demand
        /* It will help by allowing more complex achievements to be constructed, with
        different types of demands. On the other hand, do we even want to construct such complex
        achievements? On the third hand, it's already in the code, does anyone even care?
         */
        this.type = type;
        this.requirement = requirement;
    }

    /**
     * An extremely simple parser for calculating equations. Supports ^,*,/,+,- but not parenthesis
     * @param amount the base of the equation
     * @param equation the equation to be applied
     * @return calculated amount
     */
    private int calculateDemand(int amount, String equation, int cycle)
    {
        LinkedList<String> eq = new LinkedList<>(Arrays.asList(equation.split("")));
        // In Java 7, the above code will result in an empty element at first place, which we remove
        if(eq.get(0).equals(""))
            eq.remove(0);
        eq.addFirst(String.valueOf(amount));
        eq.set(eq.indexOf("c"), String.valueOf(cycle));
        while(eq.size() > 1)
        {

            if(eq.contains("^"))
            {
                int i = eq.indexOf("^");
                double x = Double.parseDouble(eq.get(i-1));
                double y = Double.parseDouble(eq.get(i+1));
                eq.set(i, String.valueOf((int) Math.pow(x, y)));
                eq.remove(i+1);
                eq.remove(i-1);

            }
            else if(eq.contains("*"))
            {
                int i = eq.indexOf("*");
                int x = Integer.parseInt(eq.get(i-1));
                int y = Integer.parseInt(eq.get(i+1));
                eq.set(i, String.valueOf(x*y));
                eq.remove(i+1);
                eq.remove(i-1);
            }
            else if(eq.contains("/"))
            {
                int i = eq.indexOf("/");
                double x = Double.parseDouble(eq.get(i-1));
                double y = Double.parseDouble(eq.get(i+1));
                eq.set(i, String.valueOf((int)(x/y)));
                eq.remove(i+1);
                eq.remove(i-1);
            }
            else if(eq.contains("+"))
            {
                int i = eq.indexOf("+");
                int x = Integer.parseInt(eq.get(i-1));
                int y = Integer.parseInt(eq.get(i+1));
                eq.set(i, String.valueOf(x+y));
                eq.remove(i+1);
                eq.remove(i-1);
            }
            else if(eq.contains("-"))
            {
                int i = eq.indexOf("+");
                int x = Integer.parseInt(eq.get(i-1));
                int y = Integer.parseInt(eq.get(i+1));
                eq.set(i, String.valueOf(x+y));
                eq.remove(i+1);
                eq.remove(i-1);
            }
        }
        return Integer.parseInt(eq.getFirst());
    }
}
