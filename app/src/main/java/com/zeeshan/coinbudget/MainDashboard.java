package com.zeeshan.coinbudget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
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
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.zeeshan.coinbudget.APICommunicator.APICurrency;
import com.zeeshan.coinbudget.adapter.MainAdapter;
import com.zeeshan.coinbudget.interfaces.CurrencyService;
import com.zeeshan.coinbudget.model.BankAccount;
import com.zeeshan.coinbudget.model.CountryModel;
import com.zeeshan.coinbudget.model.ExpenseOverview;
import com.zeeshan.coinbudget.model.Income;
import com.zeeshan.coinbudget.model.Savings;
import com.zeeshan.coinbudget.model.Transactions;
import com.zeeshan.coinbudget.model.User;
import com.zeeshan.coinbudget.utils.AlarmReceiver;
import com.zeeshan.coinbudget.utils.AppUtils;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    RecyclerView recyclerView;
    List<ExpenseOverview> expenseOverviewList;
    FloatingActionButton btnAddExtraIncome, btnAddNewTransaction;
    ConstraintLayout messageContainer;

    Double totalExtraIncome = 0.0;
    Double totalRecurring = 0.0;
    Double totalIncome = 0.0;
    Double totalEstimated = 0.0;

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

    int hourAlarm, minuteAlarm;
    String fullName, userName, pin, currency, payFrequency;
    Boolean isPremium = false;

    Double totalAccountBalance = 0.0, totalRemainingBudget = 0.0, totalAverageExpense = 0.0,
            totalRemainingBudget1 = 0.0, totalAverageExpense1 = 0.0,
            totalRemainingBudget2 = 0.0, totalAverageExpense2 = 0.0, totalExtraIncome2 = 0.0,
            totalRemainingBudget3 = 0.0, totalAverageExpense3 = 0.0, totalExtraIncome3 = 0.0;

    List<Income> incomeList;
    List<Transactions> transactionsList;
    List<BankAccount> bankAccountList;
    List<com.zeeshan.coinbudget.model.ExtraIncome> extraIncomeList;

    Double totalAmountInBank = 0.0, totalAmountInBank2 = 0.0,
            totalTransactionAmount = 0.0, totalAmountInBank3 = 0.0;

    CardView cardView1, cardView2, cardView3, cardView4;
    TextView txtRemainingBudget1, txtRemainingBudget2, txtRemainingBudget3,
            txtRemainingDays1, txtRemainingDays2, txtRemainingDays3,
            txtAverageBudget1, txtAverageBudget2, txtAverageBudget3;

    long dayCount = 0;
    long dayCount1 = 0;
    long dayCount2 = 0;
    long dayCount3 = 0;

    AdView mAdView;
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);

        setContentView(R.layout.activity_main_dashboard);
        init();

        //Ads Setup
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(android_id)
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d("ADMOB_ERROR_CODE", "admob error code: " + i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });
        mAdView.loadAd(adRequest);


        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        btnAddNewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(interstitialAd.isLoaded()){
                    interstitialAd.show();
                }else{
                    Log.d("TAG","The InterstitialAd wasn't loaded yet.");
                }
            }
        });
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });




        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.GONE);
        cardView3.setVisibility(View.GONE);
        setUpToolbar();
        loadDashboard();
        progressBar.setVisibility(View.GONE);

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
                                edEmail.setText(user.username);
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
                                String password = edPassword.getText().toString().trim();
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

                        final CurrencyService currencyService = APICurrency.getClient().create(CurrencyService.class);
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
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainDashboard.this, android.R.layout.simple_spinner_dropdown_item, currencies);
                                    spinnerCurrency.setAdapter(arrayAdapter);
                                    databaseUser.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User user = dataSnapshot.getValue(User.class);
                                            spinnerCurrency.setSelection(((ArrayAdapter) spinnerCurrency.getAdapter()).getPosition(user.currency));
                                            fullName = user.fullName;
                                            userName = user.username;
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
                                }
                            }

                            @Override
                            public void onFailure(Call<List<CountryModel>> call, Throwable t) {
                                Toast.makeText(MainDashboard.this, String.valueOf(t.getMessage()), Toast.LENGTH_SHORT).show();
                            }
                        });

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
                                userName = user.username;
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
                                txtTotalEstimatedExpenseBudget.setText(String.valueOf(totalEstimated));
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
                                txtTotalIncomeBudget.setText(String.valueOf(totalIncome));
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
                                txtTotalRecurringExpenseBudget.setText(String.valueOf(totalRecurring));

                                Double totalRemainingBudget = Double.parseDouble(txtTotalIncomeBudget.getText().toString()) -
                                        (Double.parseDouble(txtTotalEstimatedExpenseBudget.getText().toString()) +
                                                Double.parseDouble(txtTotalRecurringExpenseBudget.getText().toString()));
                                txtTotalRemainingAmountBudget.setText(String.valueOf(totalRemainingBudget));
                                progressBarBudget.setVisibility(View.INVISIBLE);
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
                                    txtAccountBalance.setText("0.00");
                                }
                                txtAccountBalance.setText(String.valueOf(totalAccountBalance));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                        datePickerDialog = new DatePickerDialog(MainDashboard.this);
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = (month) + 1 + "/" + day + "/" + year;
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


                        datePickerDialog = new DatePickerDialog(MainDashboard.this);
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = month + 1 + "/" + day + "/" + year;
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


                        datePickerDialog = new DatePickerDialog(MainDashboard.this);
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String Date = month + 1 + "/" + day + "/" + year;
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

    private void loadAd(){
        if(!interstitialAd.isLoaded()){
            interstitialAd.loadAd( new AdRequest.Builder().build());
        }
    }
    private void hideItem() {
        navigationView = findViewById(R.id.navigationView);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.pin).setVisible(false);
    }

    public void selectedTimeFormat(int hour) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCards();
    }

    private void loadCards() {
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
                        if (incomeList.size() >= 0) {
                            cardView1.setVisibility(View.VISIBLE);
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
                                    for (BankAccount bankAccount : bankAccountList) {
                                        totalAmountInBank = Double.parseDouble(bankAccount.getAmount());
                                    }
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
                                            Date today = new Date();
                                            if (incomeList.size() == 0) {
                                                Date nextDay = null;
                                                nextDay = new Date();
                                                dayCount = daysBetweenTwoDates(today, nextDay);
                                                totalRemainingBudget = 0.0;
                                                totalAverageExpense = 0.0;
                                                totalRemainingBudget = (totalAmountInBank + totalExtraIncome) - totalTransactionAmount;
                                                totalAverageExpense = (totalTransactionAmount) / transactionsList.size();

                                                txtRemainingDays1.setText(dayCount + " days");
                                                txtRemainingBudget1.setText("$ " + totalRemainingBudget);
                                                txtAverageBudget1.setText("$ " + totalAverageExpense.shortValue());
                                            } else if (incomeList.size() == 1) {
                                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                Date nextDay1 = null;
                                                try {
                                                    nextDay1 = sdf.parse(incomeList.get(0).getDateOfIncome());
                                                    dayCount1 = daysBetweenTwoDates(today, nextDay1);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                Double income1 = Double.parseDouble(incomeList.get(0).getIncomeAmount());
                                                totalRemainingBudget1 = 0.0;
                                                totalAverageExpense1 = 0.0;
                                                totalRemainingBudget1 = (income1 + totalExtraIncome) - totalTransactionAmount;
                                                totalAverageExpense1 = (totalTransactionAmount) / transactionsList.size();

                                                txtRemainingDays1.setText(dayCount1 + " days");
                                                txtRemainingBudget1.setText("$ " + totalRemainingBudget1);
                                                txtAverageBudget1.setText("$ " + totalAverageExpense1.shortValue());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (incomeList.size() == 2) {
                            cardView1.setVisibility(View.VISIBLE);
                            cardView2.setVisibility(View.VISIBLE);

                            databaseBankAccount.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    totalAmountInBank2 = 0.0;

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        BankAccount bankAccount = snapshot.getValue(BankAccount.class);
                                        if (firebaseUser.getUid().equals(bankAccount.getUserId())) {
                                            bankAccountList.add(bankAccount);
                                        }
                                    }
                                    for (BankAccount bankAccount : bankAccountList) {
                                        totalAmountInBank2 += Double.parseDouble(bankAccount.getAmount());
                                    }
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
                                                totalExtraIncome2 += Double.parseDouble(extraIncome.getExtraAmount());
                                            }

                                            Date today = new Date();
                                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                            Date nextDay = null;
                                            try {
                                                nextDay = sdf.parse(incomeList.get(1).getDateOfIncome());
                                                dayCount2 = daysBetweenTwoDates(today, nextDay);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            Double income2 = Double.parseDouble(incomeList.get(1).getIncomeAmount());
                                            totalRemainingBudget2 = 0.0;
                                            totalAverageExpense2 = 0.0;
                                            totalRemainingBudget2 = (totalExtraIncome2 + income2) - totalTransactionAmount;
                                            totalAverageExpense2 = (totalTransactionAmount) / transactionsList.size();

                                            txtRemainingDays2.setText(dayCount2 + " days");
                                            txtRemainingBudget2.setText("$ " + totalRemainingBudget2);
                                            txtAverageBudget2.setText("$ " + totalAverageExpense2.shortValue());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (incomeList.size() > 2) {
                            cardView1.setVisibility(View.VISIBLE);
                            cardView2.setVisibility(View.VISIBLE);
                            cardView3.setVisibility(View.VISIBLE);
                            databaseBankAccount.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    totalAmountInBank3 = 0.0;

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        BankAccount bankAccount = snapshot.getValue(BankAccount.class);
                                        if (firebaseUser.getUid().equals(bankAccount.getUserId())) {
                                            bankAccountList.add(bankAccount);
                                        }
                                    }
                                    for (BankAccount bankAccount : bankAccountList) {
                                        totalAmountInBank3 += Double.parseDouble(bankAccount.getAmount());
                                    }
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
                                                totalExtraIncome3 += Double.parseDouble(extraIncome.getExtraAmount());
                                            }
                                            Date today = new Date();
                                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                            Date nextDay = null;

                                            try {
                                                nextDay = sdf.parse(incomeList.get(2).getDateOfIncome());
                                                dayCount3 = daysBetweenTwoDates(today, nextDay);

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            Double income3 = Double.parseDouble(incomeList.get(2).getIncomeAmount());
                                            totalRemainingBudget3 = 0.0;
                                            totalAverageExpense3 = 0.0;

                                            totalRemainingBudget3 = (totalExtraIncome3 + income3) - totalTransactionAmount;
                                            totalAverageExpense3 = (totalTransactionAmount) / transactionsList.size();
                                            txtRemainingDays3.setText(dayCount3 + " days");
                                            txtRemainingBudget3.setText("$ " + totalRemainingBudget3);
                                            txtAverageBudget3.setText("$ " + totalAverageExpense3.shortValue());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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
        builder.setMessage("Do you want to close the Coin DailyEntryDetail?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
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

        cardView1 = findViewById(R.id.cardViewIncome1);
        cardView2 = findViewById(R.id.cardViewIncome2);
        cardView3 = findViewById(R.id.cardViewIncome3);

        txtRemainingBudget1 = findViewById(R.id.txtRemainingBudget1);
        txtRemainingBudget2 = findViewById(R.id.txtRemainingBudget2);
        txtRemainingBudget3 = findViewById(R.id.txtRemainingBudget3);

        txtRemainingDays1 = findViewById(R.id.txtRemainingDays1);
        txtRemainingDays2 = findViewById(R.id.txtRemainingDays2);
        txtRemainingDays3 = findViewById(R.id.txtRemainingDays3);

        txtAverageBudget1 = findViewById(R.id.txtAverageBudget1);
        txtAverageBudget2 = findViewById(R.id.txtAverageBudget2);
        txtAverageBudget3 = findViewById(R.id.txtAverageBudget3);

        incomeList = new ArrayList<>();
        transactionsList = new ArrayList<>();
        extraIncomeList = new ArrayList<>();
        bankAccountList = new ArrayList<>();


        incomeList = new ArrayList<>();
        transactionsList = new ArrayList<>();
        databaseTransaction = FirebaseDatabase.getInstance().getReference("Transaction");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
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

        mAdView = findViewById(R.id.adView);
    }
}
