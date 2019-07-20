package com.zeeshan.coinbudget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.model.ExtraIncome;
import com.zeeshan.coinbudget.model.Lookup;

import java.util.List;

public class ExtraIncomeAdapter extends RecyclerView.Adapter<ExtraIncomeAdapter.MyViewHolder> {

    private List<ExtraIncome> extraIncomeList;

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

    public ExtraIncomeAdapter(List<ExtraIncome> extraIncomeList) {
        this.extraIncomeList = extraIncomeList;

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
        ExtraIncome extraIncome = extraIncomeList.get(position);

        holder.txtEntry.setText(extraIncome.getIncomeSource());
        holder.txtNotes.setText(extraIncome.getNotes());
        holder.txtAmount.setText(extraIncome.getExtraAmount());
        holder.txtDate.setText(extraIncome.getIncomeDay());
    }

    @Override
    public int getItemCount() {
        return extraIncomeList.size();
    }
}