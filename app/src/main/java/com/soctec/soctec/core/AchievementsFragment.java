package com.soctec.soctec.core;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.soctec.soctec.R;
import com.soctec.soctec.achievements.Achievement;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Jeppe on 2015-09-22.
 */

public class AchievementsFragment extends Fragment
{
    ImageButton unlockedButton, lockedButton;
    ViewSwitcher viewSwitcher;
    View view;
    MainActivity main;

    /**
     * Creates and sets up the fragment, buttons, and view switcher.
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_achievements, container, false);

        unlockedButton = (ImageButton) view.findViewById(R.id.unlocked);
        lockedButton = (ImageButton) view.findViewById(R.id.locked);
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.viewSwitcher);

        unlockedButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewSwitcher.showNext();
            }
        });

        lockedButton.setOnClickListener(new View.OnClickListener()
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
        main = (MainActivity)getActivity();
        main.updateAchievementFragment();
    }

    /**
     * Refreshes the locked and unlocked achievements in the list view.
     * @param locked The locked achievements will be stored in this Arraylist.
     * @param unlocked The unlocked achievements will be stored in this Arraylist.
     */

    public void refreshAchievements(final ArrayList<Achievement> locked, final ArrayList<Achievement> unlocked)
    {

        final LinkedList<String> unlockedList =  new LinkedList<>();
        ListView unlockedAchievementListView;

        for (Achievement achi : unlocked)
        {
            StringBuilder sb = new StringBuilder(achi.getName() + "," + achi.getFlavorText());
            if(achi.getType().equals("INF"))
            {

                if(achi.getCompletedDemands().size() > 0)
                {
                    while(sb.toString().contains("#"))
                    {
                        String requirement;
                        if(achi.getId().contains("T"))
                            requirement = achi.getCompletedDemands().get(0).getMinuteRequirement();
                        else
                            requirement = achi.getCompletedDemands().get(0).requirement;
                        sb.replace(sb.indexOf("#"), sb.indexOf("#") + 1, requirement);
                    }

                }
                //Else is not necessary while locked achievements are "invisible"
            }
            else
            {
                if(achi.getCompletedDemands().size() > 0)
                {
                    while(sb.toString().contains("#"))
                    {
                        String requirement;
                        if(achi.getId().contains("T"))
                            requirement = achi.getCompletedDemands().get(0).getMinuteRequirement();
                        else
                            requirement = achi.getCompletedDemands().get(0).requirement;
                        sb.replace(sb.indexOf("#"), sb.indexOf("#") + 1, requirement);
                    }
                }
            }
            sb.append(",");
            sb.append(achi.getImageName());
            unlockedList.addLast(sb.toString());
        }
        unlockedAchievementListView = (ListView)view.findViewById(R.id.listunlocked);

        AchievementsAdapter unlockedAdapter = new AchievementsAdapter(getActivity(), unlockedList, false);
        unlockedAchievementListView.setAdapter(unlockedAdapter);
        unlockedAchievementListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id)
            {
                Achievement achi = unlocked.get((unlocked.size()-1) - (int)id);
                Intent showerIntent = new Intent(main, AchievementShowerActivity.class);
                showerIntent.putExtra("AchievementObject", achi);
                startActivity(showerIntent);
            }
        });

        LinkedList<String> lockedList = new LinkedList<>();
        ListView lockedAchievementListView;

        for(Achievement achi : locked)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Låst Utmärkelse,,");
            sb.append(achi.getImageName());
            lockedList.addFirst(sb.toString());
        }

        lockedAchievementListView = (ListView) view.findViewById(R.id.listlocked);
        AchievementsAdapter lockedAdapter = new AchievementsAdapter(getActivity(), lockedList, true);
        lockedAchievementListView.setAdapter(lockedAdapter);
    }

    /**
     * Displays the total of points the user have gathered.
     * @param points Points the user have gathered is stored in this variable.
     */
            public void setPoints(int points)
            {
                TextView textView = (TextView) view.findViewById(R.id.pointsAchi);
                textView.setText("Dina poäng: " + points);
            }
        }
