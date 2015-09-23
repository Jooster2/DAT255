package com.soctec.soctec;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class takes care of all communication with the server and peers. Sends and receives data.
 * @author David
 * @version Created on 2015-09-15
 */

public class NetworkHandler extends AsyncTask<String, Void, Void>
{
    private static final int BACKUP_MSG = 0;
    private static final int DOWNLOAD_MSG = 1;
    private static final int SCAN_MSG = 2;
    private static final int SERVER_PORT_NR = 49999;
    private static final int PEER_PORT_NR = 49998;
    private static final String SERVER_ADDRESS = "XXX.XXX.XXX.XXX";

    private int msgType;
    private String dataToSend;
    private String dataReceived;
    private MainActivity myActivity;

    private static NetworkHandler instance = null;
    private Thread connectionListenerThread;

    /**
     * Constructor. Initializes connection listener thread
     * @param activity Main activity
     */
    private NetworkHandler(MainActivity activity)
    {
        myActivity = activity;
        connectionListenerThread =  new Thread()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        //Accept connection
                        ServerSocket serverSocket = new ServerSocket(PEER_PORT_NR);
                        Socket clientSocket = serverSocket.accept();

                        //Read data
                        ObjectInputStream dis = new ObjectInputStream(
                                clientSocket.getInputStream());
                        dataReceived = (String)dis.readObject();

                        //Write data
                        ObjectOutputStream dos = new ObjectOutputStream(
                                clientSocket.getOutputStream());
                        dataToSend = "<Insert my ID here>" + "<Insert my profile here>";
                        dos.writeObject(dataToSend);

                        //Send read data to MainActivity
                        String id = dataReceived.split(",")[0];
                        String profile = dataReceived.split(",")[1];
                        myActivity.receiveDataFromPeer(id, profile); //TODO: User observer here?
                    }
                    catch(IOException | ClassNotFoundException e)
                    {
                        e.printStackTrace();
                        Log.e("NetworkHandler", "ServerSocket exception", e);
                    }
                }
            }
        };
        connectionListenerThread.start();
    }

    /**
     * Singleton pattern
     * @param activity The main activity.
     * @return The one instance of NetworkHandler.
     */
    public static NetworkHandler getInstance(MainActivity activity)
    {
        if(instance == null)
            return instance = new NetworkHandler(activity);
        else
            return instance;
    }

    /**
     * This method is used when data needs to be backed up.
     */
    public void backupData()
    {
        if(!hasNetworkAccess(myActivity.getApplicationContext()))
        {
            Log.i("NetworkHandler", "No WIFI access, aborting network task");
        }
        else
        {
            msgType = BACKUP_MSG;
            //Put together data to send to server: ID + TYPE + DATA
            dataToSend = "<Insert my ID here>" + ":0:" + "<insert data here>";
            doInBackground();
        }
    }

    /**
     * This method is used when the profile needs to be downloaded to a new device.
     */
    public void downloadData()
    {
        if(!hasNetworkAccess(myActivity.getApplicationContext()))
        {
            Log.i("NetworkHandler", "No WIFI access, aborting network task");
        }
        else
        {
            msgType = DOWNLOAD_MSG;
            //Put together data to send to server: ID + TYPE
            dataToSend = "<Insert my ID here>" + ":1";
            doInBackground();
        }
    }

    /**
     * This method is called when a scan has taken place and we need to connect to a peer
     * @param scannedAddress The peer's address, scanned from QR
     */
    public void sendScanInfo(String scannedAddress)
    {
        if (!hasNetworkAccess(myActivity.getApplicationContext()))
        {
            Log.i("NetworkHandler", "No WIFI access, aborting network task");
        }
        else
        {
            msgType = SCAN_MSG;
            //Put together data to send to server: ID + Profile data
            dataToSend = "<Insert my ID here>" + "," + "<Insert profile data here>";
            doInBackground(scannedAddress);
        }
    }

    /**
     * Start the connectionListener Thread
     */
    public void startConnectionListener()
    {
        if(connectionListenerThread.isInterrupted())
            connectionListenerThread.start();
    }

    /**
     * Interrupt the connectionListener Thread
     */
    public void stopConnectionListener()
    {
        if(connectionListenerThread.isAlive())
            connectionListenerThread.interrupt();
    }

    /**
     * Called internally in NetworkHandler to perform network tasks on another thread.
     * @param params Nothing, just void
     * @return Nothing, just null
     */
    @Override
    protected Void doInBackground(String... params)
    {
        try
        {
            Socket socket;
            if(msgType == DOWNLOAD_MSG || msgType == BACKUP_MSG)
            {
                //Send data to server
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT_NR);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeBytes(dataToSend);

                //Receive data from server
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                byte[] tmpArray = new byte[1024];
                dis.readFully(tmpArray);
                dataReceived = new String(tmpArray);

                socket.close();
            }
            else if(msgType == SCAN_MSG)
            {
                //Send data to peer
                socket = new Socket(params[0], PEER_PORT_NR);
                ObjectOutputStream dos = new ObjectOutputStream(socket.getOutputStream());
                dos.writeObject(dataToSend);

                //Receive data from peer
                ObjectInputStream dis = new ObjectInputStream(socket.getInputStream());
                dataReceived = (String)dis.readObject();
            }
        }
        catch(IOException | ClassNotFoundException e)
        {
            Log.e("NetworkHandler", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Called internally in NetworkHandler when server or peer communication is done.
     * @param v Nothing, just void
     */
    @Override
    protected void onPostExecute(Void v)
    {
        if (msgType == SCAN_MSG)
        {
            String id = dataReceived.split(",")[0];
            String profile = dataReceived.split(",")[1];
            myActivity.receiveDataFromPeer(id, profile);  //TODO: User observer here?
        }
        else if(msgType == DOWNLOAD_MSG)
        {
            myActivity.receiveDataFromServer(dataReceived);  //TODO: User observer here?
        }
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
