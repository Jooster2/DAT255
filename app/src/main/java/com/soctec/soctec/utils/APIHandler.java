package com.soctec.soctec.utils;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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

/**
 *  Handles all API-reading
 *  @author Joakim Schmidt
 *  @version 1.1
 */
public class APIHandler
{
    private static APIHandler ourInstance = new APIHandler();
    public static APIHandler getInstance()
    {
        return ourInstance;
    }

    private String key;

    /**
     * Constructs an API Handler
     */
    private APIHandler()
    {
    }
    public void setKey(String key)
    {
        this.key = key;
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
                responseData.add(getMessages(reader));
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

        return message;
    }


    /**
     * Reads the Icomera API and returns a list of parsed objects
     * @param resource Icomera resource to read
     * @return list containing Icomera objects
     */
    public ArrayList<Icomera> readIcomera(final String resource)
    {
        final ArrayList<Icomera> results = new ArrayList<>();
        Thread myThread = new Thread()
        {
            public void run()
            {
                Log.i("icomera", "entered method");
                String url = "http://www.ombord.info/api/xml/" + resource;

                try
                {
                    URL requestURL = new URL(url);
                    Log.i("icomera", "url created");
                    HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
                    Log.i("icomera", "connection established");
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    Log.i("icomera", "timeout: " + conn.getConnectTimeout());

                    //Log.i("icomera", "GET set");
                    //Log.i("icomera", String.valueOf(conn.getResponseCode()));


                    try
                    {
                        Log.i("icomera", String.valueOf(conn.getResponseCode()));

                    }
                    catch(IOException e)
                    {
                        Log.i("icomera", e.toString());
                    }

                    InputStream in;
                    Log.i("icomera", "inputstream created");
                    in = conn.getInputStream();
                    Log.i("icomera", "inputstream accepted");
                    Log.i("icomera", "Initializing XML-parser...");
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(in, null);
                    parser.nextTag();

                    Log.i("icomera", "Done");
                    while (parser.next() != XmlPullParser.END_TAG)
                    {
                        if (parser.getEventType() != XmlPullParser.START_TAG)
                            continue;
                        String name = parser.getName();
                        Log.i("icomera", name);
                        switch(name)
                        {
                            case "position": results.add(readPosition(parser)); break;
                            case "system_id": results.add(readSystem(parser)); break;
                            case "users": results.add(readUsers(parser)); break;
                            case "user": results.add(readUser(parser)); break;
                        }
                    }

                    conn.disconnect();

                }
                catch(IOException | XmlPullParserException e)
                {
                    e.printStackTrace();
                }


            }
        };
        myThread.start();
        return results;
    }
    /*DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(in);
                    doc.getDocumentElement().normalize();
                    NodeList nList = doc.getElementsByTagName("system_id");

                    for(int temp = 0; temp < nList.getLength(); temp++)
                    {

                        Node nNode = nList.item(temp);

                        System.out.println("\nCurrent Element :" + nNode.getNodeName());

                        if(nNode.getNodeType() == Node.ELEMENT_NODE)
                        {

                            Element eElement = (Element) nNode;
                            Log.i("icomera", eElement.getTextContent());

                        }
                    }*/


    /**
     * Used for parsing Icomera position
     * @param parser parser to use
     * @return new Icomera object
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Icomera readPosition(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        Icomera res = new Icomera();
        parser.require(XmlPullParser.START_TAG, null, "position");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String name = parser.getName();
            switch(name)
            {
                case "time": res.pos_time = Double.parseDouble(parser.getText()); break;
                case "latitude": res.pos_lat = Double.parseDouble(parser.getText()); break;
                case "longitude":res.pos_lon = Double.parseDouble(parser.getText()); break;
                case "altitude": res.pos_alt = Double.parseDouble(parser.getText()); break;
                case "speed": res.pos_speed = Double.parseDouble(parser.getText()); break;
                case "cmg": res.pos_cmg = Double.parseDouble(parser.getText()); break;
                case "satellites": res.pos_sat = Integer.parseInt(parser.getText()); break;
            }
        }
        return res;
    }

    /**
     * Used for parsing Icomera system
     * @param parser parser to use
     * @return new Icomera object
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Icomera readSystem(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        Log.i("icomera", "entering readSystem()");
        Icomera res = new Icomera();
        parser.require(XmlPullParser.START_TAG, null, "system");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String name = parser.getName();
            if(name.equals("system_id"))
                res.system_id = Integer.parseInt(parser.getText());
        }
        return res;
    }

    /**
     * Used for parsing Icomera users
     * @param parser parser to use
     * @return new Icomera object
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Icomera readUsers(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        Icomera res = new Icomera();
        parser.require(XmlPullParser.START_TAG, null, "users");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String name = parser.getName();
            switch(name)
            {
                case "total": res.users_tot = Integer.parseInt(parser.getText()); break;
                case "online": res.users_online = Integer.parseInt(parser.getText()); break;
            }
        }
        return res;
    }

    /**
     * Used for parsing Icomera user
     * @param parser parser to use
     * @return new Icomera object
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Icomera readUser(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        Icomera res = new Icomera();
        parser.require(XmlPullParser.START_TAG, null, "user");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String name = parser.getName();
            switch(name)
            {
                case "ip": res.user_ip = parser.getText(); break;
                case "mac": res.user_mac = parser.getText(); break;
                case "online": res.user_online = Integer.parseInt(parser.getText()); break;
                case "class": res.user_class = Integer.parseInt(parser.getText()); break;
            }
        }
        return res;
    }

    /**
     * Class containing public variables for all data that can be read from Icomera
     */
    public class Icomera
    {
        public int system_id;

        public double pos_time;
        public double pos_lat;
        public double pos_lon;
        public double pos_alt;
        public double pos_speed;
        public double pos_cmg;
        public int pos_sat;

        public int users_tot;
        public int users_online;

        public String user_ip;
        public String user_mac;
        public int user_online;
        public int user_class;

        /**
         * Constructs an empty Icomera object
         */
        public Icomera()
        {

        }
    }
}