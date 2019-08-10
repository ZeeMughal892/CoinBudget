package com.zeeshan.coinbudget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.zeeshan.coinbudget.RecurringExpensesDetails;
import com.zeeshan.coinbudget.model.EstimatedExpenses;
import com.zeeshan.coinbudget.model.RecurringExpenses;
import com.zeeshan.coinbudget.model.User;

import java.util.List;

public class RecurringDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> recyclerItems;
    private static final int ITEM_ENTRY = 0;
    private static final int ITEM_BANNER_AD = 1;

    String userCurrency;

    public RecurringDetailAdapter(List<Object> recyclerItems,String userCurrency) {
        this.recyclerItems = recyclerItems;
        this.userCurrency = userCurrency;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ITEM_ENTRY:
                final View entryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recurring_expense_detail, viewGroup, false);
                return new RecurringDetailAdapter.RecurringExpenseViewHolder(entryView);
            case ITEM_BANNER_AD:

            default:
                View bannerAdView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.banner_ad_container, viewGroup, false);
                return new RecurringDetailAdapter.BannerAdViewHolder(bannerAdView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_ENTRY:
                RecurringDetailAdapter.RecurringExpenseViewHolder recurringExpenseViewHolder = (RecurringDetailAdapter.RecurringExpenseViewHolder) viewHolder;
                RecurringExpenses recurringExpenses = (RecurringExpenses) recyclerItems.get(position);
                String recurringAmount = userCurrency+" " + recurringExpenses.getExpenseAmount();
                recurringExpenseViewHolder.txtAmountRecurring.setText(recurringAmount);
                recurringExpenseViewHolder.txtDateRecurring.setText(recurringExpenses.getDueDate());
                recurringExpenseViewHolder.txtDescRecurring.setText(recurringExpenses.getDescription());
                recurringExpenseViewHolder.txtFrequencyRecurring.setText(recurringExpenses.getFrequency());
                break;
            case ITEM_BANNER_AD:
            default:
                RecurringDetailAdapter.BannerAdViewHolder adViewHolder = (RecurringDetailAdapter.BannerAdViewHolder) viewHolder;
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
        if (position % RecurringExpensesDetails.ITEMS_PER_AD == 0) {
            return ITEM_BANNER_AD;
        } else {
            return ITEM_ENTRY;
        }
    }

    public static class RecurringExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView txtDescRecurring, txtFrequencyRecurring, txtDateRecurring, txtAmountRecurring;
        CardView cardRecurringItem;

        public RecurringExpenseViewHolder(View itemView) {
            super(itemView);

            txtAmountRecurring = itemView.findViewById(R.id.txtAmountRecurring);
            txtFrequencyRecurring = itemView.findViewById(R.id.txtFrequencyRecurring);
            txtDateRecurring = itemView.findViewById(R.id.txtDateRecurring);
            txtDescRecurring = itemView.findViewById(R.id.txtDescRecurring);
            cardRecurringItem = itemView.findViewById(R.id.cardViewRecurringDetail);

        }
    }

    public static class BannerAdViewHolder extends RecyclerView.ViewHolder {

        BannerAdViewHolder(View itemView) {
            super(itemView);
        }
    }
}








