package com.zeeshan.coinbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.zeeshan.coinbudget.adapter.ExtraIncomeAdapter;
import com.zeeshan.coinbudget.adapter.TransactionAdapter;
import com.zeeshan.coinbudget.model.BankAccount;
import com.zeeshan.coinbudget.model.ExpenseOverview;
import com.zeeshan.coinbudget.model.ExtraIncome;
import com.zeeshan.coinbudget.model.Income;
import com.zeeshan.coinbudget.model.Savings;
import com.zeeshan.coinbudget.model.Transactions;

import java.util.ArrayList;
import java.util.List;

public class DailyEntryDetail extends AppCompatActivity {
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    String LookupName;
    TextView txtTotalLabel, txtTotalAmount;
    DatabaseReference  databaseTransaction;
    RecyclerView recyclerViewDailyEntry;
    ExtraIncomeAdapter extraIncomeAdapter;
    TransactionAdapter transactionAdapter;
    List<ExtraIncome> extraIncomeList;
    List<Transactions> transactionList;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ProgressBar progressBarDailyEntry;
    int total = 0;
    DatabaseReference databaseUser, databaseIncome, databaseRecurringExpense, databaseEstimatedExpense, databaseExtraIncome, databaseSavings, databaseBankAccount;
    NavigationView navigationView;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    List<ExpenseOverview> expenseOverviewList;
    FloatingActionButton btnAddExtraIncome, btnAddNewTransaction;
    ConstraintLayout messageContainer;

    Dialog dialogBank, dialogBudget, dialogIncome, dialogExpenses, dialogSavings;

    Button   btnRecurringExpense, btnEstimatedExpense, btnSavingDetails, btnAddSavings, btnIncomeDetails,
                 btnSelectGoalDate, btnSelectDateBank,
               btnAddIncome, btnSelectDateIncome, btnAddBankAmount, btnAddBankAccount, btnAddLoanAccount, btnAddAdditionalAccount;

    TextView txtTotalIncomeBudget, txtTotalRecurringExpenseBudget, txtTotalEstimatedExpenseBudget, txtTotalRemainingAmountBudget, txtAccountBalance, txtOR;

    EditText edEmail,  edPassword,     edAmountBank, edDateBank,
            edIncomeAmount, edIncomeDescription, edIncomeDate, edSavingDate, edSavingAmount, edSavingTitle;

