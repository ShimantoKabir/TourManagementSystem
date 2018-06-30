package com.example.maask.tourmanagementsystem.LikelihoodFile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.maask.tourmanagementsystem.R;

import java.util.ArrayList;

/**
 * Created by Maask on 1/30/2018.
 */

public class LikelihoodPlaceShowingAdapter extends ArrayAdapter<StoreLikelihoodPlaces> {

    private Context context;
    private ArrayList<StoreLikelihoodPlaces> storeLikelihoodPlaces;

    public LikelihoodPlaceShowingAdapter(@NonNull Context context, ArrayList<StoreLikelihoodPlaces> storeLikelihoodPlaces) {
        super(context, R.layout.single_likelihood_places,storeLikelihoodPlaces);
        this.context = context;
        this.storeLikelihoodPlaces = storeLikelihoodPlaces;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.single_likelihood_places,parent,false);

        TextView place_name = convertView.findViewById(R.id.place_name);
        TextView place_address = convertView.findViewById(R.id.place_address);
        TextView place_pho = convertView.findViewById(R.id.place_pho);
        TextView place_rating = convertView.findViewById(R.id.rating);

        place_name.setText(storeLikelihoodPlaces.get(position).getPlaceName());
        place_address.setText("Address : "+storeLikelihoodPlaces.get(position).getPlaceAddress());
        place_pho.setText("Number : "+storeLikelihoodPlaces.get(position).getPlacePhone());
        place_rating.setText("Rating : "+String.valueOf(storeLikelihoodPlaces.get(position).getRating()));

        return convertView;
    }
}

