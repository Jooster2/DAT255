package com.soctec.soctec;

/**
 * Created by Jeppe on 2015-09-21.
 */

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Intent;

public class MainFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false );
        return view;
    }

    public void scanNow(View v)
    {
        startActivity(new Intent(MainFragment.class, CameraActivity.class));
    }

}
