package com.soctec.soctec.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.soctec.soctec.utils.FileHandler;

/**
 * Created by Jeppe on 2015-10-12.
 */

public class HelpFragment extends DialogFragment
{
    String toDisplay = "";

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