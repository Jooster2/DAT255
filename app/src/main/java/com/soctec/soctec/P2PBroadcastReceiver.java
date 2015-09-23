package com.soctec.soctec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;

/**
 * @author David
 * @version Created on 2015-09-22
 */
public class P2PBroadcastReceiver extends BroadcastReceiver
{
    private WifiP2pManager myManager;
    private WifiP2pManager.Channel myChannel;
    private MainActivity myActivity;
    private ArrayList<WifiP2pDevice> peers = new ArrayList<>();

    /**
     * Constructor
     * @param manager A WifiP2pManager
     * @param channel A WifiP2pManager channel
     * @param activity The main activity
     */
    public P2PBroadcastReceiver(WifiP2pManager manager,
                                       WifiP2pManager.Channel channel,
                                       MainActivity activity)
    {
        super();
        myManager = manager;
        myChannel = channel;
        myActivity = activity;
    }

    /**
     * Called when peers are found
     * @param context Context of calling activity
     * @param intent A random intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener()
        {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList)
            {
                peers.clear();
                peers.addAll(peerList.getDeviceList());
                for(WifiP2pDevice peer : peers)
                {
                    if(peer.deviceName.equals("<Insert device name here>"))
                    {
                        //obtain a peer from the WifiP2pDeviceList
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.deviceAddress = peer.deviceAddress;
                        myManager.connect(myChannel, config, new WifiP2pManager.ActionListener()
                        {

                            @Override
                            public void onSuccess()
                            {
                                //connect
                                NetworkHandler.sendScanInfo(
                                        "<Insert scanned code here>", myActivity);
                            }

                            @Override
                            public void onFailure(int reason)
                            {
                                //failure
                            }
                        });
                    }
                }
            }
        };

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                myActivity.setIsWifiP2pEnabled(true);
            }
            else
            {
                myActivity.setIsWifiP2pEnabled(false);
            }

        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (myManager != null) {
                myManager.requestPeers(myChannel, peerListListener);
            }
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            // Respond to new connection or disconnections
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
        {
            // Respond to this device's wifi state changing
        }
    }
}
