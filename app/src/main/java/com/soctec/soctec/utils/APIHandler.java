package com.soctec.soctec.utils;

import android.util.JsonReader;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 *  Handles all API-reading
 *  @author Joakim Schmidt
 *  @version 1.2
 */
public class APIHandler
{
    private String key = "";
    private ArrayList<ArrayList<String>> lastReadList;

    /**
     * Magic thread-safety
     */
    private static class Loader
    {
        static APIHandler INSTANCE = new APIHandler();
    }

    /**
     * Returns the instance of APIHandler
     * @return the instance of APIHandler
     */
    public static APIHandler getInstance()
    {
        return Loader.INSTANCE;
    }

    /**
     * Constructs an API Handler
     */
    private APIHandler()
    {
    }

    /**
     * Sets the Electricity API access key
     * @param key the key, encoded in Base64, to use
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Reads the API if there is no local data present, then returns a single String according
     * to parameters
     * @param lookingFor resource wanted
     * @param vinNumber VIN-number to read from
     * @param sensor sensor to read
     * @param readTime how long to listen
     * @return null or String containing the value wanted
     */
    public String readSingle(String lookingFor, String vinNumber, String sensor, int readTime)
    {
        Log.i("APIHandler", "Entered readSingle");
        String valueFound = null;
        if(lastReadList == null || lastReadList.size() == 0)
        {
            Log.i("APIHandler", "No list found or list was empty");
            lastReadList = readElectricity(vinNumber, sensor, readTime);
            Log.i("APIHandler", "Has read new values, list is now size: " + lastReadList.size());
        }
        ArrayList<String> lastReadValue = lastReadList.get(0);
        for(String item : lastReadValue)
        {
            if(item.contains(lookingFor))
            {
                valueFound = item.substring(item.indexOf(":")+1);
                lastReadList.remove(0);
                break;
            }
        }
        return valueFound;
    }

    public void clearLastRead()
    {
        lastReadList = null;
    }

    /**
     * Reads data from parameter specified sensor
     * @param vinNumber VIN-number to read from
     * @param sensor sensor to read
     * @param readTime how long to listen
     * @return list containing either all data read, or the http response code if no data read
     */
    public ArrayList<ArrayList<String>> readElectricity(String vinNumber, String sensor, int readTime)
    {
        ArrayList<ArrayList<String>> responseData = new ArrayList<>();
        long t2 = System.currentTimeMillis();
        long t1 = t2-(1000*readTime);
        String url = "https://ece01.ericsson.net:4443/ecity?dgw=Ericsson$"
                + vinNumber + "&sensorSpec=Ericsson$" + sensor +"&t1=" + t1 + "&t2=" + t2;

        String responseCode = "";
        try
        {
            URL requestURL = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) requestURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", key);
            responseCode = String.valueOf(conn.getResponseCode());

            JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream()));


            reader.beginArray();
            while(reader.hasNext())
            {
                responseData.add(getMessages(reader));
            }
            reader.endArray();

            reader.close();
            conn.disconnect();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        if(responseData.size() == 0)
        {
            responseData.add(new ArrayList<String>());
            responseData.get(0).add(responseCode);
        }
        return responseData;
    }

    /**
     * Basic Json parsing
     * @param reader JsonReader to parse
     * @return list of string where each string is "key:value"
     * @throws IOException
     */
    private ArrayList<String> getMessages(JsonReader reader) throws IOException
    {
        ArrayList<String> message = new ArrayList<>();

        reader.beginObject();
        while(reader.hasNext())
        {
            message.add(reader.nextName() + ":" + reader.nextString());
        }
        reader.endObject();
        Log.i("APIHandler", "Message read: " + message.toString());
        return message;
    }

    //------------------------ ICOMERA STUFF BELOW ---------------------------

    /**
     * Reads the Icomera API and returns a string with the SystemID
     * @return string containing SystemID
     */
    public String readIcomera()
    {
        final StringBuilder results = new StringBuilder();
        Thread myThread = new Thread()
        {
            public void run()
            {
                Log.i("readIcomera", "Thread start");
                String url = "http://www.ombord.info/api/xml/system";

                try
                {
                    URL requestURL = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
                    Log.i("readIcomera", "Connection established");
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    Log.i("readIcomera", "ResponseCode: " + conn.getResponseCode());

                    InputStream in = conn.getInputStream();
                    Log.i("readIcomera", "InputStream connected");

                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(in);
                    doc.getDocumentElement().normalize();
                    
                    NodeList nList = doc.getElementsByTagName("system_id");
                    for (int i = 0; i < nList.getLength(); i++)
                    {
                        Node node = nList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE)
                        {
                            Element element = (Element) node;
                            results.append(element.getTextContent());
                        }
                    }

                    conn.disconnect();
                }
                catch(IOException | ParserConfigurationException | SAXException e)
                {
                    e.printStackTrace();
                    Log.i("readIcomera", e.toString());
                }


            }
        };
        myThread.start();
        try
        {
            myThread.join();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        return results.toString();
    }
}