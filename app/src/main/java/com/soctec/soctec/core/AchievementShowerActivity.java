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

    private ImageView mainImageBackView;
    private ImageView mainImageFrontView;
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
        int scanCount = getIntent().getIntExtra("ScanCount", 0);

        mainImageBackView = (ImageView)findViewById(R.id.showerAchBack);
        setAchievementImageBackground();

        mainImageFrontView = (ImageView)findViewById(R.id.showerAchFront);
        setAchievementImageFront();

        TextView nameView = (TextView)findViewById(R.id.showerNameText);
        nameView.setText(achievement.getName());

        TextView pointsView = (TextView)findViewById(R.id.showerPointsText);
        pointsView.setText("Detta ger "+ achievement.getPoints() +" poäng!");

        TextView flavorText = (TextView)findViewById(R.id.flavorTextView);
        if(achievement.getType().equals("INF"))
        {
            StringBuilder sb = new StringBuilder(achievement.getFlavorText() + " ");
            if(achievement.getCompletedDemands().size() > 0)
            {
                sb.append(achievement.getCompletedDemands().get(0).requirement +
                        " av " + achievement.getCompletedDemands().get(0).requirement);
            }
            else
            {
                sb.append(scanCount + " av " + achievement.getDemands().get(0).requirement);
            }
            flavorText.setText(sb.toString());
        }
        else
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

    /**
     * Calculates the rank of the current achievement and returns it in form of an int.
     * The rank is based on the points of the current achievement.
     * Gold=3 Silver=2 Bronze=1 standard=0
     * @return The calculated rank value.
     */
    public int getAchievementRank()
    {
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

    /**
     * Sets the achievement background image. The image is based on the rank of the achievement.
     */
    public void setAchievementImageBackground()
    {
        int rank = getAchievementRank();

        switch (rank)
        {
            case 1:
                mainImageBackView.setImageResource(R.drawable.gold_wreath);
                break;

            case 2:
                mainImageBackView.setImageResource(R.drawable.silver_wreath);
                break;

            case 3:
                mainImageBackView.setImageResource(R.drawable.bronze_wreath);
                break;

            default:
                mainImageBackView.setImageResource(R.drawable.bronze_wreath); //ach_back_default
                break;
        }
    }

    /**
     * Sets the Achievement front image.
     * Uses the FileHandler to collect the right image for the current achievement.
     */
    public void setAchievementImageFront()
    {
        FileHandler fileHandler = FileHandler.getInstance();
        mainImageFrontView.setImageResource(fileHandler.getResourceID(achievement.getImageName(), "drawable"));
    }
}