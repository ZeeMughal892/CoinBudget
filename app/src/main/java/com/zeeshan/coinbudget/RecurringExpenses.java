package com.zeeshan.coinbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeshan.coinbudget.adapter.RecurringExpenseAdapter;
import com.zeeshan.coinbudget.model.BankAccount;
import com.zeeshan.coinbudget.model.ExpenseOverview;
import com.zeeshan.coinbudget.model.Income;
import com.zeeshan.coinbudget.model.Lookup;
import com.zeeshan.coinbudget.model.Savings;
import com.zeeshan.coinbudget.model.User;

import java.util.ArrayList;
import java.util.List;

public class RecurringExpenses extends AppCompatActivity {
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerViewRecurringExpenses;
    List<Lookup> lookupList;
    RecurringExpenseAdapter recurringExpenseAdapter;
    DatabaseReference databaseLookup, databaseUsers;
    String LookupName = "Recurring Monthly Expense";
    ProgressBar progressBarRecurringExpenses;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    DatabaseReference databaseUser, databaseIncome, databaseRecurringExpense, databaseEstimatedExpense, databaseExtraIncome, databaseSavings, databaseBankAccount;
    NavigationView navigationView;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    List<ExpenseOverview> expenseOverviewList;
    FloatingActionButton btnAddExtraIncome, btnAddNewTransaction;
    ConstraintLayout messageContainer;

    Dialog  dialogBank,    dialogBudget, dialogIncome, dialogExpenses, dialogSavings;

    Button  btnRecurringExpense, btnEstimatedExpense, btnSavingDetails, btnAddSavings, btnIncomeDetails,
            btnSelectGoalDate, btnSelectDateBank, btnAddIncome, btnSelectDateIncome, btnAddBankAmount, btnAddBankAccount, btnAddLoanAccount, btnAddAdditionalAccount;

    TextView txtTotalIncomeBudget, txtTotalRecurringExpenseBudget, txtTotalEstimatedExpenseBudget, txtTotalRemainingAmountBudget, txtAccountBalance, txtOR;

    EditText edEmail,  edPassword,  edAmountBank, edDateBank,
            edIncomeAmount, edIncomeDescription, edIncomeDate, edSavingDate, edSavingAmount, edSavingTitle;

    Spinner spinnerFrequencyIncome;

    String format;
    ProgressBar progressBarCurrency, progressBarBudget;
    private DatePickerDialog datePickerDialog;

