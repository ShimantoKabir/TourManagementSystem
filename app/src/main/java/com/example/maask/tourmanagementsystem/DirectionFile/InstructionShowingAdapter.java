package com.example.maask.tourmanagementsystem.DirectionFile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.maask.tourmanagementsystem.R;

import java.util.ArrayList;

/**
 * Created by Maask on 1/30/2018.
 */

public class InstructionShowingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> instructionList;

    public InstructionShowingAdapter(ArrayList<String> instructionList) {
        this.instructionList = instructionList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        View v = inflater.inflate(R.layout.single_instruction,parent,false);
        viewHolder = new InstructionViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        InstructionViewHolder instructionViewHolder = (InstructionViewHolder) holder;
        instructionViewHolder.show_instruction.setText(instructionList.get(position));

    }

    private class InstructionViewHolder extends RecyclerView.ViewHolder {
        public TextView show_instruction;
        public InstructionViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            show_instruction = itemLayoutView.findViewById(R.id.ins);
        }
    }

    @Override
    public int getItemCount() {
        return instructionList.size();
    }
}

