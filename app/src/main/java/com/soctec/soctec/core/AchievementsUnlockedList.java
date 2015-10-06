package com.soctec.soctec.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.ListFragment;

/**
 * Created by Jeppe on 2015-10-06.
 */
public class AchievementsUnlockedList extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] values = new String[] {
                "Unlocked Achievement",
                "Unlocked Achievement",
                "Unlocked Achievement",
                "Unlocked Achievement",
                "Unlocked Achievement",
                "Unlocked Achievement",
                "Unlocked Achievement",
                "Unlocked Achievement",
                "Unlocked Achievement",
                "Unlocked Achievement"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                                                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO implement some logic
    }
}
