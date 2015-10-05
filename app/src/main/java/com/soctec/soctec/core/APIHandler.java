package com.soctec.soctec.core;

import android.util.Base64;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

/**
 *  Handles all API-reading
 *  @author Joakim Schmidt
 *  @version 1.0
 */
public class APIHandler
{
    private static APIHandler ourInstance = new APIHandler();

    public static APIHandler getInstance()
    {
        return ourInstance;
    }

    private String usernamePass;
    private String encodedUsernamePass;
    private String authorization;

    /**
     * Constructs an API Handler
     */
    private APIHandler()
    {
        usernamePass = "grp24:QX4NNVfBwf";
        encodedUsernamePass = Base64.encodeToString(usernamePass.getBytes(), Base64.DEFAULT);
        authorization = "Authorization:Basic" + encodedUsernamePass;
    }

    /**
     * Reads data from parameter specified sensor
     * @param vinNumber VIN-number to read from
     * @param sensor sensor to read
     * @param readTime how long to listen
     * @return arraylist containing all data read
     */
    public ArrayList<String> readElectricity(String vinNumber, String sensor, int readTime)
    {
        ArrayList<String> responseData = new ArrayList<>();
        long t2 = System.currentTimeMillis();
        long t1 = t2-(1000*readTime);
        String url = "https://ece01.ericsson.net:4443/ecity?dgw=Ericsson$"
                + vinNumber + "&sensorSpec=&" + sensor +"t1=" + t1 + "&t2=" + t2;
        try
        {
            URL requestURL = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) requestURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", encodedUsernamePass);

            BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = input.readLine();
            while(line != null)
            {
                responseData.add(line);
                line = input.readLine();
            }
            input.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return responseData;
    }

    /**
     * Reads the Icomera API and returns a list of parsed objects
     * @param resource Icomera resource to read
     * @return list containing Icomera objects
     */
    public ArrayList<Icomera> readIcomera(String resource)
    {
        String url = "ombord.info/api/xml/" + resource;
        ArrayList<Icomera> results = new ArrayList<>();

        try
        {
            URL requestURL = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) requestURL.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = conn.getInputStream();

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            while (parser.next() != XmlPullParser.END_TAG)
            {
                if (parser.getEventType() != XmlPullParser.START_TAG)
                    continue;
                String name = parser.getName();
                switch(name)
                {
                    case "position": results.add(readPosition(parser)); break;
                    case "system": results.add(readSystem(parser)); break;
                    case "users": results.add(readUsers(parser)); break;
                    case "user": results.add(readUser(parser)); break;
                }
            }


        }
        catch(IOException | XmlPullParserException e)
        {
            e.printStackTrace();
        }
        return results;
    }

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

        public Icomera()
        {

        }
    }
}