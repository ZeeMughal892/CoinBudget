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
import com.zeeshan.coinbudget.IncomeDetails;
import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.model.Income;
import com.zeeshan.coinbudget.model.Savings;
import com.zeeshan.coinbudget.model.User;

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> recyclerItems;
    String userCurrency;
    private static final int ITEM_ENTRY = 0;
    private static final int ITEM_BANNER_AD = 1;


    public IncomeAdapter(List<Object> recyclerItems, String userCurrency) {
        this.recyclerItems = recyclerItems;
        this.userCurrency = userCurrency;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ITEM_ENTRY:
                final View entryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_income_detail, viewGroup, false);
                return new IncomeAdapter.IncomeViewHolder(entryView);
            case ITEM_BANNER_AD:

            default:
                View bannerAdView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.banner_ad_container, viewGroup, false);
                return new IncomeAdapter.BannerAdViewHolder(bannerAdView);
        }
    }
        @Override
        public void onBindViewHolder (@NonNull RecyclerView.ViewHolder viewHolder,int position){
            int viewType = getItemViewType(position);
            switch (viewType) {
                case ITEM_ENTRY:
                    IncomeAdapter.IncomeViewHolder incomeViewHolder = (IncomeAdapter.IncomeViewHolder) viewHolder;
                    Income income = (Income) recyclerItems.get(position);
                    String incomeAmount = userCurrency+" " + income.getIncomeAmount();
                    incomeViewHolder.txtIncomeDate.setText(income.getDateOfIncome());
                    incomeViewHolder.txtIncomeFrequency.setText(income.getFrequency());
                    incomeViewHolder.txtIncomeAmount.setText(incomeAmount);
                    incomeViewHolder.txtIncomeDesc.setText(income.getDescription());
                    break;
                case ITEM_BANNER_AD:
                default:
                    IncomeAdapter.BannerAdViewHolder adViewHolder = (IncomeAdapter.BannerAdViewHolder) viewHolder;
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
        public int getItemCount () {
            return recyclerItems.size();
        }

        @Override
        public int getItemViewType ( int position){
            if (position % IncomeDetails.ITEMS_PER_AD == 0) {
                return ITEM_BANNER_AD;
            } else {
                return ITEM_ENTRY;
            }
        }


    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView txtIncomeDate, txtIncomeFrequency, txtIncomeAmount, txtIncomeDesc;

        public IncomeViewHolder(View itemView) {
            super(itemView);

            txtIncomeDate = itemView.findViewById(R.id.txtIncomeDate);
            txtIncomeFrequency = itemView.findViewById(R.id.txtIncomeFrequency);
            txtIncomeAmount = itemView.findViewById(R.id.txtIncomeAmount);
            txtIncomeDesc = itemView.findViewById(R.id.txtIncomeDesc);

        }
    }

    public static class BannerAdViewHolder extends RecyclerView.ViewHolder {

        BannerAdViewHolder(View itemView) {
            super(itemView);
        }
    }
}
