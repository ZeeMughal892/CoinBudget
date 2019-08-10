package com.zeeshan.coinbudget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeshan.coinbudget.DailyEntryDetail;
import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.Transaction;
import com.zeeshan.coinbudget.model.ExtraIncome;
import com.zeeshan.coinbudget.model.Transactions;
import com.zeeshan.coinbudget.model.User;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Object> recyclerItems;
    private static final int ITEM_ENTRY = 0;
    private static final int ITEM_BANNER_AD = 1;

    String userCurrency;

    public TransactionAdapter(List<Object> recyclerItems,String userCurrency) {
        this.recyclerItems = recyclerItems;
        this.userCurrency = userCurrency;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ITEM_ENTRY:
                final View entryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_daily_entry_detail, viewGroup, false);
                return new TransactionAdapter.TransactionViewHolder(entryView);
            case ITEM_BANNER_AD:

            default:
                View bannerAdView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.banner_ad_container, viewGroup, false);
                return new TransactionAdapter.BannerAdViewHolder(bannerAdView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_ENTRY:
                TransactionAdapter.TransactionViewHolder transactionViewHolder = (TransactionAdapter.TransactionViewHolder) viewHolder;
                Transactions transactions = (Transactions) recyclerItems.get(position);
                String amount = userCurrency+" " + transactions.getTransactionAmount();
                transactionViewHolder.txtEntry.setText(transactions.getTransactionName());
                transactionViewHolder.txtNotes.setText(transactions.getNotes());
                transactionViewHolder.txtAmount.setText(amount);
                transactionViewHolder.txtDate.setText(transactions.getTransactionDay());
                break;
            case ITEM_BANNER_AD:
            default:
                TransactionAdapter.BannerAdViewHolder adViewHolder = (TransactionAdapter.BannerAdViewHolder) viewHolder;
                AdView adView = (AdView) recyclerItems.get(position);
                ViewGroup adCardView = (ViewGroup) adViewHolder.itemView;
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adCardView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }
                adCardView.addView(adView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position % DailyEntryDetail.ITEMS_PER_AD == 0) {
            return ITEM_BANNER_AD;
        } else {
            return ITEM_ENTRY;
        }
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView txtAmount, txtNotes, txtDate, txtEntry;

        public TransactionViewHolder(View itemView) {
            super(itemView);

            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtNotes = itemView.findViewById(R.id.txtNotes);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtEntry = itemView.findViewById(R.id.txtEntry);

        }
    }

    public static class BannerAdViewHolder extends RecyclerView.ViewHolder {

        BannerAdViewHolder(View itemView) {
            super(itemView);
        }
    }
}