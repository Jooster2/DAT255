package com.soctec.soctec.core;

import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.soctec.soctec.R;
import com.soctec.soctec.achievements.Achievement;

public class AchievementShowerActivity extends Activity
{
    private Image mainImageBack;
    private Image mainImageFront;
    private Image headText;
    private int headTextId;
    private int frontImageId;
    private int backImageId;
    ImageView mainImageBackView;
    ImageView mainImageFrontView;
    private Achievement achievement;
    //private Image button;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        achievement = (Achievement) getIntent().getSerializableExtra("achievementObject");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_shower);

        ImageView headImageView = (ImageView)findViewById(R.id.showerHeadView);
        headImageView.setImageResource(R.drawable.shower_head_text);


        mainImageBackView = (ImageView)findViewById(R.id.showerAchBack);
        //mainImageBackView.setImageResource(backImageId);


        mainImageFrontView = (ImageView)findViewById(R.id.showerAchFront);
        mainImageFrontView.setImageResource(frontImageId);

        TextView nameView = (TextView)findViewById(R.id.showerNameText);
        nameView.setText(getName());

        TextView pointsView = (TextView)findViewById(R.id.showerPointsText);
        pointsView.setText("Detta ger "+ getPoints() +" poäng!");

        ImageView closeButtonImg = (ImageView)findViewById(R.id.showerCloseButtonImg);
        closeButtonImg.setImageResource(R.drawable.shower_close_button);

    }





    public String getName()
    {
        return achievement.getName();
    }
    public int getPoints()
    {
        return achievement.getPoints();
    }

    public int getAchievementImageId()
    {
        return 1;
    }



    public int getAchievementRank()
    {
        return achievement.getRank();
        //Får info vilken grad achievementet har. Tex guld, silver, brons
        //alternativ: Standard bakgrund=0 Brons=1 Silver=2 Guld=3
        return 1;
    }

    //sätter olika bakgrunder beroende på rank
    public void setAchievementImageBackground()
    {
        int rank = getAchievementRank();

        switch (rank)
        {
            case 1:
                mainImageBackView.setImageResource(R.drawable.ach_back_1);
                break;

            case 2:
                mainImageBackView.setImageResource(R.drawable.ach_back_2);
                break;

            case 3:
                mainImageBackView.setImageResource(R.drawable.ach_back_3);
                break;

            default:
                mainImageBackView.setImageResource(R.drawable.ach_back_default);
                break;
        }
    }

    public void setAchievementImageFront()
    {
        //achievement.getImageName();
        //mainImageFrontView.setImageResource(R.drawable);
    }


}
