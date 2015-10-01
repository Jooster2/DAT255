package com.soctec.soctec.achievements;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Demand for Achievements, of the type "requires x of y to complete"
 * @author Carl-Henrik Hult
 * @version 1.1
 */
public class CounterDemand
{
    public String type;
    public int amount;
    public String equation;

    /**
     * Constructor that sets class variables to received parameters.
     * @param type type of demand
     * @param amount the amount of times, for example " the number of scans before unlocked".
     */
    public CounterDemand (String type, int amount)
    {
        this.type = type;
        this.amount = amount;
    }

    /**
     * Constructor that sets class variables to received parameters.
     * @param type type of demand
     * @param amount amount of type-events needed to unlock
     * @param equation equation for infinite demands
     */
    public CounterDemand (String type, int amount, String equation)
    {
        this.type = type;
        this.amount = calculateDemand(amount, equation);
        this.equation = equation;
    }

    /**
     * An extremely simple parser for calculating equations. Supports ^,*,/,+,-
     * @param amount the base of the equation
     * @param equation the equation to be applied
     * @return calculated amount
     */
    private int calculateDemand(int amount, String equation)
    {
        //TODO "c" is not amount, c is a counter that must be created (number of achievemnts before current one)
        LinkedList<String> eq = (LinkedList)Arrays.asList(equation.split(""));
        if(eq.get(0).equals(""))
            eq.remove(0);
        eq.set(eq.indexOf("c"), String.valueOf(amount));
        while(eq.size() > 1)
        {

            if(eq.contains("e"))
            {
                int i = eq.indexOf("e");
                double x = Double.parseDouble(eq.get(i-1));
                double y = Double.parseDouble(eq.get(i+1));
                eq.set(i, String.valueOf((int)Math.pow(x,y)));
                eq.remove(i-1);
                eq.remove(i+1);
            }
            else if(eq.contains("*"))
            {
                int i = eq.indexOf("*");
                int x = Integer.parseInt(eq.get(i-1));
                int y = Integer.parseInt(eq.get(i+1));
                eq.set(i, String.valueOf(x*y));
                eq.remove(i-1);
                eq.remove(i+1);
            }
            if(eq.contains("/"))
            {
                int i = eq.indexOf("/");
                double x = Double.parseDouble(eq.get(i-1));
                double y = Double.parseDouble(eq.get(i+1));
                eq.set(i, String.valueOf((int)(x/y)));
                eq.remove(i-1);
                eq.remove(i+1);
            }
            else if(eq.contains("+"))
            {
                int i = eq.indexOf("+");
                int x = Integer.parseInt(eq.get(i-1));
                int y = Integer.parseInt(eq.get(i+1));
                eq.set(i, String.valueOf(x+y));
                eq.remove(i-1);
                eq.remove(i+1);
            }
            else if(eq.contains("-"))
            {
                int i = eq.indexOf("+");
                int x = Integer.parseInt(eq.get(i-1));
                int y = Integer.parseInt(eq.get(i+1));
                eq.set(i, String.valueOf(x+y));
                eq.remove(i-1);
                eq.remove(i+1);
            }
        }
        return Integer.parseInt(eq.getFirst());
    }
}
