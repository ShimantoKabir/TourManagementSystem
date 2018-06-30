package com.example.maask.tourmanagementsystem.EventFile;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Maask on 2/9/2018.
 */

public class ShowMomentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // delete ka locha ...
    private OnDeleteIconClickListener deleteIconClickListener;

    public interface OnDeleteIconClickListener{
        void onDeleteClick(String momentParentName);
    }

    public void setOnDeleteIconClickListener(OnDeleteIconClickListener onDeleteIconClickListener){
        deleteIconClickListener = onDeleteIconClickListener;
    }

    // download ka locha ...
    private OnDownloadIconClickListener downloadIconClickListener;

    public interface OnDownloadIconClickListener{
        void onDownloadClick(String momentParentName);
    }

    public void setOnDownloadIconClickListener(OnDownloadIconClickListener onDownloadIconClickListener){
        downloadIconClickListener = onDownloadIconClickListener;
    }

    private ArrayList<ImageInfo> imageInfos;
    private Context context;

    public ShowMomentsAdapter(ArrayList<ImageInfo> imageInfos, Context context) {
        this.imageInfos = imageInfos;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder = null;
        View v = inflater.inflate(R.layout.single_moment,parent,false);
        viewHolder = new ShowMomentsViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ShowMomentsViewHolder showMomentsViewHolder = (ShowMomentsViewHolder) holder;

        String getCaptureDate = imageInfos.get(position).getCaptureDate();
        String[] parts = getCaptureDate.split("_");

        String setCaptureDate ="Capture Date : "+parts[0]+"/"+parts[1]+"/"+parts[2]+" "+parts[3]+":"+parts[4]+":"+parts[5]+" "+parts[6];

        showMomentsViewHolder.moment_capture_data.setText(setCaptureDate);
        Picasso.with(context)
        .load(Uri.parse(imageInfos.get(position).getImageUrl()))
        .fit()
        .networkPolicy(NetworkPolicy.OFFLINE)
        .into(showMomentsViewHolder.moment_img, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context)
                .load(Uri.parse(imageInfos.get(position).getImageUrl()))
                .fit()
                .into(showMomentsViewHolder.moment_img);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageInfos.size();
    }

    private class ShowMomentsViewHolder extends RecyclerView.ViewHolder {

        TextView moment_capture_data;
        ImageView moment_img,moment_delete,moment_download;

        public ShowMomentsViewHolder(View v) {
            super(v);

            moment_capture_data = itemView.findViewById(R.id.moment_capture_data);
            moment_img = itemView.findViewById(R.id.moment_img);
            moment_delete = itemView.findViewById(R.id.moment_delete);
            moment_download = itemView.findViewById(R.id.moment_download);

            moment_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (deleteIconClickListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            deleteIconClickListener.onDeleteClick(imageInfos.get(position).getCaptureDate());
                        }
                    }
                }
            });

            moment_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (downloadIconClickListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            downloadIconClickListener.onDownloadClick(imageInfos.get(position).getCaptureDate());
                        }
                    }
                }
            });

        }
    }

}
