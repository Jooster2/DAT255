package com.soctec.soctec.network;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class Server
{
    private static final int PEER_PORT_NR = 49999;

    public Server()
    {
        PassiveThread serverThread = new PassiveThread();
        serverThread.start();
    }

    private class ActiveThread extends Thread
    {
        String connectIP;
        String clientIP;
        ArrayList<ArrayList<String>> dataToSend;

        /**
         * Constructor
         * @param connectIP The ip address to connect to
         * @param dataToSend The data to transfer
         */
        public ActiveThread(String clientIP, String connectIP, ArrayList dataToSend)
        {
            this.clientIP = clientIP;
            this.connectIP = connectIP;
            this.dataToSend = dataToSend;
        }

        @Override
        public void run()
        {
            try
            {
                //Set up socket and streams
                Socket socket = new Socket(connectIP, PEER_PORT_NR);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                //Send data, then receive data
                oos.writeObject(clientIP);
                oos.writeObject(dataToSend);

                //Clean up
                oos.flush();
                oos.close();
                socket.close();

            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Waits for connections
     */
    private class PassiveThread extends Thread
    {
        ServerSocket serverSocket = null;

        @Override
        public void run()
        {
            while(true)
            {
                try
                {
                    //Set up socket
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(PEER_PORT_NR));

                    //Accept connection and set up streams
                    Socket client = serverSocket.accept();
                    String clientIP = client.getInetAddress().toString();
                    ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

                    String ipAddress = (String)ois.readObject();
                    ArrayList<ArrayList<String>> dataReceived = (ArrayList) ois.readObject();

                    ActiveThread activeThread = new ActiveThread(clientIP, ipAddress, dataReceived);
                    activeThread.start();

                    //Clean up
                    ois.close();
                    client.close();
                    serverSocket.close();

                }
                catch(IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main()
    {
        new Server();
    }

}
