package com.soctec.soctec.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.soctec.soctec.core.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * This class listens for broadcasts concerning wifi networks. Calls for generation of new QR-codes
 * when necessary. Can check if device is connected to Electricity.
 * @author David
 * @version 2015-09-29
 */
public class ConnectionChecker extends BroadcastReceiver
{
    //Set to false when testing on any network, set to true if testing on Electricity specifically
    private static final boolean TEST_FOR_ELECTRICITY = false;
    private MainActivity myActivity;

    //Got BSSIDs from https://gist.github.com/hjorthjort/fb2fcf80c773ea90c6fa
    static final String[] BSSIDs = {
            "04:f0:21:10:09:df",
            "04:f0:21:10:09:b9",
            "04:f0:21:10:09:e8",
            "04:f0:21:10:09:b7",
            "04:f0:21:10:09:53",
            "06:f0:21:10:0c:87",
            "06:f0:21:10:0c:ab",
            "06:f0:21:11:5c:3d"
    };

    public ConnectionChecker(MainActivity activity)
    {
        myActivity = activity;
    }

    /**
     * This method is called by the Android operating system whenever a change in
     * the network connection has occurred. Checks if connected to electricity wifi and updates the
     * QR image accordingly.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("Receiver", "Received broadcast: " + intent.getAction());

        final String action = intent.getAction();
        if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
        {
            boolean connectedToElectricity = isConnected(context);
            NetworkInfo ni = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            if (ni.getState().equals(NetworkInfo.State.CONNECTED) && connectedToElectricity)
            {
                myActivity.updateQR(getNewQR(context));
            }
            else
            {
                myActivity.updateQR(null);
            }
        }
    }

    /**
     * Check if device is connected to one of Electricity's routers.
     * @param context The context of the calling activity
     * @return True if device is connected, o/w false
     */
    public static boolean isConnected(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        WifiInfo wi = wm.getConnectionInfo();
        boolean connectedToElectricity = true;

        if(ni.isConnected())
        {
            Log.i("ConnectionChecker", "Connected to: " + wi.getSSID());

            //This if-statement is only used for testing.
            if(TEST_FOR_ELECTRICITY)
            {
                String myBSSID = wi.getBSSID();
                //Compare with list of accepted BSSIDs
                for (String id : BSSIDs)
                {
                    if (id.equals(myBSSID))
                    {
                        connectedToElectricity = true;
                        break;
                    }
                }
            }
            else
                connectedToElectricity = true;
        }
        Log.i("ConnectionChecker",
                (connectedToElectricity ? "C" : "Not c") + "onnected to E-city");
        return connectedToElectricity;
    }

    /**
     * Uses the QRGen class to get a new QR image based on the device's IP.
     * @param context  Context of the calling activity
     * @return The bitmap of the QR
     */
    private Bitmap getNewQR(Context context)
    {
        return (new QRGen()).getQR(getUserIP(context));
    }

    /**
     * Gets the device's IP.
     * @param context Context of the calling activity
     * @return The device's IP
     */
    private String getUserIP(Context context)
    {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ipAddress = manager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
        {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try
        {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException e)
        {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }
        return ipAddressString;
    }
}