    private int hourAlarm, minuteAlarm;
    private String fullName, userName, pin, currency, payFrequency;
    private Boolean isPremium = false;
    private int totalIncome, totalRecurring, totalEstimated = 0;
    private Double totalAccountBalance, totalRemainingBudget,totalExtraIncome = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_expenses);

        init();
        setUpToolbar();
        progressBarRecurringExpenses.setVisibility(View.VISIBLE);
        dialogBank = new Dialog(RecurringExpenses.this);
        dialogBank.setContentView(R.layout.dialog_bank);
        dialogBank.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogBudget = new Dialog(RecurringExpenses.this);
        dialogBudget.setContentView(R.layout.dialog_budget);
        dialogBudget.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogIncome = new Dialog(RecurringExpenses.this);
        dialogIncome.setContentView(R.layout.dialog_income_overview);
        dialogIncome.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogExpenses = new Dialog(RecurringExpenses.this);
        dialogExpenses.setContentView(R.layout.dialog_expenses_overview);
        dialogExpenses.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogSavings = new Dialog(RecurringExpenses.this);
        dialogSavings.setContentView(R.layout.dialog_savings);
        dialogSavings.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        bottomNavigationView.setSelectedItemId(R.id.expenses);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bank:

                        txtAccountBalance = dialogBank.findViewById(R.id.txtAccountBalance);
                        txtOR = dialogBank.findViewById(R.id.txtOR);

                        edAmountBank = dialogBank.findViewById(R.id.ed_AmountBank);
                        edDateBank = dialogBank.findViewById(R.id.ed_DateBank);

                        btnSelectDateBank = dialogBank.findViewById(R.id.btnSelectBankDate);
                        btnAddBankAmount = dialogBank.findViewById(R.id.btnAddBankAmount);
                        btnAddBankAccount = dialogBank.findViewById(R.id.btnAddBankAccount);
                        btnAddLoanAccount = dialogBank.findViewById(R.id.btnAddLoanAccount);
                        btnAddAdditionalAccount = dialogBank.findViewById(R.id.btnAddAdditionalAccount);

                        if (!isPremium) {
                            txtOR.setVisibility(View.GONE);
                            btnAddLoanAccount.setVisibility(View.GONE);
                            btnAddBankAccount.setVisibility(View.GONE);
                            btnAddAdditionalAccount.setVisibility(View.GONE);
                        }
                        databaseBankAccount.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                totalAccountBalance = 0.0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    BankAccount bankAccount = snapshot.getValue(BankAccount.class);
                                    if (firebaseUser.getUid().equals(bankAccount.getUserId())) {
                                        totalAccountBalance += Double.parseDouble(bankAccount.getAmount());
                                    }
                                }
                                if (totalAccountBalance == null) {
                                    txtAccountBalance.setText("0.00");
                                }
                                txtAccountBalance.setText(String.valueOf(totalAccountBalance));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(RecurringExpenses.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        datePickerDialog = new DatePickerDialog(RecurringExpenses.this);
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = month+1 + "/" + day + "/" + year;
                                edDateBank.setText(Date);
                            }
                        });

                        btnSelectDateBank.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                datePickerDialog.show();
                            }
                        });

                        btnAddBankAmount.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String accountId = databaseBankAccount.push().getKey();
                                String userId = firebaseUser.getUid();
                                String amount = edAmountBank.getText().toString().trim();
                                String date = edDateBank.getText().toString().trim();
                                if (TextUtils.isEmpty(amount)) {
                                    Toast.makeText(RecurringExpenses.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(date)) {
                                    Toast.makeText(RecurringExpenses.this, "Please select date", Toast.LENGTH_SHORT).show();
                                } else {
                                    BankAccount bankAccount = new BankAccount(accountId, userId, amount, date);
                                    databaseBankAccount.child(accountId).setValue(bankAccount);
                                    Toast.makeText(RecurringExpenses.this, "Amount Added", Toast.LENGTH_SHORT).show();
                                    edAmountBank.setText(null);
                                    edDateBank.setText(null);
                                    dialogBank.dismiss();
                                }
                            }
                        });
                        dialogBank.show();
                        break;
                    case R.id.budget:
                        startActivity(new Intent(RecurringExpenses.this, Budget.class));
                        break;
                    case R.id.income:
                        edIncomeAmount = dialogIncome.findViewById(R.id.ed_IncomeAmount);
                        edIncomeDate = dialogIncome.findViewById(R.id.ed_DateIncome);
                        edIncomeDescription = dialogIncome.findViewById(R.id.ed_descIncome);
                        spinnerFrequencyIncome = dialogIncome.findViewById(R.id.spinnerIncome);

                        btnIncomeDetails = dialogIncome.findViewById(R.id.btnIncomeDetails);
                        btnAddIncome = dialogIncome.findViewById(R.id.btnAddIncome);
                        btnSelectDateIncome = dialogIncome.findViewById(R.id.btnSelectDateIncome);

                        datePickerDialog = new DatePickerDialog(RecurringExpenses.this);
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = month+1 + "/" + day + "/" + year;
                                edIncomeDate.setText(Date);
                            }
                        });
                        btnSelectDateIncome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                datePickerDialog.show();
                            }
                        });
                        btnIncomeDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(RecurringExpenses.this, IncomeDetails.class));
                            }
                        });
                        btnAddIncome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String incomeId = databaseIncome.push().getKey();
                                String userId = firebaseUser.getUid();
                                String incomeAmount = edIncomeAmount.getText().toString().trim();
                                String date = edIncomeDate.getText().toString().trim();
                                String desc = edIncomeDescription.getText().toString().trim();
                                String frequency = spinnerFrequencyIncome.getSelectedItem().toString().trim();
                                if (TextUtils.isEmpty(incomeAmount)) {
                                    Toast.makeText(RecurringExpenses.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(date)) {
                                    Toast.makeText(RecurringExpenses.this, "Please select date", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(desc)) {
                                    Toast.makeText(RecurringExpenses.this, "Please enter description", Toast.LENGTH_SHORT).show();
                                } else {
                                    Income income = new Income(incomeId, userId, incomeAmount, frequency, date, desc);
                                    databaseIncome.child(incomeId).setValue(income);
                                    Toast.makeText(view.getContext(), "Income Added Successfully", Toast.LENGTH_SHORT).show();
                                    edIncomeAmount.setText(null);
                                    edIncomeDate.setText(null);
                                    edIncomeDescription.setText(null);
                                    dialogIncome.dismiss();
                                }
                            }
                        });
                        dialogIncome.show();
                        break;
                    case R.id.expenses:
                        btnRecurringExpense = dialogExpenses.findViewById(R.id.btnRecurringExpense);
                        btnEstimatedExpense = dialogExpenses.findViewById(R.id.btnEstimatedExpense);

                        btnRecurringExpense.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(RecurringExpenses.this, RecurringExpenses.class));
                                dialogExpenses.dismiss();
                            }
                        });
                        btnEstimatedExpense.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(RecurringExpenses.this, EstimatedExpenses.class));
                                dialogExpenses.dismiss();
                            }
                        });
                        dialogExpenses.show();
                        break;
                    case R.id.savings:
                        edSavingTitle = dialogSavings.findViewById(R.id.ed_titleSaving);
                        edSavingDate = dialogSavings.findViewById(R.id.ed_DateSaving);
                        edSavingAmount = dialogSavings.findViewById(R.id.ed_SavingAmount);

                        btnSelectGoalDate = dialogSavings.findViewById(R.id.btnSelectGoalDate);
                        btnSavingDetails = dialogSavings.findViewById(R.id.btnSavingDetails);
                        btnAddSavings = dialogSavings.findViewById(R.id.btnAddSavings);

                        datePickerDialog = new DatePickerDialog(RecurringExpenses.this);
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = month+1 + "/" + day + "/" + year;
                                edSavingDate.setText(Date);
                            }
                        });
                        btnSelectGoalDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                datePickerDialog.show();
                            }
                        });
                        btnSavingDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(RecurringExpenses.this, SavingDetails.class));
                            }
                        });

                        btnAddSavings.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String savingId = databaseSavings.push().getKey();
                                String userId = firebaseUser.getUid();
                                String title = edSavingTitle.getText().toString().trim();
                                String amount = edSavingAmount.getText().toString().trim();
                                String date = edSavingDate.getText().toString().trim();
                                if (TextUtils.isEmpty(title)) {
                                    Toast.makeText(RecurringExpenses.this, "Please enter goal title", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(amount)) {
                                    Toast.makeText(RecurringExpenses.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(date)) {
                                    Toast.makeText(RecurringExpenses.this, "Please select date", Toast.LENGTH_SHORT).show();
                                } else {
                                    com.zeeshan.coinbudget.model.Savings savings = new Savings(savingId, userId, title, amount, date);
                                    databaseSavings.child(savingId).setValue(savings);
                                    Toast.makeText(RecurringExpenses.this, "Saving Goal Added", Toast.LENGTH_SHORT).show();
                                    edSavingTitle.setText(null);
                                    edSavingAmount.setText(null);
                                    edSavingDate.setText(null);
                                    dialogSavings.dismiss();
                                }
                            }
                        });

                        dialogSavings.show();
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
                Toast.makeText(RecurringExpenses.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                recurringExpenseAdapter = new RecurringExpenseAdapter(lookupList, LookupName);
                recyclerViewRecurringExpenses.setLayoutManager(new GridLayoutManager(RecurringExpenses.this, 3));
                recyclerViewRecurringExpenses.setAdapter(recurringExpenseAdapter);
                progressBarRecurringExpenses.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecurringExpenses.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBarRecurringExpenses.setVisibility(View.INVISIBLE);
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
                startActivity(new Intent(RecurringExpenses.this, MainDashboard.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }
    @Override
    public void onBackPressed() {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btnDetails) {
            startActivity(new Intent(RecurringExpenses.this,RecurringExpensesDetails.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerViewRecurringExpenses = findViewById(R.id.recyclerViewRecurringExpenses);
        lookupList = new ArrayList<>();
        databaseLookup = FirebaseDatabase.getInstance().getReference("Lookups");
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        progressBarRecurringExpenses = findViewById(R.id.progressBarRecurringExpenses);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);
        navigationView = findViewById(R.id.navigationView);
        expenseOverviewList = new ArrayList<>();
        btnAddExtraIncome = findViewById(R.id.btnAddExtraIncome);
        btnAddNewTransaction = findViewById(R.id.btnAddNewTransaction);
        databaseUser = FirebaseDatabase.getInstance().getReference("Users");
        databaseBankAccount = FirebaseDatabase.getInstance().getReference("Bank Account");
        databaseSavings = FirebaseDatabase.getInstance().getReference("Saving Goals");
        databaseIncome = FirebaseDatabase.getInstance().getReference("Income");
        databaseExtraIncome = FirebaseDatabase.getInstance().getReference("Extra Income");
        databaseEstimatedExpense = FirebaseDatabase.getInstance().getReference("Estimated Monthly Expense");
        databaseRecurringExpense = FirebaseDatabase.getInstance().getReference("Recurring Monthly Expense");
    }
}
