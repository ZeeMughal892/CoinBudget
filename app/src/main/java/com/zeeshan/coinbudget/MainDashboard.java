package com.zeeshan.coinbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import com.zeeshan.coinbudget.APICommunicator.APICurrency;
import com.zeeshan.coinbudget.adapter.MainAdapter;
import com.zeeshan.coinbudget.interfaces.CurrencyService;
import com.zeeshan.coinbudget.model.CountryModel;
import com.zeeshan.coinbudget.model.ExpenseOverview;
import com.zeeshan.coinbudget.model.User;
import com.zeeshan.coinbudget.utils.AlarmReceiver;
import com.zeeshan.coinbudget.utils.AppUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainDashboard extends AppCompatActivity {

    DatabaseReference databaseUser;
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
    Dialog dialogReset, dialogUserInfo, dialogFrequency, dialogCurrency, dialogPin, dialogLogout, dialogReminder,dialogBudget;
    Button btnReset, btnCancel, btnUpdateUserInfo, btnUpdateCurrency, btnUpdateFrequency, btnUpdatePin, btnLogout, btnCancelLogout, btnSetReminder, btnSelectDateTime;
    EditText edEmail, edFullName, edPassword, edPin, edReEnterPin, edDateReminder, edNotesReminder;
    Spinner spinnerFrequency, spinnerCurrency;
    String format;
    ProgressBar progressBarCurrency;
    private int hourAlarm, minuteAlarm;
    private String fullName, userName, pin, currency, payFrequency;
    private Boolean isPremium=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_dashboard);
        init();
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
                    case R.id.payPeriodFrequency:
                        spinnerFrequency = dialogFrequency.findViewById(R.id.spinnerFrequency);
                        btnUpdateFrequency = dialogFrequency.findViewById(R.id.btnUpdateFrequency);
                        databaseUser.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                spinnerFrequency.setSelection(((ArrayAdapter) spinnerFrequency.getAdapter()).getPosition(user.frequency));
                                fullName = user.fullName;
                                userName = user.username;
                                pin = user.pin;
                                currency = user.currency;
                                isPremium = user.isPremium;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MainDashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        btnUpdateFrequency.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String frequency = spinnerFrequency.getSelectedItem().toString();
                                String userId = firebaseUser.getUid();
                                User user = new User(firebaseUser.getUid(), fullName, userName, currency, frequency, pin, isPremium);
                                databaseUser.child(userId).setValue(user);
                                Toast.makeText(MainDashboard.this, "Frequency Updated Successfully", Toast.LENGTH_SHORT).show();
                                dialogFrequency.dismiss();
                            }
                        });
                        dialogFrequency.show();
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
                        String time = hour + " : " + minute + " : " + format;
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

        dialogBudget = new Dialog(MainDashboard.this);
        dialogBudget.setContentView(R.layout.dialog_budget);
        dialogBudget.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bank:
                        startActivity(new Intent(getApplicationContext(), Bank.class));
                        break;
                    case R.id.budget:
                        dialogBudget.show();
                        break;
                    case R.id.income:
                        startActivity(new Intent(getApplicationContext(), Income.class));
                        break;
                    case R.id.expenses:
                        startActivity(new Intent(getApplicationContext(), Expenses.class));
                        break;
                    case R.id.savings:
                        startActivity(new Intent(getApplicationContext(), Savings.class));
                        break;
                }
                return true;
            }
        });
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
        databaseUser.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getPremium().equals(isPremium)){
                    hideItem();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainDashboard.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        MainAdapter mainAdapter = new MainAdapter(expenseOverviewList);
        recyclerView.setAdapter(mainAdapter);
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
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerView = findViewById(R.id.recyclerViewMain);
        expenseOverviewList = new ArrayList<>();
        btnAddExtraIncome = findViewById(R.id.btnAddExtraIncome);
        btnAddNewTransaction = findViewById(R.id.btnAddNewTransaction);
        messageContainer = findViewById(R.id.messageContainer);
        databaseUser = FirebaseDatabase.getInstance().getReference("Users");

    }
}
