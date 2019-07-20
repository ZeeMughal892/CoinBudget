package com.zeeshan.coinbudget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.Transaction;
import com.zeeshan.coinbudget.model.ExtraIncome;
import com.zeeshan.coinbudget.model.Transactions;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    private List<Transactions> transactionList;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtAmount, txtNotes,txtDate,txtEntry;


        MyViewHolder(View itemView) {
            super(itemView);

            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtNotes = itemView.findViewById(R.id.txtNotes);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtEntry = itemView.findViewById(R.id.txtEntry);
        }
    }

    public TransactionAdapter(List<Transactions> transactionList) {
        this.transactionList = transactionList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_daily_entry_detail, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Transactions transaction = transactionList.get(position);

        holder.txtEntry.setText(transaction.getTransactionName());
        holder.txtNotes.setText(transaction.getNotes());
        holder.txtAmount.setText(transaction.getTransactionAmount());
        holder.txtDate.setText(transaction.getTransactionDay());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}