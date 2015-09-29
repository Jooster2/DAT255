package com.soctec.soctec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @author David
 * @version 2015-09-29
 */
public class ConnectionReceiver extends BroadcastReceiver
{
    //Got BSSIDs from https://gist.github.com/hjorthjort/fb2fcf80c773ea90c6fa
    String[] bssids = {
            "04:f0:21:10:09:df",
            "04:f0:21:10:09:b9",
            "04:f0:21:10:09:e8",
            "04:f0:21:10:09:b7",
            "04:f0:21:10:09:53",
    };

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        WifiInfo wi = wm.getConnectionInfo();
        boolean connectionToElectricity = false;

        if(ni.isConnected())
        {
            Toast.makeText(context, "Connected to: " + wi.getSSID(), Toast.LENGTH_SHORT).show();
            String myBssid = wi.getBSSID();
            /*TODO: Check for redundant broadcast, i.e. when a broadcast is received when
                     the app is resumed but still connected to same wifi.
                     An idea: save previous BSSID and check if equal */

            for(String id : bssids)
            {
                if(id.equals(myBssid))
                {
                    Log.i("ConnectionReceiver", "Connected to electricity wifi");
                    makeQR();
                    connectionToElectricity = true;
                    break;
                }
            }
        }

        if(connectionToElectricity)
        {
            //TODO: Tell MainActivity to show QR-code.
        }
        else
        {
            //TODO: Tell MainActivity to NOt show QR-code
        }
    }

    private void makeQR()
    {

    }
}
