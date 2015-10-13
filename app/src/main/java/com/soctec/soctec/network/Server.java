package com.soctec.soctec.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by dddav on 2015-10-13.
 */
public class Server
{
    final int PORT_NR = 49999;
    final boolean PUSH_MSG = true;
    final boolean FETCH_MSG = false;

    public Server()
    {
        ServerThread thread = new ServerThread();
        thread.start();
    }

    public void main(String args[])
    {
        new Server();
    }

    public void incPosivitRating(String user)
    {
        //TODO database stuff
    }

    public void incNegativeRating(String user)
    {
        //TODO database stuff
    }

    public int getPosivitRating(String user)
    {
        //TODO database stuff
        return 0;
    }

    public int getNegativeRating(String user)
    {
        //TODO database stuff
        return 0;
    }

    private class ServerThread extends Thread
    {
        ServerSocket serverSocket;

        public ServerThread()
        {
            try
            {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(PORT_NR));
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {
            while(true)
            {
                try
                {
                    Socket client = serverSocket.accept();
                    new WorkerThread(client).start();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private class WorkerThread extends Thread
    {
        Socket client;

        public WorkerThread(Socket client)
        {
            this.client = client;
        }

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

                if(msgType == FETCH_MSG)
                {
                    oos.writeInt(getPosivitRating(userID));
                    oos.writeInt(getNegativeRating(userID));
                }
                else //msgType == PUSH_MSG
                {
                    boolean positiveRating = ois.readBoolean();
                    if(positiveRating)
                        incPosivitRating(userID);
                    else
                        incNegativeRating(userID);
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
}
