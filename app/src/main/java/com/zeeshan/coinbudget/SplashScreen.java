package com.zeeshan.coinbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashScreen extends AppCompatActivity {

    TextView txtTitle;
    ImageView imgLogo;
    Intent intent;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        txtTitle = findViewById(R.id.txtTitle);
        imgLogo = findViewById(R.id.imgLogo);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_transition);
        txtTitle.startAnimation(animation);
        imgLogo.startAnimation(animation);

        if (firebaseUser != null) {
            intent = new Intent(SplashScreen.this, MainDashboard.class);
        } else {
            intent = new Intent(SplashScreen.this, Login.class);
        }
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}
