package com.soctec.soctec.core;

import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.view.View;
import android.widget.Button;


import com.soctec.soctec.R;

public class AchievementShowerActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_shower);


        Button quitButton = (Button) findViewById(R.id.showerQuitButton);
        quitButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View V)
                    {
                        finish();
                    }
                }
        );
    }




    public void getAchievementImage() // Image
    {

    }
    public void getHeadText()
    {

    }
    public void getAchievementPoints()
    {

    }
    public int getAchievementRank()
    {
        //Får info vilken grad achievementet har. Tex guld, silver, brons
        return 1;
    }

    public void setAchievementImageBackground()
    {
        int rank = getAchievementRank();
        //sätter olika bakgrunder beroende på rank
    }


}
