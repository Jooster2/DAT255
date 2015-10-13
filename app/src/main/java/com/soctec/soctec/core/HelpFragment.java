package com.soctec.soctec.core;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soctec.soctec.R;

/**
 * Created by Jeppe on 2015-10-13.
 */
public class HelpFragment extends DialogFragment
{
    public HelpFragment()
    {
        // EMPTY CONSTRUCTOR
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        return rootView;
    }
}
