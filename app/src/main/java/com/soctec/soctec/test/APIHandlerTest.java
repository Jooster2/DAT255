package com.soctec.soctec.test;

import android.test.AndroidTestCase;

import com.soctec.soctec.utils.APIHandler;

import java.util.ArrayList;

/**
 * Basic test cases for the APIHeader class
 * @author Joakim Schmidt
 * @version 1.1
 */
public class APIHandlerTest extends AndroidTestCase
{
    APIHandler aH = APIHandler.getInstance();
    // Insert key from klotterplank here
    public static String key = "";


    /**
     * Tests that we get exactly one element in the response list
     * @throws Exception
     */
    public void testReadElecAPISimulatedBus() throws Exception
    {
        aH.setKey(key);
        ArrayList<ArrayList<String>> data = aH.readElectricity("Vin_Num_001", "Total_Vehicle_Distance", 5);
        assertEquals(1, data.size());
        assertEquals("resourceSpec:Total_Vehicle_Distance_Value", data.get(0).get(0));
        assertEquals("gatewayId:Vin_Num_001", data.get(0).get(3));
    }

    /**
     * Tests that if the request fails we get a response code in data
     * Tests that the response code is "400" (Http Bad Request)
     * @throws Exception
     */
    public void testBadElecAPI() throws Exception
    {
        aH.setKey(key);
        ArrayList<ArrayList<String>> data = aH.readElectricity("Vin_Num_001", "Tralala", 60);
        assertEquals(1, data.size());
        assertEquals(1, data.get(0).size());
        assertEquals("400", data.get(0).get(0));
    }

    /**
     * Tests that we get a response from Icomera server and that it is not null
     * @throws Exception
     */
    public void testIcomera() throws Exception
    {
        String data = aH.readIcomera();
        assertNotNull(data);
    }
}
