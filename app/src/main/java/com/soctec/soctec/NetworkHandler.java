package com.soctec.soctec;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
     */
    public static void sendScanInfo(String scannedCode)
    {
        //TODO: Get device's code (encrypted from phone number)
        String myCode = "XXXXXXXX";

        NetworkThread<String> nt = new NetworkThread<>();
        nt.sendMessage(SCAN_MSG, scannedCode + ", " + myCode);
    }
    
    /**
     * This method is used when data needs to be backed up.
     */
    public static void backupData()
    {
        Object dataToSend = null; //TODO: Get data to send

        NetworkThread<Object> nt = new NetworkThread<>();
        nt.sendMessage(BACKUP_MSG, dataToSend);
    }

    /**
     * This method is used when the profile needs to be downloaded to a new device
     * @return List of the user's profile info
     */
    public static Object downloadProfile()
    {
        String myCode = null; //TODO: Get my code.
        Object profile = null;

        NetworkThread<String> nt = new NetworkThread<>();
        nt.sendMessage(DOWNLOAD_MSG, myCode);

        //TODO: Receive data to return from server.
        return profile;
    }

    /**
     *Inner class "NetworkThread"
     */
    static class NetworkThread<T> extends Thread
    {
        //TODO: Do we need a generic type here? Or are we always sending the same type?
        private T data;
        private int msgType;

        /**
         * This method is called my NetworkHandler when it needs to communicate with the server
         * @param msgType Type of message to send to the server
         * @param data The data to send to the server
         */
        public synchronized void sendMessage(int msgType, T data)
        {
            this.data = data;
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
                Socket socket = new Socket(serverAddress, portNr);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.write(msgType);
                dos.write(data.toString().getBytes());
            }
            catch(IOException e)
            {
                Log.e("NetworkHandler", e.getMessage());
                e.printStackTrace();
                //TODO: Should this class return error codes to caller?
            }
        }
    }
}