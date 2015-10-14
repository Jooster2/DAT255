package com.soctec.soctec.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Jeppe on 2015-10-12.
 */

public class InfoFragment extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return new AlertDialog.Builder(getActivity())
                        // Set Dialog Title
                .setTitle("Om")
                        // Set Dialog Message
                .setMessage("Skapad av SocTec som består av Joakim Schmidt, David Johnsson, Robin Punell, Carl Henrik Hult och Jesper Kjellqvist")

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