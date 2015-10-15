package com.soctec.soctec.core;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.soctec.soctec.R;
import com.soctec.soctec.achievements.Stats;
import com.soctec.soctec.network.NetworkHandler;

/**
 *
 * @author Robin Punell
 * @version 2.0
 */

public class MainFragment extends Fragment
{
    private int ratingUpdateFreq = 3; // Defines the update frequency of the progressbar
    ProgressBar progressBar;
    private ImageButton yesButton;
    private ImageButton noButton;
    private Stats stats;
    private GestureDetector gestureDetector;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false );
        MainActivity main = (MainActivity)getActivity();
        stats = main.getStats();

        return view;
    }

    /**
     * When the main UI has been created, start the BroadcastReceiver
     */
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState)
    {
        progressBar = (ProgressBar)getView().findViewById(R.id.rating_progress_bar);


        yesButton = (ImageButton) getView().findViewById(R.id.pos_button);
        yesButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        sendRatingToServer(true);
                        disableRatingButtons();
                        stats.setCanNotRate(true);
                    }
                }
        );

        noButton = (ImageButton) getView().findViewById(R.id.neg_button);
        noButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        sendRatingToServer(false);
                        disableRatingButtons();
                        stats.setCanNotRate(true);
                    }
                }
        );
        updateRatingBar();

        if(stats.hasRated())
        {
            disableRatingButtons();
        }
        else
        {
            enableRatingButtons();
        }

        ((MainActivity)getActivity()).startReceiver();
    }

    /**
     * Enables the rating buttons and sets the "enabled"-image design for the button.
     */
    public void enableRatingButtons()
    {
        yesButton.setEnabled(true);
        noButton.setEnabled(true);
        yesButton.setImageResource(R.drawable.thumb_up_blackgreen);
        noButton.setImageResource(R.drawable.thumb_down_blackred);
    }

    /**
     * Disables the rating buttons and sets the "disabled"-image design for the button.
     */
    public void disableRatingButtons()
    {
        yesButton.setEnabled(false);
        noButton.setEnabled(false);
        yesButton.setImageResource(R.drawable.thumb_up_grey);
        noButton.setImageResource(R.drawable.thumb_down_grey);
    }

    /**
     * Updates the progress of the rating bar.
     * Only updates the rating bar every "ratingUpdateFreq"-time based on the number of ratings.
     */
    public void updateRatingBar()
    {
        float rating = stats.getRating();
        float ratingPercent = rating*100;

        if ((stats.getTimesRated()+1) % ratingUpdateFreq == 0)
        {
            progressBar.setProgress((int) ratingPercent);
        }
    }

    /**
     * Tells NetworkHandler to send rating to server
     * @param positiveRating true, if thumbs up was clicked. false if thumbs down
     */
    private void sendRatingToServer(boolean positiveRating)
    {
        NetworkHandler.getInstance().pushRatingToServer(
                ((MainActivity) getActivity()).getStats().getlastScanned(),
                positiveRating);
    }
}

