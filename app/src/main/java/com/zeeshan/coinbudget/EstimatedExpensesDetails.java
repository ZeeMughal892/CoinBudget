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
import com.zeeshan.coinbudget.adapter.EstimatedDetailAdapter;
import com.zeeshan.coinbudget.adapter.EstimatedExpenseAdapter;
import com.zeeshan.coinbudget.adapter.RecurringDetailAdapter;
import com.zeeshan.coinbudget.model.EstimatedExpenses;
import com.zeeshan.coinbudget.model.RecurringExpenses;

import java.util.ArrayList;
import java.util.List;

public class EstimatedExpensesDetails extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerViewEstimatedDetails;
    EstimatedDetailAdapter estimatedDetailAdapter;
    List<EstimatedExpenses> estimatedExpensesList;
    DatabaseReference databaseEstimatedExpenses;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TextView txtTotalEstimatedAmount;

    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimated_expenses_details);
        init();
        setUpToolbar();
        loadEstimatedExpense();
        bottomNavigationView.setSelectedItemId(R.id.income);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bank:
                        startActivity(new Intent(getApplicationContext(), Bank.class));
                        break;
                    case R.id.budget:
                        startActivity(new Intent(getApplicationContext(), Budget.class));
                        break;
                    case R.id.income:
                        startActivity(new Intent(getApplicationContext(), EstimatedExpensesDetails.class));
                        break;
                    case R.id.expenses:
                        startActivity(new Intent(getApplicationContext(), RecurringExpensesDetails.class));
                        break;
                    case R.id.savings:
                        startActivity(new Intent(getApplicationContext(), Savings.class));
                        break;
                }
                return true;
            }
        });
    }

    private void loadEstimatedExpense() {
        databaseEstimatedExpenses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                estimatedExpensesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EstimatedExpenses estimatedExpenses = snapshot.getValue(EstimatedExpenses.class);
                    if (estimatedExpenses.getUserID().equals(firebaseUser.getUid())) {
                        estimatedExpensesList.add(estimatedExpenses);

                    }
                }
                estimatedDetailAdapter = new EstimatedDetailAdapter(estimatedExpensesList);
                recyclerViewEstimatedDetails.setHasFixedSize(true);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewEstimatedDetails.setLayoutManager(mLayoutManager);
                recyclerViewEstimatedDetails.setItemAnimator(new DefaultItemAnimator());
                recyclerViewEstimatedDetails.setAdapter(estimatedDetailAdapter);

                for (EstimatedExpenses estimatedExpenses : estimatedExpensesList) {
                    total += (Integer.parseInt(estimatedExpenses.getExpenseAmount()));
                    txtTotalEstimatedAmount.setText(String.valueOf(total));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EstimatedExpensesDetails .this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(EstimatedExpensesDetails.this, MainDashboard.class));
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        estimatedExpensesList = new ArrayList<>();
        databaseEstimatedExpenses = FirebaseDatabase.getInstance().getReference("Estimated Monthly Expense");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        txtTotalEstimatedAmount = findViewById(R.id.txtTotalEstimatedAmount);
        recyclerViewEstimatedDetails=findViewById(R.id.recyclerViewEstimatedDetails);
    }
}