    Spinner   spinnerFrequencyIncome;

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
        dialogBank = new Dialog(DailyEntryDetail.this);
        dialogBank.setContentView(R.layout.dialog_bank);
        dialogBank.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogBudget = new Dialog(DailyEntryDetail.this);
        dialogBudget.setContentView(R.layout.dialog_budget);
        dialogBudget.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogIncome = new Dialog(DailyEntryDetail.this);
        dialogIncome.setContentView(R.layout.dialog_income_overview);
        dialogIncome.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogExpenses = new Dialog(DailyEntryDetail.this);
        dialogExpenses.setContentView(R.layout.dialog_expenses_overview);
        dialogExpenses.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogSavings = new Dialog(DailyEntryDetail.this);
        dialogSavings.setContentView(R.layout.dialog_savings);
        dialogSavings.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
                                totalAccountBalance=0.0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    BankAccount bankAccount = snapshot.getValue(BankAccount.class);
                                    if (firebaseUser.getUid().equals(bankAccount.getUserId())) {
                                        totalAccountBalance += Double.parseDouble(bankAccount.getAmount());
                                    }
                                }
                                if(totalAccountBalance == null){
                                    txtAccountBalance.setText("0.00");
                                }
                                txtAccountBalance.setText(String.valueOf(totalAccountBalance));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        datePickerDialog = new DatePickerDialog(DailyEntryDetail.this);
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = day + "/" + month + "/" + year;
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
                                BankAccount bankAccount = new BankAccount(accountId, userId, amount, date);
                                databaseBankAccount.child(accountId).setValue(bankAccount);
                                Toast.makeText(DailyEntryDetail.this, "Amount Added", Toast.LENGTH_SHORT).show();
                                edAmountBank.setText(null);
                                edDateBank.setText(null);
                                dialogBank.dismiss();
                            }
                        });
                        dialogBank.show();
                        break;
                    case R.id.budget:
                        txtTotalIncomeBudget = dialogBudget.findViewById(R.id.txtTotalIncomeBudget);
                        txtTotalEstimatedExpenseBudget = dialogBudget.findViewById(R.id.txtEstimatedExpensesBudget);
                        txtTotalRecurringExpenseBudget = dialogBudget.findViewById(R.id.txtRecurringExpensesBudget);
                        txtTotalRemainingAmountBudget = dialogBudget.findViewById(R.id.txtRemainingAmountBudget);
                        progressBarBudget = dialogBudget.findViewById(R.id.progress_barBudget);

                        databaseEstimatedExpense.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                totalEstimated = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    com.zeeshan.coinbudget.model.EstimatedExpenses estimatedExpenses = snapshot.getValue(com.zeeshan.coinbudget.model.EstimatedExpenses.class);
                                    if (estimatedExpenses.getUserID().equals(firebaseUser.getUid())) {
                                        totalEstimated += Integer.parseInt(estimatedExpenses.getExpenseAmount());
                                    }
                                }
                                txtTotalEstimatedExpenseBudget.setText(String.valueOf(totalEstimated));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(DailyEntryDetail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBarBudget.setVisibility(View.INVISIBLE);
                            }
                        });
                        databaseIncome.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                totalIncome = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    com.zeeshan.coinbudget.model.Income income = snapshot.getValue(com.zeeshan.coinbudget.model.Income.class);
                                    if (income.getUserID().equals(firebaseUser.getUid())) {
                                        totalIncome += Integer.parseInt(income.getIncomeAmount());
                                    }
                                }
                                txtTotalIncomeBudget.setText(String.valueOf(totalIncome));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(DailyEntryDetail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBarBudget.setVisibility(View.INVISIBLE);
                            }
                        });
                        databaseExtraIncome.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                totalExtraIncome = 0.0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    com.zeeshan.coinbudget.model.ExtraIncome extraIncome = snapshot.getValue(com.zeeshan.coinbudget.model.ExtraIncome.class);
                                    if (extraIncome.getUserID().equals(firebaseUser.getUid())) {
                                        totalExtraIncome += Integer.parseInt(extraIncome.getExtraAmount());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(DailyEntryDetail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBarBudget.setVisibility(View.INVISIBLE);
                            }
                        });
                        databaseRecurringExpense.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                totalRecurring = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    com.zeeshan.coinbudget.model.RecurringExpenses recurringExpenses = snapshot.getValue(com.zeeshan.coinbudget.model.RecurringExpenses.class);
                                    if (recurringExpenses.getUserID().equals(firebaseUser.getUid())) {
                                        totalRecurring += Integer.parseInt(recurringExpenses.getExpenseAmount());
                                    }
                                }
                                txtTotalRecurringExpenseBudget.setText(String.valueOf(totalRecurring));

                                totalRemainingBudget = Double.parseDouble(txtTotalIncomeBudget.getText().toString()) - (Double.parseDouble(txtTotalEstimatedExpenseBudget.getText().toString()) + Double.parseDouble(txtTotalRecurringExpenseBudget.getText().toString()));
                                txtTotalRemainingAmountBudget.setText(String.valueOf(totalRemainingBudget));
                                progressBarBudget.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(DailyEntryDetail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBarBudget.setVisibility(View.INVISIBLE);
                            }
                        });
                        txtTotalRemainingAmountBudget.setText(null);
                        txtTotalRecurringExpenseBudget.setText(null);
                        txtTotalEstimatedExpenseBudget.setText(null);
                        txtTotalIncomeBudget.setText(null);
                        dialogBudget.show();
                        break;
                    case R.id.income:
                        edIncomeAmount = dialogIncome.findViewById(R.id.ed_IncomeAmount);
                        edIncomeDate = dialogIncome.findViewById(R.id.ed_DateIncome);
                        edIncomeDescription = dialogIncome.findViewById(R.id.ed_descIncome);
                        spinnerFrequencyIncome = dialogIncome.findViewById(R.id.spinnerIncome);

                        btnIncomeDetails = dialogIncome.findViewById(R.id.btnIncomeDetails);
                        btnAddIncome = dialogIncome.findViewById(R.id.btnAddIncome);
                        btnSelectDateIncome = dialogIncome.findViewById(R.id.btnSelectDateIncome);

                        datePickerDialog = new DatePickerDialog(DailyEntryDetail.this);
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = day + "/" + month + "/" + year;
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
                                startActivity(new Intent(DailyEntryDetail.this, IncomeDetails.class));
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
                                Income income = new Income(incomeId, userId, incomeAmount, frequency, date, desc);
                                databaseIncome.child(incomeId).setValue(income);
                                Toast.makeText(view.getContext(), "Income Added Successfully", Toast.LENGTH_SHORT).show();
                                edIncomeAmount.setText(null);
                                edIncomeDate.setText(null);
                                edIncomeDescription.setText(null);
                                dialogIncome.dismiss();
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
                                startActivity(new Intent(DailyEntryDetail.this, RecurringExpenses.class));
                                dialogExpenses.dismiss();
                            }
                        });
                        btnEstimatedExpense.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(DailyEntryDetail.this, EstimatedExpenses.class));
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

                        datePickerDialog = new DatePickerDialog(DailyEntryDetail.this);
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = day + "/" + month + "/" + year;
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
                                startActivity(new Intent(DailyEntryDetail.this, SavingDetails.class));
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
                                com.zeeshan.coinbudget.model.Savings savings = new Savings(savingId, userId, title, amount, date);
                                databaseSavings.child(savingId).setValue(savings);
                                Toast.makeText(DailyEntryDetail.this, "Saving Goal Added", Toast.LENGTH_SHORT).show();
                                edSavingTitle.setText(null);
                                edSavingAmount.setText(null);
                                edSavingDate.setText(null);
                                dialogSavings.dismiss();
                            }
                        });

                        dialogSavings.show();
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
        txtTotalLabel = findViewById(R.id.txtTotalLabel);
        databaseTransaction = FirebaseDatabase.getInstance().getReference("Transaction");
        recyclerViewDailyEntry = findViewById(R.id.recyclerViewDailyDetails);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        extraIncomeList = new ArrayList<>();
        transactionList=new ArrayList<>();
        progressBarDailyEntry=findViewById(R.id.progressBarDailyEntry);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerView = findViewById(R.id.recyclerViewMain);
        expenseOverviewList = new ArrayList<>();
        btnAddExtraIncome = findViewById(R.id.btnAddExtraIncome);
        btnAddNewTransaction = findViewById(R.id.btnAddNewTransaction);
        messageContainer = findViewById(R.id.messageContainer);
        databaseUser = FirebaseDatabase.getInstance().getReference("Users");
        databaseBankAccount = FirebaseDatabase.getInstance().getReference("Bank Account");
        databaseSavings = FirebaseDatabase.getInstance().getReference("Saving Goals");
        databaseIncome = FirebaseDatabase.getInstance().getReference("Income");
        databaseExtraIncome = FirebaseDatabase.getInstance().getReference("Extra Income");
        databaseEstimatedExpense = FirebaseDatabase.getInstance().getReference("Estimated Monthly Expense");
        databaseRecurringExpense = FirebaseDatabase.getInstance().getReference("Recurring Monthly Expense");
    }
}
