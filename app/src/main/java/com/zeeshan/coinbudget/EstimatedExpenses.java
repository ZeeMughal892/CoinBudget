package com.zeeshan.coinbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeshan.coinbudget.adapter.EstimatedExpenseAdapter;
import com.zeeshan.coinbudget.model.Lookup;
import com.zeeshan.coinbudget.model.User;

import java.util.ArrayList;
import java.util.List;

public class EstimatedExpenses extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerViewEstimatedExpenses;
    List<Lookup> lookupList;
    EstimatedExpenseAdapter estimatedExpenseAdapter;
    DatabaseReference databaseLookup, databaseUsers;
    String LookupName = "Estimated Monthly Expense";
    ProgressBar progressBarEstimatedExpenses;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Boolean isPremium;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimated_expenses);

        init();
        setUpToolbar();
        progressBarEstimatedExpenses.setVisibility(View.VISIBLE);
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
        loadLookups();


    }

    private void loadLookups() {
        databaseUsers.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                isPremium = user.isPremium;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EstimatedExpenses.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseLookup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lookupList.clear();
                for (DataSnapshot lookupSnapshot : dataSnapshot.getChildren()) {
                    Lookup lookup = lookupSnapshot.getValue(Lookup.class);
                    if (LookupName.equals(lookup.getLookUpName()) && lookup.getPremium().equals(isPremium)) {
                        lookupList.add(lookup);
                    }
                }
                estimatedExpenseAdapter = new EstimatedExpenseAdapter(lookupList, LookupName);
                recyclerViewEstimatedExpenses.setLayoutManager(new GridLayoutManager(EstimatedExpenses.this, 3));
                recyclerViewEstimatedExpenses.setAdapter(estimatedExpenseAdapter);
                progressBarEstimatedExpenses.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EstimatedExpenses.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBarEstimatedExpenses.setVisibility(View.INVISIBLE);
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
                startActivity(new Intent(EstimatedExpenses.this, MainDashboard.class));
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btnDetails) {
            startActivity(new Intent(EstimatedExpenses.this,EstimatedExpensesDetails.class));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void init() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerViewEstimatedExpenses = findViewById(R.id.recyclerViewEstimatedExpenses);
        lookupList = new ArrayList<>();
        databaseLookup = FirebaseDatabase.getInstance().getReference("Lookups");
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        progressBarEstimatedExpenses = findViewById(R.id.progressBarEstimatedExpenses);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }
}
