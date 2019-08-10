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
import com.zeeshan.coinbudget.model.ExtraIncome;
import com.zeeshan.coinbudget.model.Lookup;
import com.zeeshan.coinbudget.model.Transactions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class LookupAdapter extends RecyclerView.Adapter<LookupAdapter.MyViewHolder> {

    private List<Lookup> lookupList;
    private Dialog dialog;
    TextView txtLookup;
    DatePickerDialog datePickerDialog;
    EditText ed_Amount, ed_Notes, ed_Date;
    Button btnAdd, btnToday, btnYesterday, btnOtherDay;
    ImageView imgIconDialog;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String mode;

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

    public LookupAdapter(List<Lookup> lookupList, String mode) {
        this.lookupList = lookupList;
        this.mode = mode;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_lookup_item, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(mode);

        dialog = new Dialog(itemView.getContext());
        dialog.setContentView(R.layout.dialog_extra_income);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        myViewHolder.cardViewLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtLookup = dialog.findViewById(R.id.txtLookup);

                ed_Amount = dialog.findViewById(R.id.ed_Amount);
                ed_Notes = dialog.findViewById(R.id.ed_Notes);
                ed_Date = dialog.findViewById(R.id.ed_Date);

                datePickerDialog = new DatePickerDialog(itemView.getContext());
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String Date = month+1 + "/" + day + "/" + year;
                        ed_Date.setText(Date);
                    }
                });

                btnAdd = dialog.findViewById(R.id.btnAdd);
                btnToday = dialog.findViewById(R.id.btnToday);
                btnYesterday = dialog.findViewById(R.id.btnYesterday);
                btnOtherDay = dialog.findViewById(R.id.btnOtherDate);

                imgIconDialog = dialog.findViewById(R.id.imgLogo);

                btnOtherDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        datePickerDialog.show();
                    }
                });
                btnToday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, 0);
                        ed_Date.setText((dateFormat.format(cal.getTime())));
                    }
                });
                btnYesterday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -1);
                        ed_Date.setText((dateFormat.format(cal.getTime())));
                    }
                });
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String modeID = databaseReference.push().getKey();
                        String userID = firebaseUser.getUid();
                        String source = txtLookup.getText().toString();
                        String amount = ed_Amount.getText().toString().trim();
                        String notes = ed_Notes.getText().toString().trim();
                        String date = ed_Date.getText().toString().trim();

                        if (TextUtils.isEmpty(amount)) {
                            Toast.makeText(view.getContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(notes)) {
                            Toast.makeText(view.getContext(), "Please enter notes", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(date)) {
                            Toast.makeText(view.getContext(), "Please select date", Toast.LENGTH_SHORT).show();
                        } else {
                            if (mode.equals("Extra Income")) {
                                ExtraIncome extraIncome = new ExtraIncome(modeID, userID, source, amount, notes, false, date);
                                databaseReference.child(modeID).setValue(extraIncome);
                            } else {
                                Transactions transactions = new Transactions(modeID, userID, source, amount, notes, false, date);
                                databaseReference.child(modeID).setValue(transactions);
                            }
                            Toast.makeText(view.getContext(), "Information Added Successfully", Toast.LENGTH_SHORT).show();
                            ed_Amount.setText(null);
                            ed_Date.setText(null);
                            ed_Notes.setText(null);
                            dialog.dismiss();
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
                dialog.show();
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