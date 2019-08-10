package com.zeeshan.coinbudget;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zeeshan.coinbudget.model.User;

import java.util.Arrays;
import java.util.List;

public class SignIn extends AppCompatActivity {

    List<AuthUI.IdpConfig> providers;
    private static final int MY_REQUEST_CODE = 7117;
    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            startActivity(new Intent(SignIn.this, MainDashboard.class));
        } else {

            providers = Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build(), new AuthUI.IdpConfig.EmailBuilder().build());
            databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
            showSignInOptions();
        }
    }

    private void showSignInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme)
                .setLogo(R.drawable.ic_coinbudget)
                .build(), MY_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                String userId = firebaseUser.getUid();
                String fullName = firebaseUser.getDisplayName();
                String email = firebaseUser.getEmail();
                User user = new User(userId, fullName, email, "USD", "Monthly", "", false);
                databaseUsers.child(userId).setValue(user);
                startActivity(new Intent(SignIn.this, MainDashboard.class));
            }
        }
    }

}
