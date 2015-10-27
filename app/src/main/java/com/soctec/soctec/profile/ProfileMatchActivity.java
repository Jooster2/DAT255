package com.soctec.soctec.profile;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Random;

import android.view.View;
import android.widget.ListView;

import com.soctec.soctec.R;

/**
 * Matches Strings in two different ArrayLists of ArrayLists of Strings
 * and displays the result
 * @author Carl-Henrik Hult
 * @version 1.0
 */
public class ProfileMatchActivity extends Activity
{
    ListView myListView;

    /**
     * Sets the layout for the profile match.
     * @param savedInstanceState
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profilematchwindow);
        myListView = (ListView)findViewById(R.id.matchListView);

        match((ArrayList <ArrayList <String>>)getIntent().getExtras().get("list1"),
              (ArrayList <ArrayList <String>>)getIntent().getExtras().get("list2"));
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    public void onFinishedClick(View v)
    {
        this.finish();
    }

    /**
     * Matches two profiles, and shows the things those profiles have in common.
     * The two different profiles are the two lists that is included as parameters.

     * @param thisDevice    the profile of the own device.
     * @param otherDevice   the profile of the scanned device.
     */
    public void match (ArrayList <ArrayList <String>> thisDevice, ArrayList <ArrayList<String>> otherDevice)
    {
        ArrayList <String> nameResult = new ArrayList<>();
        ArrayList <String> musicResult = new ArrayList<>();
        ArrayList <String> sportsResult = new ArrayList<>();
        ArrayList <String> movieResult = new ArrayList<>();
        ArrayList <String> miscResult = new ArrayList<>();
        /*
         * One list contains all results, where the different lists in the beginning is added.
         * Then we iterate through both thisDevice list and the otherDevice list and compares
         * every list in their lists with each other. Things that matches gets added to the fitting place
         * in the allresults list.(matched music results gets added to the music list in the list etc etc.)
         */
        ArrayList <ArrayList <String>> allResults = new ArrayList <ArrayList <String>> ();
        allResults.add(nameResult);
        allResults.add(musicResult);
        allResults.add(sportsResult);
        allResults.add(movieResult);
        allResults.add(miscResult);
        int indexOtherDevice = 0;

        for (ArrayList<String> thisCurrentList: thisDevice)
        {
            if (thisCurrentList != null && otherDevice.get(indexOtherDevice) != null)
            {
                for (String item : thisCurrentList)
                {
                    String categoryItems[] = item.split(",");
                    for (String oneCategoryItem : categoryItems)
                    {
                        for(String otherItem : otherDevice.get(indexOtherDevice))
                        {
                            String otherCategoryItems[] = otherItem.split(",");
                            for (String otherOneCategoryItem : otherCategoryItems)
                            {
                                if(!oneCategoryItem.equals("") &&
                                        oneCategoryItem.toLowerCase().trim().equals(otherOneCategoryItem.toLowerCase().trim()))
                                {
                                    allResults.get(indexOtherDevice).add(oneCategoryItem);
                                }
                            }
                        }
                    }

                }
            }
            indexOtherDevice++;
        }

        ArrayList <String> showList = new ArrayList<>();
        StringBuilder temp = new StringBuilder();

        if(nameResult.size() >= 1)
        {
            temp.append("Namn: ,");
            for(String item : nameResult)
            {
                temp.append(item);
            }
            showList.add(temp.toString());
        }

        if(musicResult.size() >= 1)
        {
            temp = new StringBuilder();
            temp.append("Musik: ,");
            for(String item : musicResult)
            {
                temp.append(item);
            }
            showList.add(temp.toString());
        }

        if(movieResult.size() >= 1)
        {
            temp = new StringBuilder();
            temp.append("Film: ,");
            for(String item : movieResult)
            {
                temp.append(item);
            }
            showList.add(temp.toString());
        }
        if(sportsResult.size() >= 1)
        {
            temp = new StringBuilder();
            temp.append("Sport: ,");
            for(String item : sportsResult)
            {
                temp.append(item);
            }
            showList.add(temp.toString());
        }
        if(miscResult.size() >= 1)
        {
            temp = new StringBuilder();
            temp.append("Övrigt: ,");
            for(String item : miscResult)
            {
                temp.append(item);
            }
            showList.add(temp.toString());
        }
        if(showList.isEmpty())
        {
            showList.add("Ni verkar inte ha några gemensamma intressen att diskutera.," +
                                 "\n\nTesta att prata om " + getRandomWord() + " istället!");
        }

        ProfileMatchAdapter matchAdapter = new ProfileMatchAdapter(this, showList);
        myListView.setAdapter(matchAdapter);
    }

