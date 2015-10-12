package com.soctec.soctec.core;

/**
 * Created by Jeppe on 2015-10-07.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.soctec.soctec.R;
import com.soctec.soctec.utils.FileHandler;
import java.util.ArrayList;

public class AchievementsAdapter extends ArrayAdapter {

    private final Activity context;
    ArrayList<String> itemNames;


    public AchievementsAdapter(Activity context, ArrayList<String> itemNames) {
        super(context, R.layout.row_achievements, itemNames);
        // TODO Auto-generated constructor stub
        this.itemNames = itemNames;
        this.context=context;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.row_achievements, parent, false);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.context);

        String fullString = itemNames.get(position);
        String [] strings = fullString.split(",");

        txtTitle.setText(strings[0]);
        //extratxt.setText(strings[1]);
        FileHandler fh = FileHandler.getInstance();
        imageView.setImageResource(fh.getResourceID(strings[1], "drawable"));

        return rowView;
    };
}