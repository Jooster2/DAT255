package com.soctec.soctec;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by David on 2015-09-15.
 */
public class NetworkHandler
{
    //TODO: Use enum instead?
    public static final int BACKUP_MSG = 0;
    public static final int DOWNLOAD_MSG = 1;

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
        nt.sendMessage(scannedCode + ", "  + myCode);
    }

    /**
     * This method is used when contacting the server, but NOT after a scan.
     * @param msgType Type of message to send to the server
     */
    public static void contactServer(int msgType)
    {
        NetworkThread<Integer> nt = new NetworkThread<>();
        nt.sendMessage(msgType);
    }

    /**
     *Inner class "NetworkThread"
     */
    static class NetworkThread<T> extends Thread
    {
        private T data;

        public void sendMessage(T data)
        {
            this.data = data;
            start();
        }

        @Override
        public void run()
        {
            try
            {
                Socket socket = new Socket(serverAddress, portNr);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.write(data.toString().getBytes());
            }
            catch(IOException e)
            {
                Log.e("NetworkHandler", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}