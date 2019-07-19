package com.zeeshan.coinbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zeeshan.coinbudget.model.User;



public class Register extends AppCompatActivity {

    EditText edFullName, edEmail, edPassword, edConfirmPassword;
    Button btnRegister, btnSignIn;
    DatabaseReference databaseUsers;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final String fullName = edFullName.getText().toString().trim();
                final String email = edEmail.getText().toString().trim();
                final String password = edPassword.getText().toString().trim();
                String confirmPassword = edConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(fullName)) {
                    Toast.makeText(Register.this, "Please Enter Your Full Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(Register.this, "Please Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(Register.this, "Password Not matched", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        User user = new User(userId, fullName, email,"USD","Monthly","",false);
                                        databaseUsers
                                                .child(userId)
                                                .setValue(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(Register.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(Register.this, MainDashboard.class);
                                                        intent.putExtra("userID", userId);
                                                        progressBar.setVisibility(View.GONE);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });

                                    }else{
                                        Toast.makeText(Register.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });
    }

    private void init() {
        edFullName = findViewById(R.id.ed_FullName);
        edEmail = findViewById(R.id.ed_Email);
        edPassword = findViewById(R.id.ed_Password);
        edConfirmPassword = findViewById(R.id.ed_ConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnSignIn = findViewById(R.id.btnSignIn);

        progressBar=findViewById(R.id.progressBar);

        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

    }
}
