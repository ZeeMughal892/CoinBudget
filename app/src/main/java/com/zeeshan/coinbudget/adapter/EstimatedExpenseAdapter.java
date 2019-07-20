package com.zeeshan.coinbudget.adapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.model.EstimatedExpenses;
import com.zeeshan.coinbudget.model.Lookup;
import com.zeeshan.coinbudget.model.RecurringExpenses;

import java.util.List;

public class EstimatedExpenseAdapter extends RecyclerView.Adapter<EstimatedExpenseAdapter.MyViewHolder> {

    private List<Lookup> lookupList;
    private Dialog dialogEstimated;
    private TextView txtLookup;
    private EditText ed_AmountEstimated, ed_DescriptionEstimated;
    private Button btnAddEstimated;
    private ImageView imgIconDialog;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String mode;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtLookupItemName;
        ImageView imgIcon;
        CardView cardViewLookup;

        MyViewHolder(View itemView) {
            super(itemView);

            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtLookupItemName = itemView.findViewById(R.id.txtLookupItemName);
            cardViewLookup = itemView.findViewById(R.id.cardViewLookup);
        }
    }

    public EstimatedExpenseAdapter(List<Lookup> lookupList, String mode) {
        this.lookupList = lookupList;
        this.mode = mode;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_lookup_item, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(mode);

        dialogEstimated = new Dialog(itemView.getContext());
        dialogEstimated.setContentView(R.layout.dialog_estimated);
        dialogEstimated.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        myViewHolder.cardViewLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtLookup = dialogEstimated.findViewById(R.id.txtLookup);

                ed_AmountEstimated = dialogEstimated.findViewById(R.id.ed_EstimatedAmount);
                ed_DescriptionEstimated = dialogEstimated.findViewById(R.id.ed_descEstimated);
                imgIconDialog = dialogEstimated.findViewById(R.id.imgLogo);
                btnAddEstimated = dialogEstimated.findViewById(R.id.btnAddEstimated);

                ed_DescriptionEstimated.setText(lookupList.get(myViewHolder.getAdapterPosition()).getLookUpItemName());

                btnAddEstimated.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String expenseId = databaseReference.push().getKey();
                        String userID = firebaseUser.getUid();
                        String source = txtLookup.getText().toString();
                        String amount = ed_AmountEstimated.getText().toString().trim();
                        String description = ed_DescriptionEstimated.getText().toString().trim();
                        if (TextUtils.isEmpty(amount)) {
                            Toast.makeText(view.getContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(description)) {
                            Toast.makeText(view.getContext(), "Please enter description", Toast.LENGTH_SHORT).show();
                        } else {
                            EstimatedExpenses estimatedExpenses = new EstimatedExpenses(expenseId, userID, source, amount, description);
                            databaseReference.child(expenseId).setValue(estimatedExpenses);
                            Toast.makeText(view.getContext(), "Expense Added Successfully", Toast.LENGTH_SHORT).show();
                            ed_AmountEstimated.setText(null);
                            dialogEstimated.dismiss();
                        }
                    }
                });

                txtLookup.setText(lookupList.get(myViewHolder.getAdapterPosition()).

                        getLookUpItemName());

                Picasso.get()
                        .load(lookupList.get(myViewHolder.getAdapterPosition()). getItemImage())
                        . fit()
                        . centerInside()
                        . placeholder(R.drawable.ic_coinbudget)
                        . into(imgIconDialog);
                dialogEstimated.show();
            }
        });


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Lookup lookup = lookupList.get(position);
        holder.txtLookupItemName.setText(lookup.getLookUpItemName());

        Picasso.get()
                .load(lookup.getItemImage())
                .fit()
                .centerInside()
                .placeholder(R.drawable.ic_coinbudget)
                .into(holder.imgIcon);
    }

    @Override
    public int getItemCount() {
        return lookupList.size();
    }
}