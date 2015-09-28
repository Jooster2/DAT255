package com.soctec.soctec;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Carl-Henrik Hult on 2015-09-25.
 */
public class ProfileMatchWindowActivity extends Activity
{
    ListView myListView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profilematchwindow);
        myListView = (ListView)findViewById(R.id.matchListView);


    }
    @Override
    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }

    public void match (ArrayList <ArrayList <String>> thisDevice, ArrayList <ArrayList<String>> otherDevice)
    {
        ArrayList <String> musicResult = new ArrayList<String>();
        ArrayList <String> movieResult = new ArrayList<String>();
        ArrayList <String> litteratureResult = new ArrayList<String>();
        ArrayList <String> sportsResult = new ArrayList<String>();

        ArrayList <ArrayList <String>> allResults = new ArrayList <ArrayList <String>> ();
        allResults.add(musicResult);
        allResults.add(movieResult);
        allResults.add(litteratureResult);
        allResults.add(sportsResult);
        int indexOtherDevice = 0;

        for (ArrayList<String> thisCurrentList: thisDevice)
        {
            if (thisCurrentList != null && otherDevice.get(indexOtherDevice) != null)
            {
                for (String item : thisCurrentList)
                {
                    for (String otherItem : otherDevice.get(indexOtherDevice))
                    {
                        if (item.toLowerCase().equals(otherItem.toLowerCase()))
                        {
                            allResults.get(indexOtherDevice).add(item);
                        }
                    }

                }

            }
            indexOtherDevice++;

        }
        ArrayList <String> showList = new ArrayList<>();
        if(musicResult != null)
        {
            StringBuilder temp = new StringBuilder();
            temp.append ("MUSIC: ,");
            for (String item : musicResult)
            {
                temp.append (item);
            }
            showList.add (temp.toString());
        }
        if(movieResult != null)
        {
            StringBuilder temp = new StringBuilder();
            temp.append ("MOVIE: ,");
            for (String item : movieResult)
            {
                temp.append (item);
            }
            showList.add (temp.toString());
        }
        if(litteratureResult != null)
        {
            StringBuilder temp = new StringBuilder();
            temp.append ("LITTERATURE: ,");
            for (String item : litteratureResult)
            {
                temp.append (item);
            }
            showList.add (temp.toString());

        }
        if(sportsResult != null)
        {
            StringBuilder temp = new StringBuilder();
            temp.append ("SPORTS: ,");
            for (String item : sportsResult)
            {
                temp.append (item);
            }
            showList.add (temp.toString());
        }
        MatchWindowAdapter matchAdapter = new MatchWindowAdapter(this, showList);
        myListView.setAdapter(matchAdapter);

    }


}
