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
 *  @version 1.1
 */
public class APIHandler
{
    private String key;
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
        String valueFound = null;
        if(lastReadList.size() == 0)
        {
            lastReadList = readElectricity(vinNumber, sensor, readTime);
        }
        ArrayList<String> lastReadValue = lastReadList.get(0);
        for(String item : lastReadValue)
        {
            if(item.contains(lookingFor))
            {
                valueFound = lookingFor.substring(lookingFor.indexOf(":")+1);
                lastReadList.remove(0);
                break;
            }
        }
        return valueFound;
    }

    public int clearLastRead()
    {
        int size = lastReadList.size();
        lastReadList.clear();
        return size;
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

    //------------------------ ICOMERA STUFF BELOW ---------------------------

    /**
     * Reads the Icomera API and returns a list of parsed objects
     * @return list containing Icomera objects
     */
    //public ArrayList<Icomera> readIcomera(final String resource)
    public String readIcomera()
    {
        //final ArrayList<Icomera> results = new ArrayList<>();
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
                    /*XmlPullParser parser = Xml.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    // Possible to set encoding here, such as UTF-8, but it should not be necessary
                    parser.setInput(in, null);

                    int eventType = parser.getEventType();
                    while(eventType != XmlPullParser.END_DOCUMENT)
                    {
                        eventType = parser.next();
                        String name = parser.getName();
                        switch(name)
                        {
                            case "position":
                                results.add(readPosition(parser));
                                break;
                            case "system":
                                results.add(readSystem(parser));
                                break;
                            case "users":
                                results.add(readUsers(parser));
                                break;
                            case "user":
                                results.add(readUser(parser));
                                break;
                        }

                    }*/
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(in);
                    doc.getDocumentElement().normalize();
                    
                    NodeList nList = doc.getElementsByTagName("system_id");

                    for (int i = 0; i < nList.getLength(); i++)
                    {
                        Node nNode = nList.item(i);

                        if (nNode.getNodeType() == Node.ELEMENT_NODE)
                        {

                            Element eElement = (Element) nNode;

                            results.append(eElement.getTextContent());


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


    /**
     * Used for parsing Icomera position
     * @param parser parser to use
     * @return new Icomera object
     * @throws XmlPullParserException
     * @throws IOException XmlPullParserException
     */
    private Icomera readPosition(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        Icomera res = new Icomera();
        parser.require(XmlPullParser.START_TAG, null, "position");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_TAG)
        {
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
            eventType = parser.next();
        }
        return res;
    }

    /**
     * Used for parsing Icomera system
     * @param parser parser to use
     * @return new Icomera object
     * @throws XmlPullParserException
     * @throws IOException XmlPullParserException
     */
    private Icomera readSystem(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        Icomera res = new Icomera();
        parser.require(XmlPullParser.START_TAG, null, "system");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_TAG)
        {
            String name = parser.getName();
            if(name.equals("system"))
                res.system_id = Integer.parseInt(parser.getText());
            eventType = parser.next();
        }
        return res;
    }

    /**
     * Used for parsing Icomera users
     * @param parser parser to use
     * @return new Icomera object
     * @throws XmlPullParserException
     * @throws IOException XmlPullParserException
     */
    private Icomera readUsers(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        Icomera res = new Icomera();
        parser.require(XmlPullParser.START_TAG, null, "users");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_TAG)
        {
            String name = parser.getName();
            switch(name)
            {
                case "total": res.users_tot = Integer.parseInt(parser.getText()); break;
                case "online": res.users_online = Integer.parseInt(parser.getText()); break;
            }
            eventType = parser.next();
        }
        return res;
    }

    /**
     * Used for parsing Icomera user
     * @param parser parser to use
     * @return new Icomera object
     * @throws XmlPullParserException
     * @throws IOException XmlPullParserException
     */
    private Icomera readUser(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        Icomera res = new Icomera();
        parser.require(XmlPullParser.START_TAG, null, "user");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_TAG)
        {
            String name = parser.getName();
            switch(name)
            {
                case "ip": res.user_ip = parser.getText(); break;
                case "mac": res.user_mac = parser.getText(); break;
                case "online": res.user_online = Integer.parseInt(parser.getText()); break;
                case "class": res.user_class = Integer.parseInt(parser.getText()); break;
            }
            eventType = parser.next();
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