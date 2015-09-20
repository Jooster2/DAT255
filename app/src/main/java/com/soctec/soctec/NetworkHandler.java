package com.soctec.soctec;

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
    private static final String SERVER_ADDRESS = "XXX.XXX.XXX.XXX";
    private static final int PORT_NR = -1;

    //Non-static variables
    private String msgType;
    //dataToSend contains user ID, message type and data, all in one string
    private String dataToSend;
    private byte[] dataFromServer;

    /**
     * Constructor
     * @param msgType Type of message (download message, backup message...)
     * @param stringToSend The data to send to server, if any.
     */
    private NetworkHandler(String msgType, String stringToSend)
    {
        this.msgType = msgType;
        dataToSend = "<Insert my ID here>" + ":" + msgType + ":" + stringToSend;
    }

    /*******************PUBLIC STATIC METHODS*******************/

    /**
     * This method is used when data needs to be backed up. Creates a new instance of
     * NetworkHandler, initializes the data to send, and contacts the server.
     * @param context Context of the calling activity
     */
    public static void backupData(Context context)
    {
        if(!hasNetworkAccess(context))
        {
            Log.i("NetworkHandler", "No WIFI access, aborting network task");
        }
        else
        {
            new NetworkHandler(BACKUP_MSG, "<Insert data to backup here>").doInBackground();
        }
    }

    /**
     * This method is used when the profile needs to be downloaded to a new device.
     * Creates a new instance of NetworkHandler, initializes the data to send,
     * and contacts the server.
     * @param context Context of the calling activity
     */
    public static void downloadData(Context context)
    {
        if(!hasNetworkAccess(context))
        {
            Log.i("NetworkHandler", "No WIFI access, aborting network task");
        }
        else
        {
            new NetworkHandler(DOWNLOAD_MSG, "").doInBackground();
        }
    }

    /**
     *This method is used when the user has just scanned a QR-code. Creates a new instance of
     *NetworkHandler, initializes the data to send, and contacts the server.
     *@param scannedCode The code from the scanned device
     *@param context Context of the calling activity
     */
    public static void sendScanInfo(String scannedCode, Context context)
    {
        if(!hasNetworkAccess(context))
        {
            Log.i("NetworkHandler", "No WIFI access, aborting network task");
        }

        //TODO: Do some magic WiFi P2P stuff here...
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
            sendToServer();
            if(msgType.equals(DOWNLOAD_MSG))
            {
                receiveFromServer();
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
        //Only perform this is data was received from server
        if(msgType.equals(DOWNLOAD_MSG))
        {
            //TODO: Send dataFromServer somewhere... (To an activity? To a file?)
        }
    }

    /*******************Helper methods*********************/

    /**
     * Sends data to server
     * @throws IOException
     */
    private void sendToServer() throws IOException
    {
        Socket socket = new Socket(SERVER_ADDRESS, PORT_NR);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.write(dataToSend.getBytes());
    }

    /**
     * Receives data from server and stores it in dataFromServer
     * @throws IOException
     */
    private void receiveFromServer() throws IOException
    {
        ServerSocket mySocket = new ServerSocket(PORT_NR);
        Socket serversSocket = mySocket.accept();
        DataInputStream dis = new DataInputStream(serversSocket.getInputStream());
        dis.readFully(dataFromServer);
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