package com.zeeshan.coinbudget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.model.RecurringExpenses;

import java.util.List;

public class RecurringDetailAdapter extends RecyclerView.Adapter<RecurringDetailAdapter.MyViewHolder> {

    private List<RecurringExpenses> recurringExpensesList;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtDescRecurring, txtFrequencyRecurring, txtDateRecurring, txtAmountRecurring;
        CardView cardRecurringItem;

        MyViewHolder(View itemView) {
            super(itemView);

            txtAmountRecurring = itemView.findViewById(R.id.txtAmountRecurring);
            txtFrequencyRecurring = itemView.findViewById(R.id.txtFrequencyRecurring);
            txtDateRecurring = itemView.findViewById(R.id.txtDateRecurring);
            txtDescRecurring = itemView.findViewById(R.id.txtDescRecurring);
            cardRecurringItem = itemView.findViewById(R.id.cardViewRecurringDetail);
        }
    }

    public RecurringDetailAdapter(List<RecurringExpenses> recurringExpensesList) {
        this.recurringExpensesList = recurringExpensesList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recurring_expense_detail, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        RecurringExpenses recurringExpenses = recurringExpensesList.get(position);
        holder.txtAmountRecurring.setText(recurringExpenses.getExpenseAmount());
        holder.txtDateRecurring.setText(recurringExpenses.getDueDate());
        holder.txtDescRecurring.setText(recurringExpenses.getDescription());
        holder.txtFrequencyRecurring.setText(recurringExpenses.getFrequency());
    }

    @Override
    public int getItemCount() {
        return recurringExpensesList.size();
    }
}