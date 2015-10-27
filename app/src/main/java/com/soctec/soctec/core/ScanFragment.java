package com.soctec.soctec.core;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soctec.soctec.R;
/**
 * Filler for ViewPager
 * @author Carl-Henrik Hult
 * @version 1.0
 */
public class ScanFragment extends android.support.v4.app.Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_scan, container, false );
        return view;
    }

}
