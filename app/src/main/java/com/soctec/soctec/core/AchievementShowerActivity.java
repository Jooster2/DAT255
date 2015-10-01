package com.soctec.soctec.core;

import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.soctec.soctec.R;

public class AchievementShowerActivity extends Activity
{
    private Image mainImageBack;
    private Image mainImageFront;
    private Image headText;
    private int headTextId;
    private int frontImageId;
    private int backImageId;

    //private Image button;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_shower);

        ImageView headImage = (ImageView)findViewById(R.id.showerHeadView);
        headImage.setImageResource(headTextId);


        ImageView mainImageBackView = (ImageView)findViewById(R.id.showerAchViewBack);
        mainImageBackView.setImageResource(backImageId);


        ImageView mainImageFrontView = (ImageView)findViewById(R.id.showerAchViewFront);
        mainImageFrontView.setImageResource(frontImageId);


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




    public int getAchievementImageId()
    {
        return 1;
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
