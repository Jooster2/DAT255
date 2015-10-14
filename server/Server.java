package com.soctec.soctec.network;

import android.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class can receive connection from a client. The client might want to push data to the
 * server or fetch data from it.
 * @author David
 * @version 1.5
 */
public class Server
{
    final int PORT_NR = 49999;
    final boolean PUSH_MSG = true;
    final boolean FETCH_MSG = false;
    HashMap<String, Pair<Integer, Integer>> database;

    /**
     * Creates a new Server object
     * @param args Command line arguments
     */
    public static void main(String args[])
    {
        new Server();
    }

    /**
     * Sets up a ServerSocket and listens for incoming connections. For each connection,
     * a new thread is started.
     */
    public Server()
    {
        try
        {
            initDatabase();
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(PORT_NR));

            while (true)
            {
                Socket client = serverSocket.accept();
                new ServerThread(client).start();
            }
        }
        catch(IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Class used for communicating with a single client
     */
    private class ServerThread extends Thread
    {
        Socket client;

        /**
         * Constructor creates new thread to communicate with a client
         * @param client The client to communicate with
         */
        public ServerThread(Socket client)
        {
            this.client = client;
        }

        /**
         * Data is sent to / received from a client, depending on if the client wants to
         * push or fetch data.
         */
        @Override
        public void run()
        {
            try
            {
                //Set up streams
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

                //Check message type and user ID
                boolean msgType = ois.readBoolean();
                String userID = (String)ois.readObject();

                //Fetch or push data
                if(msgType == FETCH_MSG) //The client wants to fetch. Send data from server
                {
                    oos.writeInt(getPositiveRating(userID));
                    oos.writeInt(getNegativeRating(userID));
                }
                else //msgType == PUSH_MSG The client wants to push. Receive data from client
                {
                    boolean positiveRating = ois.readBoolean();
                    if(positiveRating)
                        incPositiveRating(userID);
                    else
                        incNegativeRating(userID);
                    saveDatabase();
                }

                //Clean up
                oos.flush();
                oos.close();
                ois.close();
                client.close();
            }
            catch(IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets up the database when the server starts. Reads saved database from file, or creates new.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private void initDatabase() throws IOException, ClassNotFoundException
    {
        File file = new File("database.dat");
        if(file.exists())
        {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.getPath()));
            database = (HashMap) ois.readObject();
            ois.close();
        }
        else
        {
            file.createNewFile();
            database = new HashMap<>();
        }
    }

    /**
     * Saves database to file.
     * @throws IOException
     */
    private synchronized void saveDatabase() throws IOException
    {
        File file = new File("database.dat");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(database);
        oos.flush();
        oos.close();
    }

    /**
     * Increment the positive rating of a user
     * @param user The user to increment
     */
    public synchronized void incPositiveRating(String user)
    {
        if(database.containsKey(user))
        {
            database.put(user, new Pair<>(database.get(user).first + 1,
                                          database.get(user).second));
        }
        else
        {
            database.put(user, new Pair<>(1, 0));
        }
    }

    /**
     * Increment the negative rating of a user
     * @param user The user to increment
     */
    public synchronized void incNegativeRating(String user)
    {
        if(database.containsKey(user))
        {
            database.put(user, new Pair<>(database.get(user).first,
                                          database.get(user).second + 1));
        }
        else
        {
            database.put(user, new Pair<>(0, 1));
        }
    }

    /**
     * Returns the positive rating of a user
     * @param user The user whose rating is returned
     * @return The positive rating
     */
    public synchronized int getPositiveRating(String user)
    {
        if(database.containsKey(user))
        {
            return database.get(user).first;
        }
        else
            return 0;
    }

    /**
     * Returns the negative rating of a user
     * @param user The user whose rating is returned
     * @return The negative rating
     */
    public synchronized int getNegativeRating(String user)
    {
        if(database.containsKey(user))
        {
            return database.get(user).second;
        }
        else
            return 0;
    }
}
