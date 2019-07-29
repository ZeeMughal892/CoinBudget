package com.zeeshan.coinbudget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.model.Budget;
import com.zeeshan.coinbudget.model.BudgetModel;
import com.zeeshan.coinbudget.model.ExpenseOverview;

import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.MyViewHolder> {

    private List<Budget> budgetList;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtExpenseName, txtDueDate, txtExpense, txtIncome, txtBalance;

        MyViewHolder(View itemView) {
            super(itemView);
            txtExpenseName = itemView.findViewById(R.id.txtExpenseName);
            txtDueDate = itemView.findViewById(R.id.txtDueDate);
            txtExpense = itemView.findViewById(R.id.txtExpenseAmount);
            txtIncome = itemView.findViewById(R.id.txtIncome);
            txtBalance = itemView.findViewById(R.id.txtBalance);
        }
    }

    public BudgetAdapter(List<Budget> budgetList) {
        this.budgetList = budgetList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_budget_summary, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Budget budget = budgetList.get(position);

        holder.txtExpenseName.setText(budget.getExpenseName().get(position));
        holder.txtDueDate.setText(budget.getDueDate().get(position));
        holder.txtExpense.setText(budget.getExpense().get(position));
        holder.txtIncome.setText(budget.getIncome().get(position));

    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }
}
