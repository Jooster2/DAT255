package com.soctec.soctec.core;

/**
 * Created by Jeppe on 2015-10-07.
 */
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.soctec.soctec.R;
import com.soctec.soctec.utils.FileHandler;
import java.util.ArrayList;
import java.util.LinkedList;

public class AchievementsAdapter extends ArrayAdapter
{

    private final Activity context;
    LinkedList<String> itemNames;
    private boolean locked;//todo ändrat

    public AchievementsAdapter(Activity context, LinkedList<String> itemNames, boolean locked)//todo ändrat
    {
        super(context, R.layout.row_achievements, itemNames);
        // TODO Auto-generated constructor stub (why is this TODO?)
        this.itemNames = itemNames;
        this.context=context;
        this.locked=locked;//todo ändrat
    }

    public View getView(int position,View view,ViewGroup parent)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.row_achievements, parent, false);

        TextView titleText = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView flavorText = (TextView) rowView.findViewById(R.id.context);

        String fullString = itemNames.get((itemNames.size()-1) - position);
        String [] strings = fullString.split(",");

        titleText.setText(strings[0]);
        flavorText.setText(strings[1]);
        FileHandler fh = FileHandler.getInstance();
        imageView.setImageResource(fh.getResourceID(strings[2], "drawable"));

        //todo ändrat
        if(locked == true)
        imageView.setColorFilter(Color.GRAY);

        return rowView;
    }
}