package com.zeeshan.coinbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeshan.coinbudget.adapter.SavingAdapter;
import com.zeeshan.coinbudget.adapter.TransactionAdapter;
import com.zeeshan.coinbudget.model.Savings;
import com.zeeshan.coinbudget.model.Transactions;

import java.util.ArrayList;
import java.util.List;

public class SavingDetails extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    DatabaseReference databaseSavings;
    RecyclerView recyclerViewSavingDetails;
    SavingAdapter savingAdapter;
    List<Savings> savingsList;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_details);
        init();
        setUpToolbar();
        loadSavings();
        bottomNavigationView.setSelectedItemId(R.id.savings);
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

    private void loadSavings() {
        databaseSavings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                savingsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.zeeshan.coinbudget.model.Savings savings = snapshot.getValue(com.zeeshan.coinbudget.model.Savings.class);
                    if (savings.getUserID().equals(firebaseUser.getUid())) {
                        savingsList.add(savings);
                    }
                }
                savingAdapter = new SavingAdapter(savingsList);
                recyclerViewSavingDetails.setHasFixedSize(true);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewSavingDetails.setLayoutManager(mLayoutManager);
                recyclerViewSavingDetails.setItemAnimator(new DefaultItemAnimator());
                recyclerViewSavingDetails.setAdapter(savingAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SavingDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(SavingDetails.this, MainDashboard.class));
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerViewSavingDetails=findViewById(R.id.recyclerViewSavingDetails);
        savingsList=new ArrayList<>();
        databaseSavings= FirebaseDatabase.getInstance().getReference("Saving Goals");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
    }
}
