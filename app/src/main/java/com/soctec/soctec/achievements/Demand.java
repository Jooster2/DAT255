package com.soctec.soctec.achievements;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Demand for Achievements, of the type "requires x of y to complete"
 * @author Carl-Henrik Hult, Joakim Schmidt
 * @version 1.2
 */
public class Demand implements Serializable
{
    private static final long serialVersionUID = 2L;
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
        //TODO might want to migrate eq to LinkedList<Character> for optimization, but maybe not worth it
        LinkedList<String> eq = new LinkedList<>(Arrays.asList(equation.split("")));
        // In Java 7, the above code will result in an empty element at first place, which we remove
        if(eq.get(0).equals(""))
            eq.remove(0);
        // Replace all instances of 'a' with amount
        while(eq.contains("a"))
            eq.set(eq.indexOf("a"), String.valueOf(amount));
        // Replace all instances of 'c' with cycle
        while(eq.contains("c"))
            eq.set(eq.indexOf("c"), String.valueOf(cycle));
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
        return Integer.parseInt(eq.getFirst());
    }

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
}
