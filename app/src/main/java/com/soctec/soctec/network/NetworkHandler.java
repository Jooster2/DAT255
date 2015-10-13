package com.soctec.soctec.network;

import android.util.Log;

import com.soctec.soctec.core.MainActivity;
import com.soctec.soctec.profile.Profile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author David
 * @version 5.2
 */
public class NetworkHandler
{
    private static final int PEER_PORT_NR = 49998;
    private static final int SERVER_PORT_NR = 49999;
    private static final String SERVER_IP = "jooster.no-ip.org";
    private MainActivity myActivity;
    private PassiveThread listenerThread;
    private boolean listenForConnections = true;
    private static NetworkHandler instance = null;

    /**
     * Returns the only instance
     * @return the instance
     */
    public static NetworkHandler getInstance()
    {
        if(instance == null)
            return instance = new NetworkHandler();
        else
            return instance;
    }

    public void setMyActivity(MainActivity activity)
    {
        myActivity = activity;
    }

    /**
     * Starts thread that connects to peer and transfers data
     * @param scannedAddress The address to connect to
     */
    public void sendScanInfoToPeer(String scannedAddress)
    {
        if(ConnectionChecker.isConnected(myActivity.getApplicationContext()))
        {
            //Start networking thread
            ActiveThread thread = new ActiveThread(scannedAddress);
            thread.start();
        }
    }

    /**
     * Opens a connection to the server and pushes the latest rating
     * @param userID The ID of the user that was just given a new rating
     * @param positiveRating true, if the user was given a posivite rating, o/w false
     */
    public void pushRatingToServer(String userID, boolean positiveRating)
    {
        ServerThread serverThread = new ServerThread(userID, positiveRating);
        serverThread.start();
    }

    /**
     * Opens a connection to the server and fetches the latest rating
     */
    public void fetchRatingFromServer()
    {
        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }

    /**
     * Starts the thread that listens for incoming connections from peer
     */
    public void startThread()
    {
        if(listenerThread == null || !listenerThread.isAlive())
        {
            listenForConnections = true;
            listenerThread = new PassiveThread();
            listenerThread.start();
        }
    }

    /**
     * Stops the thread that listens for incoming connections from peer
     */
    public void stopThread()
    {
        listenForConnections = false;
        listenerThread.stopThread();
    }

    /**
     * This method returns the users profile & ID
     * @return The user's profile & ID
     */
    @SuppressWarnings("unchecked")
    private ArrayList<ArrayList<String>> getDataToSend()
    {
        ArrayList<ArrayList<String>> listToSend = (ArrayList)Profile.getProfile().clone();
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add(Profile.getUserCode());
        listToSend.add(tmp);
        return listToSend;
    }

    /**
     * This thread can connect to a peer, transfers data, and sends it to MainActivity
     */
    private class ActiveThread extends Thread
    {
        String peerIp;

        /**
         * Constructor
         * @param ip The ip address to connect to
         */
        public ActiveThread(String ip)
        {
            peerIp = ip;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run()
        {
            try
            {
                //Set up socket and streams
                Socket socket = new Socket(peerIp, PEER_PORT_NR);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                //Send, and then receive data
                Log.i("myTag", "Transmitting and receiving...");
                oos.writeObject(getDataToSend());
                ArrayList<ArrayList<String>> receivedData = (ArrayList)ois.readObject();

                //Clean up
                oos.flush();
                oos.close();
                ois.close();
                socket.close();

                //Handle received data
                final String userCode = receivedData.get(receivedData.size() - 1).get(0);
                receivedData.remove(receivedData.size() - 1);
                final ArrayList<ArrayList<String>> userProfile = receivedData;

                myActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        myActivity.receiveDataFromPeer(userCode, userProfile);
                    }
                });

            } catch(IOException | ClassNotFoundException e)
            {
                Log.i("myTag", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * This thread listens for a connection, transfers data, and sends it to MainActivity
     */
    private class PassiveThread extends Thread
    {
        ServerSocket serverSocket = null;

        @Override
        @SuppressWarnings("unchecked")
        public void run()
        {
            while(listenForConnections)
            {
                try
                {
                    //Set up socket
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(PEER_PORT_NR));

                    //Accept connection and set up streams
                    Socket client = serverSocket.accept();
                    ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());

                    //Receive, and then send data
                    Log.i("myTag", "Receiving and sending...");
                    ArrayList<ArrayList<String>> receivedData = (ArrayList)ois.readObject();
                    oos.writeObject(getDataToSend());

                    //Clean up
                    ois.close();
                    oos.flush();
                    oos.close();
                    client.close();
                    serverSocket.close();

                    //Handle received data
                    final String userCode = receivedData.get(receivedData.size() - 1).get(0);
                    receivedData.remove(receivedData.size() - 1);
                    final ArrayList<ArrayList<String>> userProfile = receivedData;

                    myActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            myActivity.receiveDataFromPeer(userCode, userProfile);
                        }
                    });
                }
                catch(IOException | ClassNotFoundException e)
                {
                    Log.i("myTag", e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        /**
         * Stops the thread from listening for connections
         */
        public void stopThread()
        {
            try
            {
                if (serverSocket != null)
                {
                    serverSocket.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                interrupt();
            }
        }
    }

    private class ServerThread extends Thread
    {
        private final boolean PUSH_TYPE = true;
        private final boolean FETCH_TYPE = false;
        private boolean msgType;
        private String userID;
        private boolean rating;

        /**
         * Thread that pushes rating to server
         * @param ID The ID of the user that was just given a new rating
         * @param positiveRating true, if the user was given a posivite rating, o/w false
         */
        public ServerThread(String ID, boolean positiveRating)
        {
            msgType = PUSH_TYPE;
            userID = ID;
            rating = positiveRating;
        }

        /**
         * Thread that fetches rating from server
         */
        public ServerThread()
        {
            msgType = FETCH_TYPE;
        }

        @Override
        public void run()
        {
            try
            {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT_NR);

                if(msgType == FETCH_TYPE)
                {
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    int ratingPos = ois.readInt();
                    int ratingNeg = ois.readInt();
                    myActivity.getStats().setRatingPos(ratingPos);
                    myActivity.getStats().setRatingNeg(ratingNeg);
                    ois.close();
                }
                else //msg == PUSH_TYPE
                {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(userID);
                    oos.writeBoolean(rating);
                    oos.close();
                }
                socket.close();
            }
            catch(IOException e)
            {
                Log.i("myTag", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
