package com.regan.kabar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeScreenActivity extends AppCompatActivity {

    TextView loginLink;
    Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        loginLink = (TextView) findViewById(R.id.login_link_text);
        joinButton = (Button) findViewById(R.id.join_link_button);

        // On Click Listener buat login link
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(WelcomeScreenActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        // On Click Listener buat join button
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(WelcomeScreenActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });
    }
}