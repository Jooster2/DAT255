package com.soctec.soctec.network;

import android.util.Log;

import com.soctec.soctec.core.MainActivity;
import com.soctec.soctec.profile.Profile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class NetworkHandler
{
    private static final int PORT_NR = 49999;
    private static final String SERVER_IP = "jooster.no-ip.org";
    private MainActivity myActivity;
    private PassiveThread listenerThread;
    private boolean listenForConnections = true;
    private boolean hasSent = false;
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
        String peerIp;
        String dataToSend;
        int ipSize, dataSize;

        /**
         * Constructor
         * @param ip The ip address to connect to
         * @param list The data to transfer
         */
        public ActiveThread(String ip, ArrayList list)
        {
            peerIp = ip;
            this.dataToSend = "data";
            ipSize = ip.length();
            dataSize = dataToSend.length();
        }

        @Override
        public void run()
        {
            try
            {
                //Set up socket and streams
                Socket socket = new Socket(SERVER_IP, PORT_NR);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                //Send data
                dos.writeInt(ipSize);
                dos.writeBytes(peerIp);
                dos.writeInt(1);
                dos.writeInt(dataSize);
                dos.writeBytes(dataToSend);

                hasSent = true;
                //Receive peer's ip. Useless!
               /* int ipLength = dis.readInt();
                byte[] peerIp;
                peerIp = new byte[ipLength];
                dis.readFully(peerIp, 0, ipLength);

                //Read peer's data. Not useless!
                int dataLength = dis.readInt();
                byte[] data;
                data = new byte[dataLength];
                dis.readFully(data, 0, dataLength);

                //Handle received data
                StringBuilder dataReceived = new StringBuilder();
                for(byte b : data)
                {
                    dataReceived.append(b);
                }

                Log.i("myTag", dataReceived.toString());*/
                /*
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
                */

                //Clean up
                dis.close();
                dos.flush();
                dos.close();
                socket.close();

            } catch(IOException e)
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
                    serverSocket.bind(new InetSocketAddress(PORT_NR));

                    //Accept connection and set up streams
                    Socket client = serverSocket.accept();
                    DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                    DataInputStream dis = new DataInputStream(client.getInputStream());

                    //Receive peer's ip
                    int ipLength = dis.readInt();
                    byte[] peerIp;
                    peerIp = new byte[ipLength];
                    dis.readFully(peerIp, 0, ipLength);

                    //Read peer's data
                    int dataLength = dis.readInt();
                    byte[] data;
                    data = new byte[dataLength];
                    dis.readFully(data, 0, dataLength);

                    if(!hasSent)
                    {
                        //Send data
                        dos.writeInt(ipLength);
                        dos.write(peerIp);
                        dos.writeInt(1);
                        dos.writeInt("data 2".length());
                        dos.writeBytes("data 2");

                        StringBuilder dataReceived = new StringBuilder();
                        for (byte b : data)
                        {
                            dataReceived.append(b);
                        }

                        //Send data
                        dos.writeBytes(getDataToSend().toString() + "Insert ip here");

                        Log.i("myTag", dataReceived.toString());
                    }
                    hasSent = false;
                    //Handle received data
                    /*final String userCode = dataReceived.get(dataReceived.size() - 1).get(0);
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
                    */

                    //Clean up
                    dis.close();
                    dos.flush();
                    dos.close();
                    client.close();
                    serverSocket.close();

                } catch(IOException e)
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
