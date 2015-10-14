package com.soctec.soctec.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.soctec.soctec.utils.FileHandler;

import java.io.File;

/**
 * Created by Jeppe on 2015-10-12.
 */

public class InfoFragment extends DialogFragment
{
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