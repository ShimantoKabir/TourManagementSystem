package com.example.maask.tourmanagementsystem.EventFile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maask.tourmanagementsystem.R;

import java.util.ArrayList;

/**
 * Created by Maask on 2/7/2018.
 */

public class ShowExpenseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // delete ka locha ...
    private OnDeleteIconClickListener deleteIconClickListener;

    public interface OnDeleteIconClickListener{
        void onDeleteClick(String expenseParentName);
    }

    public void setOnDeleteIconClickListener(OnDeleteIconClickListener onDeleteIconClickListener){
        deleteIconClickListener = onDeleteIconClickListener;
    }

    // edit ka locha ...
    private OnEditIconClickListener editIconClickListener;

    public interface OnEditIconClickListener{
        void onEditClick(String expenseParentName);
    }

    public void setOnEditIconClickListener(OnEditIconClickListener onEditIconClickListener){
        editIconClickListener = onEditIconClickListener;
    }

    private ArrayList<ExpenseInfo> expenseInfos;
    private Context context;

    public ShowExpenseAdapter(ArrayList<ExpenseInfo> expenseInfos, Context context) {
        this.expenseInfos = expenseInfos;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder = null;
        View v = inflater.inflate(R.layout.single_expense_history,parent,false);
        viewHolder = new ShowExpenseViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ShowExpenseViewHolder eventViewHolder = (ShowExpenseViewHolder) holder;

        eventViewHolder.ex_title.setText("Title : "+expenseInfos.get(position).getExpenseTitle());
        eventViewHolder.ex_amount.setText("Amount : "+expenseInfos.get(position).getExpenseAmount()+" TK");
        eventViewHolder.ex_create.setText("Date : "+expenseInfos.get(position).getCreatedDate());

    }

    @Override
    public int getItemCount() {
        return expenseInfos.size();
    }

    private class ShowExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView ex_title,ex_amount,ex_create;
        ImageView edit_expense,delete_expense;

        public ShowExpenseViewHolder(View v) {
            super(v);
            ex_title = itemView.findViewById(R.id.ex_title);
            ex_amount = itemView.findViewById(R.id.ex_amount);
            edit_expense = itemView.findViewById(R.id.edit_expense);
            delete_expense = itemView.findViewById(R.id.delete_expense);
            ex_create = itemView.findViewById(R.id.ex_create);

            delete_expense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (deleteIconClickListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            deleteIconClickListener.onDeleteClick(expenseInfos.get(position).getExpenseParentName());
                        }
                    }
                }
            });

            edit_expense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editIconClickListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            editIconClickListener.onEditClick(expenseInfos.get(position).getExpenseParentName());
                        }
                    }
                }
            });

        }
    }
}
