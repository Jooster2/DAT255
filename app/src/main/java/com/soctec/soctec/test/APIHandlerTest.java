package com.soctec.soctec.test;

import android.test.AndroidTestCase;

import com.soctec.soctec.core.APIHandler;

import java.util.ArrayList;

/**
 * Basic test cases for the APIHeader class
 * @author Joakim Schmidt
 * @version 1.0
 */
public class APIHandlerTest extends AndroidTestCase
{
    APIHandler aH = APIHandler.getInstance();

    /**
     * Tests that we get exactly one element in the response list
     * @throws Exception
     */
    public void testReadElecAPI() throws Exception
    {
        ArrayList<String> data = aH.readElectricity("Vin_Num_001", "Total_Vehicle_Distance", 60);
        assertEquals(1, data.size());
    }

    /**
     * Tests that if the request fails we get a response code in data
     * Tests that the response code is "400" (Http Bad Request)
     * @throws Exception
     */
    public void testBadElecAPI() throws Exception
    {
        ArrayList<String> data = aH.readElectricity("Vin_Num_001", "Tralala", 60);
        assertEquals(1, data.size());
        assertEquals("400", data.get(0));
    }
}
