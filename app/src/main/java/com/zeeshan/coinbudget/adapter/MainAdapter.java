package com.zeeshan.coinbudget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.model.ExpenseOverview;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    private List<ExpenseOverview> expenseOverviewList;


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtRemainingBudget, txtRemainingDays, txtAverageBudget;

        MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            txtRemainingBudget = itemView.findViewById(R.id.txtRemainingBudget);
            txtAverageBudget = itemView.findViewById(R.id.txtAverageBudget);
            txtRemainingDays = itemView.findViewById(R.id.txtRemainingDays);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public MainAdapter(List<ExpenseOverview> expenseOverviewList) {
        this.expenseOverviewList = expenseOverviewList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_main, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ExpenseOverview expenseOverview = expenseOverviewList.get(position);
        holder.txtRemainingBudget.setText(expenseOverview.getRemainingAmount());
        holder.txtRemainingDays.setText(expenseOverview.getRemainingDays());
        holder.txtAverageBudget.setText(expenseOverview.getAverageExpenseAmount());

    }

    @Override
    public int getItemCount() {
        return expenseOverviewList.size();
    }
}