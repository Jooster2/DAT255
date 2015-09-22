package com.soctec.soctec;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class takes care of all communication with the server. Sends and receives data.
 *
 * Don't create instances of this class. Simply call a method (e.g. backupData) and the class
 * will take care of the rest.
 * @author David
 * @version Created on 2015-09-15
 */

public class NetworkHandler extends AsyncTask<Void, Void, Void>
{
    //static final variables
    public static final String BACKUP_MSG = "0";
    public static final String DOWNLOAD_MSG = "1";
    public static final String SCAN_MSG = "2";
    private static final String SERVER_ADDRESS = "XXX.XXX.XXX.XXX";
    private static final int SERVER_PORT_NR = 49999;
    private static final int PEER_PORT_NR = 49998;

    //Non-static variables
    private String msgType;
    //dataToSend contains user ID, message type and data, all in one string
    private String dataToSend;
    private byte[] dataReceived;
    private MainActivity myActivity;

    /**
     * Constructor
     * @param msgType Type of message (download message, backup message...)
     * @param stringToSend The data to send to server, if any.
     */
    private NetworkHandler(String msgType, String stringToSend, MainActivity activity)
    {
        this.msgType = msgType;
        this.myActivity = activity;
        if(msgType.equals(SCAN_MSG))
        {
            //This string will be sent to peer
            dataToSend = "<Insert my ID here>" + ":" + stringToSend;
        }
        else
        {
            //This string will be sent to server
            dataToSend = "<Insert my ID here>" + ":" + msgType + ":" + stringToSend;
        }
    }

    /*******************PUBLIC STATIC METHODS*******************/

    /**
     * This method is used when data needs to be backed up. Creates a new instance of
     * NetworkHandler, initializes the data to send, and contacts the server.
     * @param activity The calling activity
     */
    public static void backupData(MainActivity activity)
    {
        if(!hasNetworkAccess(activity.getApplicationContext()))
        {
            Log.i("NetworkHandler", "No WIFI access, aborting network task");
        }
        else
        {
            new NetworkHandler(BACKUP_MSG, "<Insert data to backup here>", activity)
                    .doInBackground();
        }
    }

    /**
     * This method is used when the profile needs to be downloaded to a new device.
     * Creates a new instance of NetworkHandler, initializes the data to send,
     * and contacts the server.
     * @param activity The calling activity
     */
    public static void downloadData(MainActivity activity)
    {
        if(!hasNetworkAccess(activity.getApplicationContext()))
        {
            Log.i("NetworkHandler", "No WIFI access, aborting network task");
        }
        else
        {
            new NetworkHandler(DOWNLOAD_MSG, "", activity).doInBackground();
        }
    }

    /**
     *This method is used when the user has just scanned a QR-code. Creates a new instance of
     *NetworkHandler, initializes the data to send, and contacts the server.
     *@param scannedCode The code from the scanned device
     *@param activity The calling activity
     */
    public static void sendScanInfo(String scannedCode, MainActivity activity)
    {
        if(!hasNetworkAccess(activity.getApplicationContext()))
        {
            Log.i("NetworkHandler", "No WIFI access, aborting network task");
        }
        else
        {
            new NetworkHandler(SCAN_MSG, "<Insert profile data here>", activity).doInBackground();
        }
    }

    /********************Methods inherited from AsyncTask**************************/

    /**
     * Called internally in NetworkHandler to perform network tasks on another thread.
     * @param params Nothing, just void
     * @return Nothing, just null
     */
    @Override
    protected Void doInBackground(Void... params)
    {
        try
        {
            if(msgType.equals(SCAN_MSG))
            {
                doP2pStuff();
            }
            else
            {
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT_NR);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeBytes(dataToSend);

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                dis.readFully(dataReceived);

                socket.close();
            }
        }
        catch(IOException e)
        {
            Log.e("NetworkHandler", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Called internally in NetworkHandler when server communication is done.
     * @param v Nothing, just void
     */
    @Override
    protected void onPostExecute(Void v)
    {
        if(msgType.equals(SCAN_MSG))
        {
            myActivity.receiveDataFromPeer(dataReceived);
        }
        else if(msgType.equals(DOWNLOAD_MSG))
        {
            myActivity.receiveDataFromServer(dataReceived);
        }
    }

    /*******************Helper methods*********************/

    /**
     * This method does all the p2p stuff
     * @throws IOException
     */
    private void doP2pStuff() throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(PEER_PORT_NR);
        Socket clientSocket = serverSocket.accept();
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

        dos.writeBytes(dataToSend);
        dis.readFully(dataReceived);

        serverSocket.close();
    }

    /**
     * Checks if user is connected to Electricity wifi
     * @param myContext Context of calling activity
     * @return True if connected to Electricity wifi, o/w false
     */
    public static boolean hasNetworkAccess(Context myContext)
    {
        ConnectivityManager connMgr =
                (ConnectivityManager)myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connMgr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI);
    }
}
