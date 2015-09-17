package com.soctec.soctec;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class takes care of all communication with the server. Sends and receives data.
 * @author David
 * @version Created on 2015-09-15
 */
public class NetworkHandler
{
    public static final int SCAN_MSG = 0;
    public static final int BACKUP_MSG = 1;
    public static final int DOWNLOAD_MSG = 2;

    //TODO: Should these be hard-coded?
    private static final String serverAddress = "XXX.XXX.XXX.XXX";
    private static final int portNr = -1;

    /**
     *This method is used when the user has just scanned a QR-code
     *@param scannedCode The code from the scanned device
     *@param cntxt Context of the calling class
     */
    public static void sendScanInfo(String scannedCode, Context cntxt)
    {
        //TODO: Get device's code (encrypted from phone number)
        String myCode = "XXXXXXXX";

        NetworkThread nt = new NetworkThread();
        nt.sendMessage(SCAN_MSG, scannedCode + ", " + myCode, cntxt);
    }
    
    /**
     * This method is used when data needs to be backed up.
     *@param cntxt Context of the calling class
     */
    public static void backupData(Context cntxt)
    {
        Object dataToSend = null; //TODO: Get data to send

        NetworkThread nt = new NetworkThread();
        nt.sendMessage(BACKUP_MSG, dataToSend.toString(), cntxt);
    }

    /**
     * This method is used when the profile needs to be downloaded to a new device
     * @return List of the user's profile info
     *@param cntxt Context of the calling class
     */
    public static Object downloadProfile(Context cntxt)
    {
        String myCode = null; //TODO: Get my code.
        Object profile = null;

        NetworkThread nt = new NetworkThread();
        nt.sendMessage(DOWNLOAD_MSG, myCode, cntxt);

        //TODO: Receive data to return from server.
        return profile;
    }


    /********************************************************************************************
                                    Inner class "NetworkThread"
     ********************************************************************************************/

    /**
     * A new thread instance is created every time the app needs to communicate with the sever
     */
    static class NetworkThread extends Thread
    {
        private String dataToBeSent;
        private int msgType;

        /**
         * This method is called my NetworkHandler when it needs to communicate with the server
         * @param msgType Type of message to send to the server
         * @param data The data to send to the server
         * @param myContext Context of the calling activity
         */
        public synchronized void sendMessage(int msgType, String data, Context myContext)
        {
            ConnectivityManager connMgr =
                    (ConnectivityManager)myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connMgr.getActiveNetworkInfo().getType() != ConnectivityManager.TYPE_WIFI)
            {
                Log.i("NetworkHandler", "Not connected to WIFI");
                return;
            }
            dataToBeSent = data;
            this.msgType = msgType;
            start();
        }

        /**
         * This method performs all communication with the server.
         */
        @Override
        public void run()
        {
            try
            {
                if(msgType == DOWNLOAD_MSG || msgType == SCAN_MSG)
                {
                    sendToServer();
                    receiveFromServer();
                }
                else if(msgType == BACKUP_MSG)
                {
                    sendToServer();
                }
            }
            catch(IOException e)
            {
                Log.e("NetworkHandler", e.getMessage());
                e.printStackTrace();
                //TODO: Should this class return error codes to caller?
            }
        }

        private void sendToServer() throws IOException
        {
            Socket socket = new Socket(serverAddress, portNr);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.write(msgType);
            dos.write(dataToBeSent.getBytes());
        }

        private void receiveFromServer() throws IOException
        {
            byte[] inputData = new byte[1]; //TODO: How should we set the size here?

            ServerSocket mySocket = new ServerSocket(portNr);
            Socket serversSocket = mySocket.accept();
            DataInputStream dis = new DataInputStream(serversSocket.getInputStream());
            dis.readFully(inputData);

            //TODO: Return inputData to caller somehow...
        }
    }
}