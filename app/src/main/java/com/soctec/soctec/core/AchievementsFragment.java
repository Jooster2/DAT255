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
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.soctec.soctec.R;
import com.soctec.soctec.achievements.Achievement;
import com.soctec.soctec.core.AchievementsAdapter;

import java.util.ArrayList;

public class AchievementsFragment extends Fragment
{
    ImageButton unlocked, locked;
    ViewSwitcher viewSwitcher;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_achievements, container, false);

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        MainActivity main = (MainActivity)getActivity();
        main.updateAchievementFragment();
    }

    public void refreshAchievements(ArrayList<Achievement> locked, ArrayList<Achievement> unlocked)
    {

        ArrayList<String> unlockedList =  new ArrayList<>();
        ListView unlockedAchievementListView;

        for (Achievement achi : unlocked)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(achi.getName() + ",");
            //if (achi.getType().equals("INF");
                //sb.append(achi.getFlavorText() + " "+ stats.getScans() + " av "+achi.getDemands+ ",");
            //sb.append(achi.getFlavorText() + ",");
            sb.append("walla,");
            sb.append(achi.getImageName());
            unlockedList.add(sb.toString());
        }

        unlockedAchievementListView = (ListView)view.findViewById(R.id.listunlocked);
        AchievementsAdapter unlockedAdapter = new AchievementsAdapter(getActivity(), unlockedList);
        unlockedAchievementListView.setAdapter(unlockedAdapter);

        ArrayList<String> lockedList =  new ArrayList<>();
        ListView lockedAchievementListView;

        for (Achievement achi : locked)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(achi.getName() + ",");
            //sb.append(achi.getFlavorText() + ",");
            sb.append("walla,");
            sb.append(achi.getImageName());
            lockedList.add(sb.toString());
        }

        lockedAchievementListView = (ListView)view.findViewById(R.id.listlocked);
        AchievementsAdapter lockedAdapter = new AchievementsAdapter(getActivity(), lockedList);
        lockedAchievementListView.setAdapter(lockedAdapter);
    }
    public void setPoints (int points){
        TextView textView = (TextView)view.findViewById(R.id.pointsAchi);
        textView.setText("Dina poäng: "+ points);
    }
}
