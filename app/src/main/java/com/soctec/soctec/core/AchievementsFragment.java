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
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.soctec.soctec.R;
import com.soctec.soctec.achievements.AchievementsAdapter;

import java.util.ArrayList;

public class AchievementsFragment extends Fragment
{
    ImageButton unlocked, locked;
    ViewSwitcher viewSwitcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);

        ArrayList<String> theList =  new ArrayList<String> ();
        ListView unlockedAchievementListView;
        theList.add("Hej, someimg,extratext");
        theList.add("på");
        theList.add("dig");
        theList.add("jesper!");
        unlockedAchievementListView = (ListView)view.findViewById(R.id.listunlocked);
        AchievementsAdapter unlockedadapter = new AchievementsAdapter(getActivity(), theList);
        unlockedAchievementListView.setAdapter(unlockedadapter);

        ArrayList<String> theOtherList =  new ArrayList<String> ();
        ListView lockedAchievmentListView;
        theOtherList.add("prutt, someimg,extratext");
        theOtherList.add("på");
        theOtherList.add("dig");
        theOtherList.add("jesper!");
        lockedAchievmentListView = (ListView)view.findViewById(R.id.listlocked);
        AchievementsAdapter lockedadapter = new AchievementsAdapter(getActivity(), theOtherList);
        lockedAchievmentListView.setAdapter(lockedadapter);

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