    private String getRandomWord()
    {
        switch(new Random().nextInt(10))
        {
            case 0:
                return "bussar";
            case 1:
                return "vädret";
            case 2:
                return "Västtrafik";
            case 3:
                return "Electricity";
            case 4:
                return "kvantfysik";
            case 5:
                return "din favoritfärg";
            case 6:
                return "bläckfiskar";
            case 7:
                return "er gemensamma släkting";
            case 8:
                return "nyheterna";
            case 9:
                return "Dignum";
            default:
                return "något annat";
        }
    }

    /**
     * Used only for testing
     * @param thisDevice Profile from this device
     * @param otherDevice Profile from other device
     * @return The list of matching items
     */
    public ArrayList <String> match2 (ArrayList <ArrayList <String>> thisDevice, ArrayList <ArrayList<String>> otherDevice)
    {
        ArrayList <String> nameResult = new ArrayList<>();
        ArrayList <String> musicResult = new ArrayList<>();
        ArrayList <String> sportsResult = new ArrayList<>();
        ArrayList <String> movieResult = new ArrayList<>();
        ArrayList <String> miscResult = new ArrayList<>();
        /*
         * One list contains all results, where the different lists in the beginning is added.
         * Then we iterate through both thisDevice list and the otherDevice list and compares
         * every list in their lists with each other. Things that matches gets added to the fitting place
         * in the allresults list.(matched music results gets added to the music list in the list etc etc.)
         */
        ArrayList <ArrayList <String>> allResults = new ArrayList <ArrayList <String>> ();
        allResults.add(nameResult);
        allResults.add(musicResult);
        allResults.add(sportsResult);
        allResults.add(movieResult);
        allResults.add(miscResult);
        int indexOtherDevice = 0;

        for (ArrayList<String> thisCurrentList: thisDevice)
        {
            if (thisCurrentList != null && otherDevice.get(indexOtherDevice) != null)
            {
                for (String item : thisCurrentList)
                {
                    String categoryItems[] = item.split(",");
                    for (String oneCategoryItem : categoryItems)
                    {
                        for(String otherItem : otherDevice.get(indexOtherDevice))
                        {
                            String otherCategoryItems[] = otherItem.split(",");
                            for (String otherOneCategoryItem : otherCategoryItems)
                            {
                                if(!oneCategoryItem.equals("") &&
                                        oneCategoryItem.toLowerCase().trim().equals(otherOneCategoryItem.toLowerCase().trim()))
                                {
                                    allResults.get(indexOtherDevice).add(oneCategoryItem);
                                }
                            }
                        }
                    }

                }
            }
            indexOtherDevice++;
        }

        ArrayList <String> showList = new ArrayList<>();
        StringBuilder temp = new StringBuilder();

        if(nameResult.size() >= 1)
        {
            temp.append("Namn: ,");
            for(String item : nameResult)
            {
                temp.append(item);
            }
            showList.add(temp.toString());
        }

        if(musicResult.size() >= 1)
        {
            temp = new StringBuilder();
            temp.append("Musik: ,");
            for(String item : musicResult)
            {
                temp.append(item);
            }
            showList.add(temp.toString());
        }

        if(movieResult.size() >= 1)
        {
            temp = new StringBuilder();
            temp.append("Film: ,");
            for(String item : movieResult)
            {
                temp.append(item);
            }
            showList.add(temp.toString());
        }
        if(sportsResult.size() >= 1)
        {
            temp = new StringBuilder();
            temp.append("Sport: ,");
            for(String item : sportsResult)
            {
                temp.append(item);
            }
            showList.add(temp.toString());
        }
        if(miscResult.size() >= 1)
        {
            temp = new StringBuilder();
            temp.append("Övrigt: ,");
            for(String item : miscResult)
            {
                temp.append(item);
            }
            showList.add(temp.toString());
        }

        return showList;
    }
}
