package com.zeeshan.coinbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.zeeshan.coinbudget.model.BankAccount;
import com.zeeshan.coinbudget.model.ExpenseOverview;
import com.zeeshan.coinbudget.model.Income;
import com.zeeshan.coinbudget.model.Savings;
import com.zeeshan.coinbudget.model.Transactions;
import com.zeeshan.coinbudget.model.User;
import com.zeeshan.coinbudget.utils.AlarmReceiver;
import com.zeeshan.coinbudget.utils.AppUtils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainDashboard extends AppCompatActivity {


    DatabaseReference databaseUser, databaseIncome, databaseRecurringExpense, databaseEstimatedExpense, databaseExtraIncome, databaseSavings, databaseBankAccount, databaseTransaction;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    ProgressBar progressBar;
    BottomNavigationView bottomNavigationView;

    FloatingActionButton btnAddExtraIncome, btnAddNewTransaction;
    ConstraintLayout messageContainer;

    Double totalExtraIncome = 0.00;
    Double totalRecurring = 0.00;
    Double totalIncome = 0.00;
    Double totalEstimated = 0.00;

    Dialog dialogReset, dialogUserInfo, dialogFrequency, dialogCurrency, dialogBank,
            dialogPin, dialogLogout, dialogReminder, dialogBudget, dialogIncome, dialogExpenses, dialogSavings;

    Button btnReset, btnCancel, btnRecurringExpense, btnEstimatedExpense, btnSavingDetails, btnAddSavings, btnIncomeDetails,
            btnUpdateUserInfo, btnUpdateCurrency, btnUpdatePin, btnLogout, btnSelectGoalDate, btnSelectDateBank,
            btnCancelLogout, btnSetReminder, btnSelectDateTime, btnAddIncome, btnSelectDateIncome, btnAddBankAmount, btnAddBankAccount, btnAddLoanAccount, btnAddAdditionalAccount;

    TextView txtTotalIncomeBudget, txtTotalRecurringExpenseBudget, txtTotalEstimatedExpenseBudget, txtTotalRemainingAmountBudget, txtAccountBalance, txtOR;

    EditText edEmail, edFullName, edPassword, edPin, edReEnterPin, edDateReminder, edNotesReminder, edAmountBank, edDateBank,
            edIncomeAmount, edIncomeDescription, edIncomeDate, edSavingDate, edSavingAmount, edSavingTitle;

    Spinner spinnerCurrency, spinnerFrequencyIncome;

    String format;
    ProgressBar progressBarCurrency, progressBarBudget;
    DatePickerDialog datePickerDialog;
    DatePickerDialog.OnDateSetListener dateSetListener;

    int hourAlarm, minuteAlarm;
    String fullName, userName, pin, currency, payFrequency;
    Boolean isPremium = false;
    String userCurrency;

    Double totalAccountBalance = 0.00,
            totalRemainingBudget = 0.00, totalAverageExpense = 0.00,
            totalRemainingBudget1 = 0.00, totalAverageExpense1 = 0.00,
            totalRemainingBudget2 = 0.00, totalAverageExpense2 = 0.00,
            totalRemainingBudget3 = 0.00, totalAverageExpense3 = 0.00,
            totalAmountInBank = 0.00, totalTransactionAmount = 0.00;


    List<Income> incomeList;
    List<Transactions> transactionsList;
    List<BankAccount> bankAccountList;
    List<com.zeeshan.coinbudget.model.ExtraIncome> extraIncomeList;
    List<ExpenseOverview> expenseOverviewList;


    CardView cardView, cardView1, cardView2, cardView3;

    TextView txtRemainingBudget, txtRemainingDays, txtAverageBudget,
            txtRemainingBudget1, txtRemainingBudget2, txtRemainingBudget3,
            txtRemainingDays1, txtRemainingDays2, txtRemainingDays3,
            txtAverageBudget1, txtAverageBudget2, txtAverageBudget3;

    long dayCount = 0;
    long dayCount1 = 0;
    long dayCount2 = 0;
    long dayCount3 = 0;

    private AdView adView;
    private InterstitialAd interstitialAd;
    private NativeExpressAdView nativeExpressAdView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        prepareAd();
        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        init();

        setUpToolbar();
        loadDashboard();
        startAlarmBroadcastReceiver(MainDashboard.this);
        getAllCurrencies();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadCards();
                    }
                });
            }
        }, 100, 100, TimeUnit.MILLISECONDS);

        loadValues();
        progressBar.setVisibility(View.GONE);
        ScheduledExecutorService scheduledExecutorService1 = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService1.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (interstitialAd.isLoaded()) {
                            interstitialAd.show();
                        } else {
                        }
                        prepareAd();
                    }
                });
            }
        }, 45, 45, TimeUnit.SECONDS);


        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(String.valueOf(R.string.Banner_ad_unit_id));

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(android_id)
                .build();

        adView.loadAd(adRequest);
        nativeExpressAdView.loadAd(new AdRequest.Builder().addTestDevice(android_id).build());


        dialogFrequency = new Dialog(MainDashboard.this);
        dialogFrequency.setContentView(R.layout.dialog_frequency);
        dialogFrequency.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogUserInfo = new Dialog(MainDashboard.this);
        dialogUserInfo.setContentView(R.layout.dialog_user_information);
        dialogUserInfo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogCurrency = new Dialog(MainDashboard.this);
        dialogCurrency.setContentView(R.layout.dialog_currency);
        dialogCurrency.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogReminder = new Dialog(MainDashboard.this);
        dialogReminder.setContentView(R.layout.dialog_reminder);
        dialogReminder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogPin = new Dialog(MainDashboard.this);
        dialogPin.setContentView(R.layout.dialog_pin);
        dialogPin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogReset = new Dialog(MainDashboard.this);
        dialogReset.setContentView(R.layout.dialog_reset_app);
        dialogReset.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogLogout = new Dialog(MainDashboard.this);
        dialogLogout.setContentView(R.layout.dialog_logout);
        dialogLogout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(), MainDashboard.class));
                        break;
                    case R.id.userInformation:
                        edEmail = dialogUserInfo.findViewById(R.id.ed_Email);
                        edFullName = dialogUserInfo.findViewById(R.id.ed_FullName);
                        edPassword = dialogUserInfo.findViewById(R.id.ed_Password);
                        btnUpdateUserInfo = dialogUserInfo.findViewById(R.id.btnUpdateUserInfo);
                        databaseUser.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                edFullName.setText(user.fullName);
                                edEmail.setText(user.email);
                                pin = user.pin;
                                currency = user.currency;
                                isPremium = user.isPremium;
                                payFrequency = user.frequency;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        btnUpdateUserInfo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String userId = firebaseUser.getUid();
                                String email = edEmail.getText().toString().trim();
                                String fullName = edFullName.getText().toString().trim();
                                User user = new User(userId, fullName, email, currency, payFrequency, pin, isPremium);
                                databaseUser.child(userId).setValue(user);
                                Toast.makeText(MainDashboard.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                dialogUserInfo.dismiss();
                            }
                        });
                        dialogUserInfo.show();
                        break;
                    case R.id.currency:
                        spinnerCurrency = dialogCurrency.findViewById(R.id.spinnerCurrency);
                        btnUpdateCurrency = dialogCurrency.findViewById(R.id.btnUpdateCurrency);
                        progressBarCurrency = dialogCurrency.findViewById(R.id.progress_barCurrency);
                        List<String> currencyCodes = new ArrayList<>();
                        for (Currency currency : getAllCurrencies()) {
                            currencyCodes.add(currency.getCurrencyCode());
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainDashboard.this, android.R.layout.simple_spinner_dropdown_item, currencyCodes);
                        spinnerCurrency.setAdapter(arrayAdapter);
                        databaseUser.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                spinnerCurrency.setSelection(((ArrayAdapter) spinnerCurrency.getAdapter()).getPosition(user.currency));
                                fullName = user.fullName;
                                userName = user.email;
                                payFrequency = user.frequency;
                                pin = user.pin;
                                isPremium = user.isPremium;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        progressBarCurrency.setVisibility(View.INVISIBLE);


                      /*  final CurrencyService currencyService = APICurrency.getClient().create(CurrencyService.class);
                        Call<List<CountryModel>> call = currencyService.all();
                        call.enqueue(new Callback<List<CountryModel>>() {
                            @Override
                            public void onResponse(Call<List<CountryModel>> call, Response<List<CountryModel>> response) {
                                if (response.isSuccessful()) {
                                    List<CountryModel> countryModelList = response.body();
                                    List<String> currencies = new ArrayList<>();
                                    List<String> countryNames = new ArrayList<>();
                                    for (int i = 0; i < countryModelList.size(); i++) {
                                        countryNames.add(countryModelList.get(i).getName());
                                        currencies.addAll(countryModelList.get(i).getCurrencies());
                                    }

                                }
                            }

                            @Override
                            public void onFailure(Call<List<CountryModel>> call, Throwable t) {
                                Toast.makeText(MainDashboard.this, String.valueOf(t.getMessage()), Toast.LENGTH_SHORT).show();
                            }
                        });*/

                        btnUpdateCurrency.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String currency = spinnerCurrency.getSelectedItem().toString();
                                String userId = firebaseUser.getUid();
                                User user = new User(userId, fullName, userName, currency, payFrequency, pin, isPremium);
                                databaseUser.child(userId).setValue(user);
                                Toast.makeText(MainDashboard.this, "Currency Updated Successfully", Toast.LENGTH_SHORT).show();
                                dialogCurrency.dismiss();
                            }
                        });
                        dialogCurrency.show();
                        break;
                    case R.id.reminder:
                        edDateReminder = dialogReminder.findViewById(R.id.ed_DateReminder);
                        edNotesReminder = dialogReminder.findViewById(R.id.ed_NotesReminder);
                        btnSelectDateTime = dialogReminder.findViewById(R.id.btnSelectDateTime);
                        btnSetReminder = dialogReminder.findViewById(R.id.btnReminder);

                        Calendar currentTime;
                        final int hour, minute;
                        currentTime = Calendar.getInstance();
                        hour = currentTime.get(Calendar.HOUR_OF_DAY);
                        minute = currentTime.get(Calendar.MINUTE);
                        selectedTimeFormat(hour);
                        final String time = hour + " : " + minute + " : " + format;
                        edDateReminder.setText(time);
                        final int notificationId = 1;

                        btnSelectDateTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TimePickerDialog timePickerDialog = new TimePickerDialog(MainDashboard.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                        selectedTimeFormat(hourOfDay);
                                        String time = hourOfDay + " : " + minute + " : " + format;
                                        edDateReminder.setText(time);
                                        hourAlarm = hourOfDay;
                                        minuteAlarm = minute;
                                    }
                                }, hour, minute, false);
                                timePickerDialog.show();
                            }
                        });
                        btnSetReminder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainDashboard.this, AlarmReceiver.class);
                                intent.putExtra("notificationId", notificationId);
                                intent.putExtra("title", "Reminder");
                                intent.putExtra("notes", edNotesReminder.getText().toString().trim());
                                final PendingIntent alarmIntent = PendingIntent.getBroadcast(MainDashboard.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                final AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

                                Calendar startTime = Calendar.getInstance();
                                startTime.set(Calendar.HOUR_OF_DAY, hourAlarm);
                                startTime.set(Calendar.MINUTE, minuteAlarm);
                                startTime.set(Calendar.SECOND, 0);
                                long alarmStartTime = startTime.getTimeInMillis();
                                alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);
                                Toast.makeText(MainDashboard.this, "Reminder Set Successfully", Toast.LENGTH_SHORT).show();
                                dialogReminder.dismiss();
                            }
                        });
                        dialogReminder.show();
                        break;
                    case R.id.pin:
                        edPin = dialogPin.findViewById(R.id.ed_pin);
                        edReEnterPin = dialogPin.findViewById(R.id.ed_reEnterPin);
                        btnUpdatePin = dialogPin.findViewById(R.id.btnUpdatePin);
                        databaseUser.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                fullName = user.fullName;
                                userName = user.email;
                                edPin.setText(user.pin);
                                currency = user.currency;
                                isPremium = user.isPremium;
                                payFrequency = user.frequency;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        btnUpdatePin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String pin = edPin.getText().toString().trim();
                                String repin = edReEnterPin.getText().toString().trim();
                                if (pin.equals(repin)) {
                                    String userId = firebaseUser.getUid();
                                    User user = new User(userId, fullName, userName, currency, payFrequency, pin, isPremium);
                                    databaseUser.child(userId).setValue(user);
                                    Toast.makeText(MainDashboard.this, "Pin Generated", Toast.LENGTH_SHORT).show();
                                    dialogPin.dismiss();
                                } else {
                                    Toast.makeText(MainDashboard.this, "Pin Doesn't Match. Please Re-Enter", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialogPin.show();
                        break;
                    case R.id.resetApp:
                        btnReset = dialogReset.findViewById(R.id.btnReset);
                        btnCancel = dialogReset.findViewById(R.id.btnCancel);
                        btnReset.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AppUtils.clearData(MainDashboard.this);
                                Toast.makeText(MainDashboard.this, "Application Reset Complete", Toast.LENGTH_SHORT).show();
                                dialogReset.dismiss();
                            }
                        });
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogReset.dismiss();
                            }
                        });
                        dialogReset.show();
                        break;
                    case R.id.logout:
                        btnLogout = dialogLogout.findViewById(R.id.btnLogout);
                        btnCancelLogout = dialogLogout.findViewById(R.id.btnCancel);

                        btnLogout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AuthUI.getInstance().signOut(MainDashboard.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(MainDashboard.this, SignIn.class));
                                    }
                                });

                                FirebaseAuth.getInstance().signOut();
                                dialogLogout.dismiss();
                                Intent intent = new Intent(MainDashboard.this, Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                        btnCancelLogout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogLogout.dismiss();
                            }
                        });
                        dialogLogout.show();
                        break;
                }
                return true;
            }
        });

        dialogBank = new Dialog(MainDashboard.this);
        dialogBank.setContentView(R.layout.dialog_bank);
        dialogBank.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogBudget = new Dialog(MainDashboard.this);
        dialogBudget.setContentView(R.layout.dialog_budget);
        dialogBudget.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogIncome = new Dialog(MainDashboard.this);
        dialogIncome.setContentView(R.layout.dialog_income_overview);
        dialogIncome.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogExpenses = new Dialog(MainDashboard.this);
        dialogExpenses.setContentView(R.layout.dialog_expenses_overview);
        dialogExpenses.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogSavings = new Dialog(MainDashboard.this);
        dialogSavings.setContentView(R.layout.dialog_savings);
        dialogSavings.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
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
                                txtTotalEstimatedExpenseBudget.setText(userCurrency + " " + totalEstimated);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                                txtTotalIncomeBudget.setText(userCurrency + " " + totalIncome);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                                txtTotalRecurringExpenseBudget.setText(userCurrency + " " + totalRecurring);

                                String[] incomeBudget = txtTotalIncomeBudget.getText().toString().split(userCurrency);
                                String[] estimatedExpenseBudget = txtTotalEstimatedExpenseBudget.getText().toString().split(userCurrency);
                                String[] recurringExpenseBudget = txtTotalRecurringExpenseBudget.getText().toString().split(userCurrency);


                                Double totalRemainingBudget = (Double.parseDouble(incomeBudget[1]) + totalExtraIncome)
                                        - (Double.parseDouble(estimatedExpenseBudget[1]) + Double.parseDouble(recurringExpenseBudget[1]));

                                txtTotalRemainingAmountBudget.setText(userCurrency + " " + totalRemainingBudget);
                                progressBarBudget.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBarBudget.setVisibility(View.INVISIBLE);
                            }
                        });
                        txtTotalRemainingAmountBudget.setText(null);
                        txtTotalRecurringExpenseBudget.setText(null);
                        txtTotalEstimatedExpenseBudget.setText(null);
                        txtTotalIncomeBudget.setText(null);
                        dialogBudget.show();
                        break;
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
                                    txtAccountBalance.setText(userCurrency + " 0.00");
                                }
                                txtAccountBalance.setText(userCurrency + " " + totalAccountBalance);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        btnSelectDateBank.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);

                                datePickerDialog = new DatePickerDialog(MainDashboard.this, dateSetListener, year, month, day);
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
                                    Toast.makeText(MainDashboard.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(date)) {
                                    Toast.makeText(MainDashboard.this, "Please select date", Toast.LENGTH_SHORT).show();
                                } else {
                                    BankAccount bankAccount = new BankAccount(accountId, userId, amount, date);
                                    databaseBankAccount.child(accountId).setValue(bankAccount);
                                    Toast.makeText(MainDashboard.this, "Amount Added", Toast.LENGTH_SHORT).show();
                                    edAmountBank.setText(null);
                                    edDateBank.setText(null);
                                    dialogBank.dismiss();
                                }
                            }
                        });
                        dialogBank.show();
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

                                datePickerDialog = new DatePickerDialog(MainDashboard.this, dateSetListener, year, month, day);
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
                                startActivity(new Intent(MainDashboard.this, IncomeDetails.class));
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
                                    Toast.makeText(MainDashboard.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(date)) {
                                    Toast.makeText(MainDashboard.this, "Please select date", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(desc)) {
                                    Toast.makeText(MainDashboard.this, "Please enter description", Toast.LENGTH_SHORT).show();
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
                                startActivity(new Intent(MainDashboard.this, RecurringExpenses.class));
                                dialogExpenses.dismiss();
                            }
                        });
                        btnEstimatedExpense.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(MainDashboard.this, EstimatedExpenses.class));
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

                                datePickerDialog = new DatePickerDialog(MainDashboard.this, dateSetListener, year, month, day);
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
                                startActivity(new Intent(MainDashboard.this, SavingDetails.class));
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
                                    Toast.makeText(MainDashboard.this, "Please enter goal title", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(amount)) {
                                    Toast.makeText(MainDashboard.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(date)) {
                                    Toast.makeText(MainDashboard.this, "Please select date", Toast.LENGTH_SHORT).show();
                                } else {
                                    Savings savings = new Savings(savingId, userId, title, amount, date);
                                    databaseSavings.child(savingId).setValue(savings);
                                    Toast.makeText(MainDashboard.this, "Saving Goal Added", Toast.LENGTH_SHORT).show();
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
        btnAddNewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainDashboard.this, Transaction.class));
            }
        });
        btnAddExtraIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainDashboard.this, ExtraIncome.class));
            }
        });
    }

    public static List<Currency> getAllCurrencies() {
        List<Currency> toret = new ArrayList<>();
        Locale[] locs = Locale.getAvailableLocales();

        for (Locale loc : locs) {
            try {
                Currency currency = Currency.getInstance(loc);

                if (currency != null) {
                    toret.add(currency);
                }
            } catch (Exception exc) {
                // Locale not found
            }
        }

        return toret;
    }

    public static void startAlarmBroadcastReceiver(Context context) {
        final int notificationId = 1;
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("title", "Forget Something?");
        intent.putExtra("notes", "Don't forget to enter your daily expenses!");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar startTime = Calendar.getInstance();
        //startTime.setTimeInMillis(System.currentTimeMillis());
        startTime.set(Calendar.HOUR_OF_DAY, 20);
        startTime.set(Calendar.MINUTE, 00);
        startTime.set(Calendar.SECOND, 00);
        long alarmStartTime = startTime.getTimeInMillis();
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent);
    }

    public void prepareAd() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.Interstitial_ad_unit_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void hideItem() {
        navigationView = findViewById(R.id.navigationView);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.pin).setVisible(false);
    }

    public void selectedTimeFormat(int hour) {
        if (hour == 0) {
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            format = "PM";
        } else {
            format = "AM";
        }
    }

    private void loadDashboard() {
        progressBar.setVisibility(View.VISIBLE);
        databaseUser.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (isPremium.equals(user.getPremium())) {
                    hideItem();
                }
                userCurrency = user.currency;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadValues() {

        databaseIncome.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                incomeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Income income = snapshot.getValue(Income.class);
                    if (firebaseUser.getUid().equals(income.getUserID())) {
                        incomeList.add(income);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        databaseBankAccount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalAmountInBank = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BankAccount bankAccount = snapshot.getValue(BankAccount.class);
                    if (firebaseUser.getUid().equals(bankAccount.getUserId())) {
                        bankAccountList.add(bankAccount);
                    }
                }
                if (bankAccountList.size() > 0) {
                    totalAmountInBank = Double.parseDouble(bankAccountList.get(bankAccountList.size() - 1).getAmount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        databaseTransaction.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionsList.clear();
                totalTransactionAmount = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transactions transactions = snapshot.getValue(Transactions.class);
                    if (firebaseUser.getUid().equals(transactions.getUserID())) {
                        transactionsList.add(transactions);
                    }
                }
                for (Transactions transactions : transactionsList) {
                    totalTransactionAmount += Double.parseDouble(transactions.getTransactionAmount());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        databaseExtraIncome.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.zeeshan.coinbudget.model.ExtraIncome extraIncome = snapshot.getValue(com.zeeshan.coinbudget.model.ExtraIncome.class);
                    if (firebaseUser.getUid().equals(extraIncome.getUserID())) {
                        extraIncomeList.add(extraIncome);
                    }
                }
                for (com.zeeshan.coinbudget.model.ExtraIncome extraIncome : extraIncomeList) {
                    totalExtraIncome += Double.parseDouble(extraIncome.getExtraAmount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCards() {

        if (incomeList.size() != 0 || incomeList != null) {
            Date today = new Date();

            Date nextDay;
            nextDay = new Date();
            dayCount = daysBetweenTwoDates(today, nextDay);
            totalRemainingBudget = 0.00;
            totalAverageExpense = 0.00;

            totalRemainingBudget = (totalAmountInBank + totalExtraIncome) - totalTransactionAmount;


            totalAverageExpense = (totalTransactionAmount) / transactionsList.size();

            if(totalAverageExpense == 0){
                txtAverageBudget.setText(userCurrency + " 0.00"  );

            }else{
                txtAverageBudget.setText(userCurrency + " " + totalAverageExpense.shortValue());

            }



            txtRemainingDays.setText(dayCount + " days");
            txtRemainingBudget.setText(userCurrency + " " + totalRemainingBudget);

        }
        if (incomeList.size() > 0) {
            cardView1.setVisibility(View.VISIBLE);
            Date today = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Date nextDay1;
                try {
                    nextDay1 = sdf.parse(incomeList.get(0).getDateOfIncome());
                    dayCount1 = daysBetweenTwoDates(today, nextDay1) + 1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Double income1 = Double.parseDouble(incomeList.get(0).getIncomeAmount());
                totalRemainingBudget1 = 0.00;
                totalAverageExpense1 = 0.00;
                totalRemainingBudget1 = (income1 + totalRemainingBudget);
                totalAverageExpense1 = (totalTransactionAmount) / transactionsList.size();

                txtRemainingDays1.setText(dayCount1 + " days");
                txtRemainingBudget1.setText(userCurrency + " " + totalRemainingBudget1);
                txtAverageBudget1.setText(userCurrency + " " + totalAverageExpense1.shortValue());

        }
        if (incomeList.size() == 2) {
            cardView2.setVisibility(View.VISIBLE);
            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date nextDay;
            try {
                nextDay = sdf.parse(incomeList.get(1).getDateOfIncome());
                dayCount2 = daysBetweenTwoDates(today, nextDay) + 1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Double income2 = Double.parseDouble(incomeList.get(1).getIncomeAmount());
            totalRemainingBudget2 = 0.00;
            totalAverageExpense2 = 0.00;
            totalRemainingBudget2 = totalRemainingBudget1 + income2;
            totalAverageExpense2 = (totalTransactionAmount) / transactionsList.size();

            txtRemainingDays2.setText(dayCount2 + " days");
            txtRemainingBudget2.setText(userCurrency + " " + totalRemainingBudget2);
            txtAverageBudget2.setText(userCurrency + " " + totalAverageExpense2.shortValue());
        }
        if (incomeList.size() > 2) {
            cardView3.setVisibility(View.VISIBLE);
            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date nextDay;

            try {
                nextDay = sdf.parse(incomeList.get(2).getDateOfIncome());
                dayCount3 = daysBetweenTwoDates(today, nextDay) + 1;

            } catch (ParseException e) {
                e.printStackTrace();
            }
            Double income3 = Double.parseDouble(incomeList.get(2).getIncomeAmount());
            totalRemainingBudget3 = 0.00;
            totalAverageExpense3 = 0.00;

            totalRemainingBudget3 = totalRemainingBudget2 + income3;
            totalAverageExpense3 = (totalTransactionAmount) / transactionsList.size();
            txtRemainingDays3.setText(dayCount3 + " days");
            txtRemainingBudget3.setText(userCurrency + " " + totalRemainingBudget3);
            txtAverageBudget3.setText(userCurrency + " " + totalAverageExpense3.shortValue());
        }
    }

    public long daysBetweenTwoDates(Date dtOne, Date dtTwo) {
        long difference = (dtOne.getTime() - dtTwo.getTime()) / 86400000;
        return Math.abs(difference);

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_sort_black_24dp);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainDashboard.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to close the Coin Budget?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        // System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void init() {

        cardView = findViewById(R.id.cardViewIncome);
        cardView1 = findViewById(R.id.cardViewIncome1);
        cardView2 = findViewById(R.id.cardViewIncome2);
        cardView3 = findViewById(R.id.cardViewIncome3);

        txtRemainingBudget = findViewById(R.id.txtRemainingBudget);
        txtRemainingBudget1 = findViewById(R.id.txtRemainingBudget1);
        txtRemainingBudget2 = findViewById(R.id.txtRemainingBudget2);
        txtRemainingBudget3 = findViewById(R.id.txtRemainingBudget3);

        txtRemainingDays = findViewById(R.id.txtRemainingDays);
        txtRemainingDays1 = findViewById(R.id.txtRemainingDays1);
        txtRemainingDays2 = findViewById(R.id.txtRemainingDays2);
        txtRemainingDays3 = findViewById(R.id.txtRemainingDays3);

        txtAverageBudget = findViewById(R.id.txtAverageBudget);
        txtAverageBudget1 = findViewById(R.id.txtAverageBudget1);
        txtAverageBudget2 = findViewById(R.id.txtAverageBudget2);
        txtAverageBudget3 = findViewById(R.id.txtAverageBudget3);


        incomeList = new ArrayList<>();
        transactionsList = new ArrayList<>();
        extraIncomeList = new ArrayList<>();
        bankAccountList = new ArrayList<>();
        expenseOverviewList = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressBar = findViewById(R.id.progressBar);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        btnAddExtraIncome = findViewById(R.id.btnAddExtraIncome);
        btnAddNewTransaction = findViewById(R.id.btnAddNewTransaction);

        databaseUser = FirebaseDatabase.getInstance().getReference("Users");
        databaseBankAccount = FirebaseDatabase.getInstance().getReference("Bank Account");
        databaseSavings = FirebaseDatabase.getInstance().getReference("Saving Goals");
        databaseIncome = FirebaseDatabase.getInstance().getReference("Income");
        databaseTransaction = FirebaseDatabase.getInstance().getReference("Transaction");
        databaseExtraIncome = FirebaseDatabase.getInstance().getReference("Extra Income");
        databaseEstimatedExpense = FirebaseDatabase.getInstance().getReference("Estimated Monthly Expense");
        databaseRecurringExpense = FirebaseDatabase.getInstance().getReference("Recurring Monthly Expense");

        adView = findViewById(R.id.adView);
        nativeExpressAdView = findViewById(R.id.nativeExpressAd);



    }



}
