package com.zeeshan.coinbudget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.model.EstimatedExpenses;
import com.zeeshan.coinbudget.model.Savings;

import java.util.List;

public class SavingAdapter extends RecyclerView.Adapter<SavingAdapter.MyViewHolder> {

    private List<Savings> savingsList;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtGoalTitle,txtGoalDate,txtGoalAmount;

        MyViewHolder(View itemView) {
            super(itemView);

            txtGoalTitle = itemView.findViewById(R.id.txtGoalTitle);
            txtGoalDate = itemView.findViewById(R.id.txtGoalDate);
            txtGoalAmount = itemView.findViewById(R.id.txtGoalAmount);

        }
    }

    public SavingAdapter(List<Savings> savingsList) {
        this.savingsList = savingsList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_saving_detail, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Savings savings = savingsList.get(position);

       holder.txtGoalAmount.setText(savings.getAmountToSave());
       holder.txtGoalDate.setText(savings.getGoalDate());
       holder.txtGoalTitle.setText(savings.getSavingGoalTitle());
    }

    @Override
    public int getItemCount() {
        return savingsList.size();
    }
}