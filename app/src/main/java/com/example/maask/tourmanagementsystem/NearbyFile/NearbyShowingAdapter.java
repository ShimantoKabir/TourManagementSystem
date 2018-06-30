package com.example.maask.tourmanagementsystem.NearbyFile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.maask.tourmanagementsystem.R;

import java.util.List;

/**
 * Created by Maask on 1/20/2018.
 */

public class NearbyShowingAdapter extends ArrayAdapter<NearbyResponse.Result> {

    private Context context;
    private List<NearbyResponse.Result>results;

    public NearbyShowingAdapter(@NonNull Context context, List<NearbyResponse.Result> results) {
        super(context, R.layout.single_nearby_places,results);
        this.context = context;
        this.results = results;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.single_nearby_places,parent,false);

        TextView place_name = convertView.findViewById(R.id.nearby_name);
        TextView place_address = convertView.findViewById(R.id.nearby_address);
        TextView place_rating = convertView.findViewById(R.id.nearby_rating);

        place_name.setText(results.get(position).getName());
        place_address.setText("Address : "+results.get(position).getVicinity());
        place_rating.setText("Rating : "+String.valueOf(results.get(position).getRating()));

        return convertView;
    }
}
