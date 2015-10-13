package com.soctec.soctec.core;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.soctec.soctec.R;
import com.soctec.soctec.achievements.Achievement;
import com.soctec.soctec.utils.FileHandler;

public class AchievementShowerActivity extends Activity
{

    ImageView mainImageBackView;
    ImageView mainImageFrontView;
    private Achievement achievement;
    private int gold = 50;
    private int silver = 30;
    private int bronze = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_shower);

        achievement = (Achievement) getIntent().getSerializableExtra("AchievementObject");

        ImageView headImageView = (ImageView)findViewById(R.id.showerHeadView);
        //headImageView.setImageResource(R.drawable.shower_head_text);

        mainImageBackView = (ImageView)findViewById(R.id.showerAchBack);
        setAchievementImageBackground();

        mainImageFrontView = (ImageView)findViewById(R.id.showerAchFront);
        setAchievementImageFront();

        TextView nameView = (TextView)findViewById(R.id.showerNameText);
        nameView.setText(achievement.getName());

        TextView pointsView = (TextView)findViewById(R.id.showerPointsText);
        pointsView.setText("Detta ger "+ achievement.getPoints() +" poäng!");

        TextView flavorText = (TextView)findViewById(R.id.flavorTextView);
        flavorText.setText(achievement.getFlavorText());

        Button closeButton = (Button)findViewById(R.id.showerCloseButton);
        closeButton.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        finish();
                    }
                }
        );
    }

    public int getAchievementRank()
    {
        /*
        Får info vilken grad achievementet har. Tex guld, silver, brons
        Standard bakgrund=0 Brons=1 Silver=2 Guld=3

        alt 1: Varje achivement har bestämd rank
            return achievement.getRank();

        alt 2: Rank baseras på antal poäng
        */

        int points = achievement.getPoints();

        if(points >= gold)
            return 1;
        else if(points>=silver)
            return 2;
        else if (points>=bronze)
            return 3;
        else
            return 0;
    }

    public void setAchievementImageBackground()
    {
        //sätter olika bakgrunder beroende av rank
        int rank = getAchievementRank();

        switch (rank)
        {
            case 1:
                mainImageBackView.setImageResource(R.drawable.ach_back_test); //ach_back_gold
                break;

            case 2:
                mainImageBackView.setImageResource(R.drawable.ach_back_test); //ach_back_silver
                break;

            case 3:
                mainImageBackView.setImageResource(R.drawable.ach_back_test); //ach_back_bronze
                break;

            default:
                mainImageBackView.setImageResource(R.drawable.ach_back_test); //ach_back_default
                break;
        }
    }

    public void setAchievementImageFront()
    {
        FileHandler fileHandler = FileHandler.getInstance();
        mainImageFrontView.setImageResource(fileHandler.getResourceID(achievement.getImageName(), "drawable"));
    }
}