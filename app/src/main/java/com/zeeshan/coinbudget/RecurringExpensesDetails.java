package com.zeeshan.coinbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.zeeshan.coinbudget.adapter.RecurringDetailAdapter;
import com.zeeshan.coinbudget.adapter.RecurringExpenseAdapter;
import com.zeeshan.coinbudget.model.RecurringExpenses;

import java.util.ArrayList;
import java.util.List;

public class RecurringExpensesDetails extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerViewRecurringDetails;
    RecurringDetailAdapter recurringDetailAdapter;
    List<RecurringExpenses> recurringExpensesList;
    DatabaseReference databaseRecurringExpenses;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TextView txtTotalRecurringAmount;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_expenses_details);
        init();
        setUpToolbar();
        bottomNavigationView.setSelectedItemId(R.id.expenses);
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
        loadRecurringExpense();
    }

    private void loadRecurringExpense() {
        databaseRecurringExpenses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recurringExpensesList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    RecurringExpenses recurringExpenses=snapshot.getValue(RecurringExpenses.class);
                    if(recurringExpenses.getUserID().equals(firebaseUser.getUid())){
                        recurringExpensesList.add(recurringExpenses);

                    }
                }
                recurringDetailAdapter = new RecurringDetailAdapter(recurringExpensesList);
                recyclerViewRecurringDetails.setHasFixedSize(true);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewRecurringDetails.setLayoutManager(mLayoutManager);
                recyclerViewRecurringDetails.setItemAnimator(new DefaultItemAnimator());
                recyclerViewRecurringDetails.setAdapter(recurringDetailAdapter);

                for (RecurringExpenses recurringExpenses : recurringExpensesList) {
                    total += (Integer.parseInt(recurringExpenses.getExpenseAmount()));
                    txtTotalRecurringAmount.setText(String.valueOf(total));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecurringExpensesDetails.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(RecurringExpensesDetails.this, MainDashboard.class));
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerViewRecurringDetails = findViewById(R.id.recyclerViewRecurringDetails);
        recurringExpensesList = new ArrayList<>();
        databaseRecurringExpenses = FirebaseDatabase.getInstance().getReference("Recurring Monthly Expense");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        txtTotalRecurringAmount=findViewById(R.id.txtTotalRecurringAmount);
    }
}
