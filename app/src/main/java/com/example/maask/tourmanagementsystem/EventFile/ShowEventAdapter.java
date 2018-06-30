package com.example.maask.tourmanagementsystem.EventFile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.EventManagerActivity;
import com.example.maask.tourmanagementsystem.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Maask on 2/4/2018.
 */

public class ShowEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private OnDeleteIconClickListener deleteIconClickListener;
    private OnEditIconClickListener editIconClickListener;

    // delete interface
    public interface OnDeleteIconClickListener{
        void onDeleteClick(String parentName);
    }

    public void setOnDeleteIconClickListener(OnDeleteIconClickListener onDeleteIconClickListener){
        deleteIconClickListener = onDeleteIconClickListener;
    }

    // edit interface

    public interface OnEditIconClickListener{
        void onEditClick(String parentName);
    }

    public void setOnEditIconClickListener(OnEditIconClickListener onEditIconClickListener){
        editIconClickListener = onEditIconClickListener;
    }

    private ArrayList<UserEventInfo> userEventInfos;
    private Context context;

    public ShowEventAdapter(ArrayList<UserEventInfo> userEventInfos, Context context) {
        this.userEventInfos = userEventInfos;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder = null;
        View v = inflater.inflate(R.layout.single_event,parent,false);
        viewHolder = new ShowEventViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShowEventViewHolder eventViewHolder = (ShowEventViewHolder) holder;
        String currentData = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

        eventViewHolder.show_end_location.setText("Destination : "+userEventInfos.get(position).getEventDestination());
        eventViewHolder.show_start_location.setText("Start : "+userEventInfos.get(position).getEventStartLocation());
        eventViewHolder.show_departure_date.setText("Departure Data : "+userEventInfos.get(position).getDepartureDate());
        eventViewHolder.show_created_date.setText("Event Created Data : "+userEventInfos.get(position).getEventCreateData());
        eventViewHolder.show_event_name.setText("Event Name : "+userEventInfos.get(position).getEventName());
        eventViewHolder.show_event_budget.setText("Budget : "+userEventInfos.get(position).getEventBudget() + " TK");

        try {

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date recentDate = format.parse(currentData);
            Date departureDate  = format.parse(userEventInfos.get(position).getDepartureDate());

            long diff = departureDate.getTime() - recentDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays == 0){
                eventViewHolder.left_days.setText("Enjoy the tour");
            }else if (diffDays < 0){
                eventViewHolder.left_days.setText("Passed away");
            }else {
                eventViewHolder.left_days.setText(String.valueOf(diffDays)+ " Days Left");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return userEventInfos.size();
    }

    private class ShowEventViewHolder extends RecyclerView.ViewHolder {

        TextView show_end_location,show_start_location,show_departure_date,
                show_created_date,show_event_name,show_event_budget,left_days;
        ImageView enter_this_event,edit_event,delete_event;

        public ShowEventViewHolder(View v) {
            super(v);

            show_end_location = itemView.findViewById(R.id.show_end_location);
            show_start_location = itemView.findViewById(R.id.show_start_location);
            show_departure_date = itemView.findViewById(R.id.show_departure_date);
            show_created_date = itemView.findViewById(R.id.show_created_date);
            show_event_name = itemView.findViewById(R.id.show_event_name);
            show_event_budget = itemView.findViewById(R.id.show_event_budget);
            left_days = itemView.findViewById(R.id.left_days);
            enter_this_event = itemView.findViewById(R.id.enter_this_event);
            edit_event = itemView.findViewById(R.id.edit_event);
            delete_event = itemView.findViewById(R.id.delete_event);

            enter_this_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    String parentName = userEventInfos.get(pos).getParentName();

                    Intent intent = new Intent(context, EventManagerActivity.class);
                    intent.putExtra("parent_name",parentName);
                    context.startActivity(intent);
                }
            });

            edit_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editIconClickListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            editIconClickListener.onEditClick(userEventInfos.get(position).getParentName());
                        }
                    }

                }
            });

            delete_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (deleteIconClickListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            deleteIconClickListener.onDeleteClick(userEventInfos.get(position).getParentName());
                        }
                    }
                }
            });

        }
    }
}
