package com.soctec.soctec.profile;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

import android.widget.ListView;

import com.soctec.soctec.R;

/**
 * Created by Carl-Henrik Hult on 2015-09-25.
 */
public class ProfileMatchActivity extends Activity
{
    ListView myListView;

    /**
     * Sets the layout for the profile match.
     * @param savedInstanceState
     */
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

    /**
     * Matches two profiles, and shows the things those profiles have in common.
     * The two different profiles is the two lists that is included as parameters.

     * @param thisDevice    the profile of the own device.
     * @param otherDevice   the profile of the scanned device.
     */
    public void match (ArrayList <ArrayList <String>> thisDevice, ArrayList <ArrayList<String>> otherDevice)
    {
        ArrayList <String> musicResult = new ArrayList<String>();
        ArrayList <String> movieResult = new ArrayList<String>();
        ArrayList <String> litteratureResult = new ArrayList<String>();
        ArrayList <String> sportsResult = new ArrayList<String>();
        /*
         * One list contains all results, where the different lists in the beginning is added.
         * Then we iterate through both thisDevice list and the otherDevice list and compares
         * every list in their lists with each other. Things that matches gets added to the fitting place
         * in the allresults list.(matched music results gets added to the music list in the list etc etc.)
         */
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
        ProfileMatchAdapter matchAdapter = new ProfileMatchAdapter(this, showList);
        myListView.setAdapter(matchAdapter);

    }


}
