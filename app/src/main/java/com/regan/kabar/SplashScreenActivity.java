package com.regan.kabar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private int loadingTime = 2500;
    private FirebaseAuth mAuth;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent targetIntent;

                /*
                    Check current user
                    Kalau user tidak ada, akan dibawa ke welcome screen
                    Kalau ada, dibawa ke main activity
                 */
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser == null)
                    targetIntent = new Intent(SplashScreenActivity.this, WelcomeScreenActivity.class);
                else
                    targetIntent = new Intent(SplashScreenActivity.this, MainActivity.class);

                startActivity(targetIntent);
                finish();
            }
        }, loadingTime);
    }
}