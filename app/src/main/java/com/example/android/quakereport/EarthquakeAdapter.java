package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by harsh24 on 27/2/18.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    EarthquakeAdapter(Context context, ArrayList<Earthquake> eqArrayList) {
        super(context,0,eqArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_item,parent,false);

        final Earthquake currentWord = getItem(position);

        TextView eqMag = (TextView) listItemView.findViewById(R.id.mag);
        double mag = Double.parseDouble(currentWord.getEqMag());
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        eqMag.setText(decimalFormat.format(mag));

        GradientDrawable magCircle = (GradientDrawable) eqMag.getBackground();
        int magColor = getMagnitudeColor(mag);
        magCircle.setColor(magColor);

        TextView eqPreLoc = (TextView) listItemView.findViewById(R.id.preLoc);
        String preLoc = currentWord.getEqLoc();
        int pos1 = preLoc.indexOf(" of ");
        if(pos1 != -1)
            eqPreLoc.setText(preLoc.substring(0,pos1+3));
        else
            eqPreLoc.setText("Near the ");

        TextView eqLoc = (TextView) listItemView.findViewById(R.id.loc);
        String place = currentWord.getEqLoc();
        int pos = place.indexOf(" of ");
        if(pos != -1)
            eqLoc.setText(place.substring(pos+4));
        else
            eqLoc.setText(place);

        TextView eqTime = (TextView) listItemView.findViewById(R.id.time);
        long time = Long.parseLong(currentWord.getEqTime());
        Date dateInms = new Date(time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy   hh:mm a", Locale.ENGLISH);
        eqTime.setText(dateFormat.format(dateInms));

        GradientDrawable dateBg = (GradientDrawable) eqTime.getBackground();
        int timeColor = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark);
        dateBg.setColor(timeColor);

        return listItemView;
    }

    private int getMagnitudeColor(double mag) {
        int color;

        if(mag >= 0 && mag <= 2)
            color = ContextCompat.getColor(getContext(),R.color.magnitude1);
        else if(mag > 2 && mag <= 3)
            color = ContextCompat.getColor(getContext(),R.color.magnitude2);
        else if(mag > 3 && mag <= 4)
            color = ContextCompat.getColor(getContext(),R.color.magnitude3);
        else if(mag > 4 && mag <= 5)
            color = ContextCompat.getColor(getContext(),R.color.magnitude4);
        else if(mag > 5 && mag <= 6)
            color = ContextCompat.getColor(getContext(),R.color.magnitude5);
        else if(mag > 6 && mag <= 7)
            color = ContextCompat.getColor(getContext(),R.color.magnitude6);
        else if(mag > 7 && mag <= 8)
            color = ContextCompat.getColor(getContext(),R.color.magnitude7);
        else if(mag > 8 && mag <= 9)
            color = ContextCompat.getColor(getContext(),R.color.magnitude8);
        else if(mag > 9 && mag <= 10)
            color = ContextCompat.getColor(getContext(),R.color.magnitude9);
        else
            color = ContextCompat.getColor(getContext(),R.color.magnitude10plus);

        return color;
    }

}
