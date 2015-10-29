package com.soctec.soctec.core;

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
import java.util.LinkedList;

/**
 * Adapter for displaying several properties from
 * {@link com.soctec.soctec.achievements.Achievement} in a single ListView slot
 * @author Jesper Kjellqvist, Carl-Henrik Hult
 * @version 1.1
 */

public class AchievementsAdapter extends ArrayAdapter
{

    private final Activity context;
    LinkedList<String> itemNames;
    private boolean locked;

    /**
     * Constructor that sets up the items in the list view and what they contain.
     * @param context  Context text below the item name.
     * @param itemNames Name of the items.
     * @param locked If the achievement in the list is still locked, it will show with a grey color.
     */
    public AchievementsAdapter(Activity context, LinkedList<String> itemNames, boolean locked)
    {
        super(context, R.layout.row_achievements, itemNames);
        this.itemNames = itemNames;
        this.context=context;
        this.locked=locked;
    }

    /**
     * Sets up the view within the list.
     * @param position Position in the list view.
     * @param view The list view.
     * @param parent The parent to this view.
     * @return Returns the view.
     */

    @Override
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

        if(locked == true)
        imageView.setColorFilter(Color.GRAY);

        return rowView;
    }
}