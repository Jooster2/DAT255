package com.soctec.soctec.core;

/**
 * Created by Jeppe on 2015-09-22.
 */

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

import com.soctec.soctec.R;

public class AchievementsFragment extends Fragment
{
    ImageButton unlocked, locked;
    ViewSwitcher viewSwitcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);

        unlocked = (ImageButton) view.findViewById(R.id.unlocked);
        locked = (ImageButton) view.findViewById(R.id.locked);
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.viewSwitcher);

        unlocked.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewSwitcher.showNext();
            }
        });

        locked.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewSwitcher.showNext();
            }
        });
        return view;
    }
}
