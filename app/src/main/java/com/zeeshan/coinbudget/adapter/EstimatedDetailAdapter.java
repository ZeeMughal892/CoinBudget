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
import com.zeeshan.coinbudget.model.RecurringExpenses;

import java.util.List;

public class EstimatedDetailAdapter extends RecyclerView.Adapter<EstimatedDetailAdapter.MyViewHolder> {

    private List<EstimatedExpenses> estimatedExpensesList;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtDescEstimated, txtAmountEstimated;
        CardView cardEstimatedItem;

        MyViewHolder(View itemView) {
            super(itemView);

            txtDescEstimated = itemView.findViewById(R.id.txtDescEstimated);
            txtAmountEstimated = itemView.findViewById(R.id.txtAmountEstimated);
            cardEstimatedItem = itemView.findViewById(R.id.cardViewEstimatedDetail);
        }
    }

    public EstimatedDetailAdapter(List<EstimatedExpenses> estimatedExpensesList) {
        this.estimatedExpensesList = estimatedExpensesList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_estimated_expense_detail, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        EstimatedExpenses estimatedExpenses = estimatedExpensesList.get(position);

        holder.txtAmountEstimated.setText(estimatedExpenses.getExpenseAmount());
        holder.txtDescEstimated.setText(estimatedExpenses.getDescription());
    }

    @Override
    public int getItemCount() {
        return estimatedExpensesList.size();
    }
}