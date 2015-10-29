package com.soctec.soctec.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.soctec.soctec.utils.FileHandler;

/**
 * Displays information about the app in a DialogFragment
 * @author Jesper Kjellqvist
 * @version 1.0
 */
public class InfoFragment extends DialogFragment
{
    /**
     * Creates the dialog window with a button and a title, and which text the window shall display.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        int resID = FileHandler.getInstance().getResourceID("about_text", "string");
        String aboutText = FileHandler.getInstance().readString(resID);

        return new AlertDialog.Builder(getActivity())
                        // Set Dialog Title
                .setTitle("Om")
                        // Set Dialog Message
                .setMessage(aboutText)

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