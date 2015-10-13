package com.soctec.soctec.core;


import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.soctec.soctec.R;
import com.soctec.soctec.achievements.Stats;
import com.soctec.soctec.network.ConnectionChecker;

/**
 *
 * @author Robin Punell
 * @version 2.0
 */

public class MainFragment extends Fragment
{

    private int ratingUpdateFreq = 5; // Defines the update frequency of the progressbar
    ProgressBar progressBar;
    ImageButton yesButton;
    ImageButton noButton;
    Stats stats;


    private GestureDetector gestureDetector;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false );

        MainActivity main = (MainActivity)getActivity();
        stats = main.getStats();

        progressBar = (ProgressBar)getView().findViewById(R.id.progressBar);

        yesButton = (ImageButton) getView().findViewById(R.id.pos_button);
        yesButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        //SEND incPosRating();to other user
                        disableRatingButtons();
                    }
                }
        );


        noButton = (ImageButton) getView().findViewById(R.id.neg_button);
        noButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        //SEND incNegRating(); to other user
                        disableRatingButtons();
                    }
                }
        );



        return view;
    }

    /**
     * When the main UI has been created, start the BroadcastReceiver
     */
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState)
    {
        ((MainActivity)getActivity()).startReceiver();
    }


    public void enableRatingButtons(){
        yesButton.setEnabled(true);
        noButton.setEnabled(true);
        yesButton.setImageResource(R.drawable.thumb_up_black_36dp); //TODO Fixa färgade tummar
        noButton.setImageResource(R.drawable.thumb_down_black_36dp);
    }
    public void disableRatingButtons(){
        yesButton.setEnabled(false);
        noButton.setEnabled(false);
        yesButton.setImageResource(R.drawable.thumb_up_grey_test); //TODO Fixa bleka tummar
        noButton.setImageResource(R.drawable.thumb_up_grey_test);
    }

    public void updateRatingBar()
    {
        float rating = stats.getRating();
        float ratingPercent = rating*100;

        if (stats.getTimesRated() % ratingUpdateFreq == 0) //just show new rating every "ratingUpdateFreq" time
        {
            progressBar.setProgress((int) ratingPercent);
        }
    }
}


//TODO ny update beroede på hur vi tar in data från server
//TODO Ny grafik: "Utmärkelse!", Grön (y), Röd (n), Blek (y), Blek (n)


