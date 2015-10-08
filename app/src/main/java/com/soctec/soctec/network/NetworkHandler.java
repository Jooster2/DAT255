package com.soctec.soctec.network;

import com.soctec.soctec.core.MainActivity;
import com.soctec.soctec.profile.Profile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class NetworkHandler
{
    private static final int PEER_PORT_NR = 49998;
    private MainActivity myActivity;
    private PassiveThread listenerThread;
    private boolean listenForConnections = true;
    private static NetworkHandler instance = null;

    /**
     * Constructor
     * @param act MainActivity
     */
    private NetworkHandler(MainActivity act)
    {
        myActivity = act;
    }

    /**
     * Returns the only instance
     * @param activity MainActivity
     * @return the instance
     */
    public static NetworkHandler getInstance(MainActivity activity)
    {
        if(instance == null)
            return instance = new NetworkHandler(activity);
        else
            return instance;
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
            ActiveThread thread = new ActiveThread(scannedAddress, getDataToSend());
            thread.start();
        }
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
        String ip;
        ArrayList<ArrayList<String>> dataToSend;

        /**
         * Constructor
         * @param ip The ip address to connect to
         * @param dataToSend The data to transfer
         */
        public ActiveThread(String ip, ArrayList dataToSend)
        {
            this.ip = ip;
            this.dataToSend = dataToSend;
        }

        @Override
        public void run()
        {
            try
            {
                //Set up socket and streams
                Socket socket = new Socket(ip, PEER_PORT_NR);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                //Send data, then receive data
                oos.writeObject(dataToSend);
                ArrayList<ArrayList<String>> dataReceived = (ArrayList)ois.readObject();

                //Handle received data
                final String userCode = dataReceived.get(dataReceived.size()-1).get(0);
                dataReceived.remove(dataReceived.size() - 1);
                final ArrayList<ArrayList<String>> userProfile = dataReceived;

                myActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        myActivity.receiveDataFromPeer(userCode, userProfile);
                    }
                });

                //Clean up
                ois.close();
                oos.flush();
                oos.close();
                socket.close();

            } catch(IOException | ClassNotFoundException e)
            {
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
                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

                    //Receive data, then send data
                    ArrayList<ArrayList<String>> dataReceived = (ArrayList) ois.readObject();
                    oos.writeObject(getDataToSend());

                    //Handle received data
                    final String userCode = dataReceived.get(dataReceived.size() - 1).get(0);
                    dataReceived.remove(dataReceived.size() - 1);
                    final ArrayList<ArrayList<String>> userProfile = dataReceived;

                    myActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            myActivity.receiveDataFromPeer(userCode, userProfile);
                        }
                    });

                    //Clean up
                    ois.close();
                    oos.flush();
                    oos.close();
                    client.close();
                    serverSocket.close();

                } catch(IOException | ClassNotFoundException e)
                {
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
}
