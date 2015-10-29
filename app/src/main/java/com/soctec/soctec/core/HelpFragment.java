package com.soctec.soctec.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.soctec.soctec.utils.FileHandler;

/**
 * Specialized DialogFragment for displaying "help"
 * @author Jesper Kjellqvist
 * @version 1.0
 */
public class HelpFragment extends DialogFragment
{
    String toDisplay = "";

    /**
     * Manages what text the dialog fragment shall show when called upon.
     * @param messageType Different types of messages, first startup and post first startup.
     */
    public void show(FragmentManager fm, String tag, int messageType)
    {
        if(messageType == 0)
        {
            int resID = FileHandler.getInstance().getResourceID("help_main", "string");
            StringBuilder sb = new StringBuilder(FileHandler.getInstance().readString(resID));
            resID = FileHandler.getInstance().getResourceID("help_profile", "string");
            sb.append(FileHandler.getInstance().readString(resID));

            toDisplay = sb.toString();
        }
        else if(messageType == 1) //Display on first startup
        {
            int resID = FileHandler.getInstance().getResourceID("help_welcome", "string");
            StringBuilder sb = new StringBuilder(FileHandler.getInstance().readString(resID));
            resID = FileHandler.getInstance().getResourceID("help_main", "string");
            sb.append(FileHandler.getInstance().readString(resID));
            resID = FileHandler.getInstance().getResourceID("help_profile", "string");
            sb.append(FileHandler.getInstance().readString(resID));
            resID = FileHandler.getInstance().getResourceID("help_help", "string");
            sb.append(FileHandler.getInstance().readString(resID));

            toDisplay = sb.toString();
        }
        super.show(fm, tag);
    }

    /**
     * Creates the dialog window with a button and a title.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        return new AlertDialog.Builder(getActivity())
                // Set Dialog Title
                .setTitle("Hjälp")
                        // Set Dialog Message
                .setMessage(toDisplay)

                        // OK button
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dismiss();
                    }
                }).create();
    }
}