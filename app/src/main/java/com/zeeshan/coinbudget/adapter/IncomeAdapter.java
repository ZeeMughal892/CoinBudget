package com.zeeshan.coinbudget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.model.Income;
import com.zeeshan.coinbudget.model.Savings;

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.MyViewHolder> {

    private List<Income> incomeList;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtIncomeDate,txtIncomeFrequency,txtIncomeAmount,txtIncomeDesc;

        MyViewHolder(View itemView) {
            super(itemView);

            txtIncomeDate = itemView.findViewById(R.id.txtIncomeDate);
            txtIncomeFrequency = itemView.findViewById(R.id.txtIncomeFrequency);
            txtIncomeAmount = itemView.findViewById(R.id.txtIncomeAmount);
            txtIncomeDesc = itemView.findViewById(R.id.txtIncomeDesc);
        }
    }

    public IncomeAdapter(List<Income> incomeList) {
        this.incomeList = incomeList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_income_detail, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Income income = incomeList.get(position);

        holder.txtIncomeDate.setText(income.getDateOfIncome());
        holder.txtIncomeFrequency.setText(income.getFrequency());
        holder.txtIncomeAmount.setText(income.getIncomeAmount());
        holder.txtIncomeDesc.setText(income.getDescription());
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }
}