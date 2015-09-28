package com.soctec.soctec;

/**
 * Created by Jeppe on 2015-09-21.
 */

import android.content.DialogInterface;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainFragment extends Fragment implements OnClickListener
{
    ImageButton btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false );
        btn = (ImageButton) view.findViewById(R.id.imageButton);
        btn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        startActivity(intent);
    }
}
