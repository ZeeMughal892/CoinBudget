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
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
import com.zeeshan.coinbudget.adapter.EstimatedDetailAdapter;
import com.zeeshan.coinbudget.adapter.TransactionAdapter;
import com.zeeshan.coinbudget.model.BankAccount;
import com.zeeshan.coinbudget.model.EstimatedExpenses;
import com.zeeshan.coinbudget.model.ExpenseOverview;
import com.zeeshan.coinbudget.model.Income;
import com.zeeshan.coinbudget.model.Savings;
import com.zeeshan.coinbudget.model.User;

import java.util.ArrayList;
import java.util.Calendar;
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
    DatabaseReference databaseUser, databaseIncome, databaseRecurringExpense, databaseEstimatedExpense, databaseExtraIncome, databaseSavings, databaseBankAccount;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    List<ExpenseOverview> expenseOverviewList;
    FloatingActionButton btnAddExtraIncome, btnAddNewTransaction;
    ConstraintLayout messageContainer;

    Dialog dialogBank, dialogBudget, dialogIncome, dialogExpenses, dialogSavings;

    Button btnRecurringExpense, btnEstimatedExpense, btnSavingDetails, btnAddSavings, btnIncomeDetails, btnSelectGoalDate, btnSelectDateBank,
            btnAddIncome, btnSelectDateIncome, btnAddBankAmount, btnAddBankAccount, btnAddLoanAccount, btnAddAdditionalAccount;

    TextView txtTotalIncomeBudget, txtTotalRecurringExpenseBudget, txtTotalEstimatedExpenseBudget, txtTotalRemainingAmountBudget, txtAccountBalance, txtOR;

    EditText edEmail, edPassword, edAmountBank, edDateBank,
            edIncomeAmount, edIncomeDescription, edIncomeDate, edSavingDate, edSavingAmount, edSavingTitle;

    Spinner spinnerFrequencyIncome;

    String format, userCurrency;
    ProgressBar progressBarCurrency, progressBarBudget;
    private DatePickerDialog datePickerDialog;
    DatePickerDialog.OnDateSetListener dateSetListener;

    private Boolean isPremium = false;
    private Double totalAccountBalance = 0.00;

    int total = 0;
    Double totalExtraIncome = 0.0;
    Double totalRecurring = 0.0;
    Double totalIncome = 0.0;
    Double totalEstimated = 0.0;

    public static final int ITEMS_PER_AD = 4;
    private List<Object> recyclerItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimated_expenses_details);
        init();
        setUpToolbar();

        recyclerViewEstimatedDetails.setHasFixedSize(true);
        recyclerViewEstimatedDetails.setLayoutManager(new LinearLayoutManager(this));
        loadCurrency();

        MobileAds.initialize(EstimatedExpensesDetails.this, getString(R.string.AdMob_app_id));
        loadEstimatedExpense();


        dialogBank = new Dialog(EstimatedExpensesDetails.this);
        dialogBank.setContentView(R.layout.dialog_bank);
        dialogBank.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogBudget = new Dialog(EstimatedExpensesDetails.this);
        dialogBudget.setContentView(R.layout.dialog_budget);
        dialogBudget.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogIncome = new Dialog(EstimatedExpensesDetails.this);
        dialogIncome.setContentView(R.layout.dialog_income_overview);
        dialogIncome.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogExpenses = new Dialog(EstimatedExpensesDetails.this);
        dialogExpenses.setContentView(R.layout.dialog_expenses_overview);
        dialogExpenses.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogSavings = new Dialog(EstimatedExpensesDetails.this);
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
                                    txtAccountBalance.setText(userCurrency+" 0.00");
                                }
                                txtAccountBalance.setText(userCurrency+" "+totalAccountBalance);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(EstimatedExpensesDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        btnSelectDateBank.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);

                                datePickerDialog = new DatePickerDialog(EstimatedExpensesDetails.this, dateSetListener, year, month, day);
                                datePickerDialog.show();
                            }
                        });
                        dateSetListener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = (month + 1) + "/" + day + "/" + year;
                                edDateBank.setText(Date);
                            }
                        };

                        btnAddBankAmount.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String accountId = databaseBankAccount.push().getKey();
                                String userId = firebaseUser.getUid();
                                String amount = edAmountBank.getText().toString().trim();
                                String date = edDateBank.getText().toString().trim();
                                if (TextUtils.isEmpty(amount)) {
                                    Toast.makeText(EstimatedExpensesDetails.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(date)) {
                                    Toast.makeText(EstimatedExpensesDetails.this, "Please select date", Toast.LENGTH_SHORT).show();
                                } else {
                                    BankAccount bankAccount = new BankAccount(accountId, userId, amount, date);
                                    databaseBankAccount.child(accountId).setValue(bankAccount);
                                    Toast.makeText(EstimatedExpensesDetails.this, "Amount Added", Toast.LENGTH_SHORT).show();
                                    edAmountBank.setText(null);
                                    edDateBank.setText(null);
                                    dialogBank.dismiss();
                                }
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
                                totalEstimated = 0.0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    com.zeeshan.coinbudget.model.EstimatedExpenses estimatedExpenses = snapshot.getValue(com.zeeshan.coinbudget.model.EstimatedExpenses.class);
                                    if (estimatedExpenses.getUserID().equals(firebaseUser.getUid())) {
                                        totalEstimated += Double.parseDouble(estimatedExpenses.getExpenseAmount());
                                    }
                                }
                                txtTotalEstimatedExpenseBudget.setText(userCurrency+ " "+totalEstimated);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(EstimatedExpensesDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBarBudget.setVisibility(View.INVISIBLE);
                            }
                        });
                        databaseIncome.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                totalIncome = 0.0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Income income = snapshot.getValue(Income.class);
                                    if (income.getUserID().equals(firebaseUser.getUid())) {
                                        totalIncome += Double.parseDouble(income.getIncomeAmount());
                                    }
                                }
                                txtTotalIncomeBudget.setText(userCurrency+ " "+totalIncome);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(EstimatedExpensesDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                                        totalExtraIncome += Double.parseDouble(extraIncome.getExtraAmount());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(EstimatedExpensesDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBarBudget.setVisibility(View.INVISIBLE);
                            }
                        });
                        databaseRecurringExpense.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                totalRecurring = 0.0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    com.zeeshan.coinbudget.model.RecurringExpenses recurringExpenses = snapshot.getValue(com.zeeshan.coinbudget.model.RecurringExpenses.class);
                                    if (recurringExpenses.getUserID().equals(firebaseUser.getUid())) {
                                        totalRecurring += Integer.parseInt(recurringExpenses.getExpenseAmount());
                                    }
                                }
                                txtTotalRecurringExpenseBudget.setText(userCurrency+ " " + totalRecurring);
                                String[] incomeBudget = txtTotalIncomeBudget.getText().toString().split(userCurrency);
                                String[] estimatedExpenseBudget = txtTotalEstimatedExpenseBudget.getText().toString().split(userCurrency);
                                String[] recurringExpenseBudget = txtTotalRecurringExpenseBudget.getText().toString().split(userCurrency);
                                Double totalRemainingBudget = (Double.parseDouble(incomeBudget[1]) + totalExtraIncome)
                                        - (Double.parseDouble(estimatedExpenseBudget[1]) +  Double.parseDouble(recurringExpenseBudget[1]));

                                txtTotalRemainingAmountBudget.setText(userCurrency+ " " + totalRemainingBudget);
                                progressBarBudget.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(EstimatedExpensesDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

                        btnSelectDateIncome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);

                                datePickerDialog = new DatePickerDialog(EstimatedExpensesDetails.this, dateSetListener, year, month, day);
                                datePickerDialog.show();
                            }
                        });
                        dateSetListener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = (month + 1) + "/" + day + "/" + year;
                                edIncomeDate.setText(Date);
                            }
                        };
                        btnIncomeDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(EstimatedExpensesDetails.this, IncomeDetails.class));
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
                                    Toast.makeText(EstimatedExpensesDetails.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(date)) {
                                    Toast.makeText(EstimatedExpensesDetails.this, "Please select date", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(desc)) {
                                    Toast.makeText(EstimatedExpensesDetails.this, "Please enter description", Toast.LENGTH_SHORT).show();
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
                                startActivity(new Intent(EstimatedExpensesDetails.this, RecurringExpenses.class));
                                dialogExpenses.dismiss();
                            }
                        });
                        btnEstimatedExpense.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(EstimatedExpensesDetails.this, com.zeeshan.coinbudget.EstimatedExpenses.class));
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

                        btnSelectGoalDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);

                                datePickerDialog = new DatePickerDialog(EstimatedExpensesDetails.this, dateSetListener, year, month, day);
                                datePickerDialog.show();
                            }
                        });
                        dateSetListener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = (month + 1) + "/" + day + "/" + year;
                                edSavingDate.setText(Date);
                            }
                        };
                        btnSavingDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(EstimatedExpensesDetails.this, SavingDetails.class));
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
                                    Toast.makeText(EstimatedExpensesDetails.this, "Please enter goal title", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(amount)) {
                                    Toast.makeText(EstimatedExpensesDetails.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(date)) {
                                    Toast.makeText(EstimatedExpensesDetails.this, "Please select date", Toast.LENGTH_SHORT).show();
                                } else {
                                    com.zeeshan.coinbudget.model.Savings savings = new Savings(savingId, userId, title, amount, date);
                                    databaseSavings.child(savingId).setValue(savings);
                                    Toast.makeText(EstimatedExpensesDetails.this, "Saving Goal Added", Toast.LENGTH_SHORT).show();
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
    }

    private void loadCurrency() {
        databaseUser.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userCurrency = user.currency;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EstimatedExpensesDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getEstimatedExpenses() {
        recyclerItems.addAll(estimatedExpensesList);
    }

    private void getBannerAds() {
        for (int i = 0; i < recyclerItems.size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(EstimatedExpensesDetails.this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(getString(R.string.Banner_ad_unit_id));
            recyclerItems.add( i,adView);
        }
    }

    private void loadBannerAds() {
        for (int i = 0; i < recyclerItems.size(); i++) {
            Object item = recyclerItems.get(i);
            if (item instanceof AdView) {
                final AdView adView = (AdView) item;
                adView.loadAd(new AdRequest.Builder().build());
            }
        }
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
                getEstimatedExpenses();
                getBannerAds();
                loadBannerAds();
                estimatedDetailAdapter = new EstimatedDetailAdapter(recyclerItems, userCurrency);
                recyclerViewEstimatedDetails.setAdapter(estimatedDetailAdapter);

                for (EstimatedExpenses estimatedExpenses : estimatedExpensesList) {
                    total += (Integer.parseInt(estimatedExpenses.getExpenseAmount()));
                    txtTotalEstimatedAmount.setText(userCurrency+ " "+total);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EstimatedExpensesDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {

    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        estimatedExpensesList = new ArrayList<>();
        databaseEstimatedExpenses = FirebaseDatabase.getInstance().getReference("Estimated Monthly Expense");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        txtTotalEstimatedAmount = findViewById(R.id.txtTotalEstimatedAmount);
        recyclerViewEstimatedDetails = findViewById(R.id.recyclerViewEstimatedDetails);
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
