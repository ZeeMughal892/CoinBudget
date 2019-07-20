package com.zeeshan.coinbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeshan.coinbudget.adapter.ExtraIncomeAdapter;
import com.zeeshan.coinbudget.adapter.TransactionAdapter;
import com.zeeshan.coinbudget.model.ExtraIncome;
import com.zeeshan.coinbudget.model.Transactions;

import java.util.ArrayList;
import java.util.List;

public class DailyEntryDetail extends AppCompatActivity {
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    String LookupName;
    TextView txtTotalLabel, txtTotalAmount;
    DatabaseReference databaseExtraIncome, databaseTransaction;
    RecyclerView recyclerViewDailyEntry;
    ExtraIncomeAdapter extraIncomeAdapter;
    TransactionAdapter transactionAdapter;
    List<ExtraIncome> extraIncomeList;
    List<Transactions> transactionList;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ProgressBar progressBarDailyEntry;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_entry_detail);
        init();
        setUpToolbar();
        Intent intent = getIntent();
        LookupName = intent.getStringExtra("LookupName");
        String message;
        if (LookupName.equals("Extra Income")) {
            message = "Total Extra Income = ";
            txtTotalLabel.setText(message);
            loadExtraIncomeEntries();
        } else {
            message = "Total Transaction Amount = ";
            txtTotalLabel.setText(message);
            loadTransactionEntries();
        }

        bottomNavigationView.setSelectedItemId(R.id.budget);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bank:
                        startActivity(new Intent(getApplicationContext(), Bank.class));
                        break;
                    case R.id.budget:
                        startActivity(new Intent(getApplicationContext(), DailyEntryDetail.class));
                        break;
                    case R.id.income:
                        startActivity(new Intent(getApplicationContext(), EstimatedExpensesDetails.class));
                        break;
                    case R.id.expenses:
                        startActivity(new Intent(getApplicationContext(), RecurringExpensesDetails.class));
                        break;
                    case R.id.savings:
                        startActivity(new Intent(getApplicationContext(), SavingDetails.class));
                        break;
                }
                return true;
            }
        });
    }

    private void loadExtraIncomeEntries() {
        databaseExtraIncome.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                extraIncomeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.zeeshan.coinbudget.model.ExtraIncome extraIncome = snapshot.getValue(com.zeeshan.coinbudget.model.ExtraIncome.class);
                    if (extraIncome.getUserID().equals(firebaseUser.getUid())) {
                        extraIncomeList.add(extraIncome);

                    }
                }
                extraIncomeAdapter = new ExtraIncomeAdapter(extraIncomeList);
                recyclerViewDailyEntry.setHasFixedSize(true);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewDailyEntry.setLayoutManager(mLayoutManager);
                recyclerViewDailyEntry.setItemAnimator(new DefaultItemAnimator());
                recyclerViewDailyEntry.setAdapter(extraIncomeAdapter);

                for (ExtraIncome extraIncome : extraIncomeList) {
                    total += (Integer.parseInt(extraIncome.getExtraAmount()));
                    txtTotalAmount.setText(String.valueOf(total));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DailyEntryDetail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadTransactionEntries() {
        databaseTransaction.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.zeeshan.coinbudget.model.Transactions transactions = snapshot.getValue(com.zeeshan.coinbudget.model.Transactions.class);
                    if (transactions.getUserID().equals(firebaseUser.getUid())) {
                        transactionList.add(transactions);

                    }
                }
                transactionAdapter = new TransactionAdapter(transactionList);
                recyclerViewDailyEntry.setHasFixedSize(true);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewDailyEntry.setLayoutManager(mLayoutManager);
                recyclerViewDailyEntry.setItemAnimator(new DefaultItemAnimator());
                recyclerViewDailyEntry.setAdapter(transactionAdapter);

                for (Transactions transactions : transactionList) {
                    total += (Integer.parseInt(transactions.getTransactionAmount()));
                    txtTotalAmount.setText(String.valueOf(total));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DailyEntryDetail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setUpToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DailyEntryDetail.this, MainDashboard.class));
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        txtTotalLabel = findViewById(R.id.txtTotalLabel);
        databaseExtraIncome = FirebaseDatabase.getInstance().getReference("Extra Income");
        databaseTransaction = FirebaseDatabase.getInstance().getReference("Transaction");
        recyclerViewDailyEntry = findViewById(R.id.recyclerViewDailyDetails);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        extraIncomeList = new ArrayList<>();
        transactionList=new ArrayList<>();
        progressBarDailyEntry=findViewById(R.id.progressBarDailyEntry);
    }
}
