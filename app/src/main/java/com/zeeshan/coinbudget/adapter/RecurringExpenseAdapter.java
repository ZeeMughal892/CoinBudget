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
import com.zeeshan.coinbudget.EstimatedExpenses;
import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.model.Lookup;
import com.zeeshan.coinbudget.model.RecurringExpenses;


import java.util.Calendar;
import java.util.List;

public class RecurringExpenseAdapter extends RecyclerView.Adapter<RecurringExpenseAdapter.MyViewHolder> {

    private List<Lookup> lookupList;
    private Dialog dialogRecurring;
    private TextView txtLookup;
    private DatePickerDialog datePickerDialog;
    private EditText ed_AmountRecurring, ed_DateRecurring, ed_Description;
    private Button btnAddRecurring, btnDateRecurring;
    DatePickerDialog.OnDateSetListener dateSetListener;
    private ImageView imgIconDialog;
    private DatabaseReference databaseReference;
    private Spinner spinnerRecurring;
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

    public RecurringExpenseAdapter(List<Lookup> lookupList, String mode) {
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

        dialogRecurring = new Dialog(itemView.getContext());
        dialogRecurring.setContentView(R.layout.dialog_recurring);
        dialogRecurring.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        myViewHolder.cardViewLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtLookup = dialogRecurring.findViewById(R.id.txtLookup);

                ed_AmountRecurring = dialogRecurring.findViewById(R.id.ed_RecurringAmount);
                spinnerRecurring = dialogRecurring.findViewById(R.id.spinnerRecurring);
                ed_Description = dialogRecurring.findViewById(R.id.ed_descRecurring);
                ed_DateRecurring = dialogRecurring.findViewById(R.id.ed_DateRecurring);
                imgIconDialog = dialogRecurring.findViewById(R.id.imgLogo);
                btnDateRecurring = dialogRecurring.findViewById(R.id.btnSelectDateRecurring);
                btnAddRecurring = dialogRecurring.findViewById(R.id.btnAddRecurring);

                btnDateRecurring.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        datePickerDialog = new DatePickerDialog(itemView.getContext(), dateSetListener, year, month, day);
                        datePickerDialog.show();
                    }
                });
                dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String Date = (month + 1) + "/" + day + "/" + year;
                        ed_DateRecurring.setText(Date);
                    }
                };


                ed_Description.setText(lookupList.get(myViewHolder.getAdapterPosition()).getLookUpItemName());

                btnAddRecurring.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String expenseId = databaseReference.push().getKey();
                        String userID = firebaseUser.getUid();
                        String source = txtLookup.getText().toString();
                        String amount = ed_AmountRecurring.getText().toString().trim();
                        String description = ed_Description.getText().toString().trim();
                        String frequency = spinnerRecurring.getSelectedItem().toString().trim();
                        String date = ed_DateRecurring.getText().toString().trim();
                        if (TextUtils.isEmpty(amount)) {
                            Toast.makeText(view.getContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(description)) {
                            Toast.makeText(view.getContext(), "Please enter description", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(date)) {
                            Toast.makeText(view.getContext(), "Please select date", Toast.LENGTH_SHORT).show();
                        } else {
                            RecurringExpenses recurringExpenses = new RecurringExpenses(expenseId, userID, source, amount, frequency, date, description);
                            databaseReference.child(expenseId).setValue(recurringExpenses);
                            Toast.makeText(view.getContext(), "Expense Added Successfully", Toast.LENGTH_SHORT).show();
                            ed_AmountRecurring.setText(null);
                            dialogRecurring.dismiss();
                        }
                    }
                });

                txtLookup.setText(lookupList.get(myViewHolder.getAdapterPosition()).getLookUpItemName());

                Picasso.get()
                        .load(lookupList.get(myViewHolder.getAdapterPosition()).getItemImage())
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.ic_coinbudget)
                        .into(imgIconDialog);
                dialogRecurring.show();
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