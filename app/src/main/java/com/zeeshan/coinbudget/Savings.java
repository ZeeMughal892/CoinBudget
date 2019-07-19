package com.zeeshan.coinbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Savings extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);
        init();
        setUpToolbar();
        bottomNavigationView.setSelectedItemId(R.id.savings);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.bank:
                        startActivity(new Intent(getApplicationContext(),Bank.class));
                        break;
                    case R.id.budget:
                        startActivity(new Intent(getApplicationContext(),Budget.class));
                        break;
                    case R.id.income:
                        startActivity(new Intent(getApplicationContext(),Income.class));
                        break;
                    case R.id.expenses:
                        startActivity(new Intent(getApplicationContext(),Expenses.class));
                        break;
                    case R.id.savings:
                        startActivity(new Intent(getApplicationContext(),Savings.class));
                        break;
                }
                return true;
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
                startActivity(new Intent(Savings.this, MainDashboard.class));
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
    }
}
