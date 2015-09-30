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
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * @author David
 * @version 2015-09-29
 */
public class ConnectionReceiver extends BroadcastReceiver
{
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
    String prevBSSID = "";

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
        if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction()))
        {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wi = wm.getConnectionInfo();

            boolean connectedToElectricity = isConnected(context);
            if (!connectedToElectricity)
            {
                //TODO: Tell MainFragment to NOT show QR-code
                prevBSSID = "";
            }
            else if (!prevBSSID.equals(wi.getBSSID())) //Check if connected to new wifi or same one
            {
                showNewQR(context);
                //TODO: Tell MainFragment to show QR-code.
                prevBSSID = wi.getBSSID();
            }
        }
    }

    /**
     * Check if device is connected to one of Electricity's routers.
     * @param context The context of the calling activity
     * @return False if not connected to wifi or if connected to any wifi other than electricity,
     * o/w true
     */
    public static boolean isConnected(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        WifiInfo wi = wm.getConnectionInfo();
        boolean connectedToElectricity = false;

        String myBSSID = wi.getBSSID();
        if(ni.isConnected() && myBSSID != null) //TODO: Beware of bugs!
        {
            Log.i("ConnectionReceiver", "Connected to: " + wi.getSSID());
            Toast.makeText(context, "Connected to: " + wi.getSSID(), Toast.LENGTH_SHORT).show();

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
        Log.i("ConnectionReceiver",
                (connectedToElectricity ? "C" : "Not c") + "onnected to E-city");
        return connectedToElectricity;
    }

    private void showNewQR(Context context)
    {
        String ip = getUserIP(context);
        Bitmap qr = (new QRGen()).getQR(ip);

        //write qr to file
        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), "qr-code"));
            qr.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        }
        catch(IOException e)
        {
            Log.e("QR file", e.getMessage());
            e.printStackTrace();
        }
        //TODO: Notify GUI of the updated QR image
    }

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
