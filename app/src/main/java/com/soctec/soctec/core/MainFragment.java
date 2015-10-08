package com.soctec.soctec.core;

/**
 * Created by Jeppe on 2015-09-21.
 */

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.soctec.soctec.R;
import com.soctec.soctec.network.ConnectionChecker;

public class MainFragment extends Fragment
{
    private GestureDetector gestureDetector;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false );
        return view;
    }

    /**
     * When the main UI has been created, start the BroadcastReceiver
     */
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState)
    {
        ((MainActivity)getActivity()).startReceiver();
    }
}
