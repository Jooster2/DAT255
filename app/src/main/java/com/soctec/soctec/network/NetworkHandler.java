package com.soctec.soctec.network;

import android.os.AsyncTask;
import android.util.Log;

import com.soctec.soctec.core.MainActivity;
import com.soctec.soctec.profile.Profile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
    private static final int PEER_PORT_NR = 49997;
    private static final String SERVER_ADDRESS = "XXX.XXX.XXX.XXX";

    private int msgType;
    //private String dataToSend;
    //private String dataReceived;
    private ArrayList<ArrayList<String>> dataToSend;
    private ArrayList<ArrayList<String>> dataReceived;
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
                Log.i("connectionThread", "thread started!");
                try
                {
                    //Accept connection
                    ServerSocket serverSocket = new ServerSocket(PEER_PORT_NR);
                    Socket clientSocket = serverSocket.accept();

                    Log.i("Server thread", "Connection established!");

                    //Read data
                    ObjectInputStream dis = new ObjectInputStream(
                            clientSocket.getInputStream());
                    //dataReceived = (String)dis.readObject();
                    dataReceived = (ArrayList)dis.readObject();

                    //Write data
                    ObjectOutputStream dos = new ObjectOutputStream(
                            clientSocket.getOutputStream());

                    /*
                    Retrieves the profile list, clones it and adds the
                    usercode to the end of it
                     */
                    dataToSend = (ArrayList)Profile.getProfile().clone();
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(Profile.getUserCode());
                    dataToSend.add(temp);


                    dos.writeObject(dataToSend);

                    //Send read data to MainActivity

                    //Retrieves the usercode from the end of the list and then removes that element
                    final String id = dataReceived.get(dataReceived.size()-1).get(0);
                    dataReceived.remove(dataReceived.size()-1);
                    final ArrayList<ArrayList<String>> profile = dataReceived;
                    myActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            myActivity.receiveDataFromPeer(id, profile);
                        }
                    });

                    serverSocket.close();
                }
                catch(IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                    Log.e("NetworkHandler", "ServerSocket exception", e);
                }
                instance = new NetworkHandler(myActivity);
            }
        };
        connectionListenerThread.start();
        Log.i("NetworkHandler", "Tried to start connectionListenerTread");
    }

    /**
     * Singleton pattern. Whenever you want to call a method in this class, use:
     * NetworkHandler.getInstance().aMethod();
     * First time called, connectionListenerThread will start automatically.
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
     * This method is used when data needs to be backed up to the server.
     */
    /*public void backupData()
    {
        msgType = BACKUP_MSG;
        //Put together data to send to server: ID + TYPE + DATA
        dataToSend = "<Insert my ID here>" + ":0:" + "<insert data here>";
        execute();
    }*/

    /**
     * This method is used when the profile needs to be downloaded from the server to a new device.
     */
    /*public void downloadData()
    {
        msgType = DOWNLOAD_MSG;
        //Put together data to send to server: ID + TYPE
        dataToSend = "<Insert my ID here>" + ":1";
        execute();
    }*/

    /**
     * This method is called when a scan has taken place and we need to connect to a peer.
     * This method will connect to the other device and exchange ID and profile data.
     * MainActivity is called when this process in done.
     * @param scannedAddress The peer's address, scanned from QR
     */
    public void sendScanInfoToPeer(String scannedAddress)
    {
        if (!ConnectionChecker.isConnected(myActivity.getApplicationContext()))
        {
            Log.i("NetworkHandler", "No WIFI access, aborting network task");
        }
        else
        {
            msgType = SCAN_MSG;
            /*
            Retrieves the profile list, clones it and adds the
            usercode to the end of it
             */
            dataToSend = (ArrayList)Profile.getProfile().clone();
            ArrayList<String> temp = new ArrayList<>();
            temp.add(Profile.getUserCode());
            dataToSend.add(temp);
            execute(scannedAddress);
        }
    }

    /**
     * Start the connectionListener Thread
     * This is done automatically in the constructor. Therefore, no need to call this function
     * after class in initialized (initialization = calling .getInstance() the first time).
     */
    public void startConnectionListener()
    {
        if(!connectionListenerThread.isAlive())
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
        Log.i("doInBackground", "Got this far!");
        try
        {
            Socket socket;
            if(msgType == DOWNLOAD_MSG || msgType == BACKUP_MSG)
            {
                //Send data to server
                /*socket = new Socket(SERVER_ADDRESS, SERVER_PORT_NR);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeBytes(dataToSend);

                //Receive data from server
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                byte[] tmpArray = new byte[1024];
                dis.readFully(tmpArray);
                dataReceived = new String(tmpArray);

                socket.close();*/
            }
            else if(msgType == SCAN_MSG)
            {
                Log.i("", params[0]);
                //Send data to peer
                socket = new Socket();
                socket.connect(new InetSocketAddress(params[0], PEER_PORT_NR), 10000);

                Log.i("Client socket", "Socket created!");

                ObjectOutputStream dos = new ObjectOutputStream(socket.getOutputStream());
                dos.writeObject(dataToSend);

                //Receive data from peer
                ObjectInputStream dis = new ObjectInputStream(socket.getInputStream());
                dataReceived = (ArrayList)dis.readObject();

                socket.close();
            }
        }
        catch(IOException | ClassNotFoundException e)
        {
            Log.e("NetworkHandler", e.getMessage());
            e.printStackTrace();
            dataReceived = null;
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
            if (dataReceived != null)
            {
                Log.i("PostExecute", "Received: " + dataReceived);

                // Retrieves the usercode from the end of the list and then removes that element
                final String id = dataReceived.get(dataReceived.size()-1).get(0);
                dataReceived.remove(dataReceived.size()-1);
                final ArrayList<ArrayList<String>> profile = dataReceived;
                myActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        myActivity.receiveDataFromPeer(id, profile);
                    }
                });
            }
        } else if (msgType == DOWNLOAD_MSG)
        {
            if (dataReceived != null)
            {
                myActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //myActivity.receiveDataFromServer(dataReceived);
                    }
                });
            }
        }

        instance = new NetworkHandler(myActivity);
    }
}
