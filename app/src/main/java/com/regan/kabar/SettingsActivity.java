package com.regan.kabar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference userDBReference;

    ImageView back_button;
    TextView logout_text_button;

    TextView usernameText, emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        usernameText = (TextView) findViewById(R.id.settings_username_text);
        emailText = (TextView) findViewById(R.id.settings_email_text);

        back_button = (ImageView) findViewById(R.id.back_button);
        logout_text_button = (TextView) findViewById(R.id.logout_text);
        back_button.setClickable(true);
        logout_text_button.setClickable(true);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logout_text_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                startActivity(new Intent(SettingsActivity.this, SplashScreenActivity.class));
                finish();
            }
        });

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userDBReference = database.getReference("users");

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Set Text
        userDBReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usernameText.setText(snapshot.child("username").getValue().toString());
                emailText.setText(currentUser.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

}