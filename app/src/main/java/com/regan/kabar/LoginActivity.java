package com.regan.kabar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView registerLink, errorMessage;

    EditText emailEditText, passwordEditText;
    Button loginButton;

    private FirebaseAuth mAuth;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerLink = (TextView) findViewById(R.id.register_textview_link);
        emailEditText = (EditText) findViewById(R.id.emailEditTextLogin);
        passwordEditText = (EditText) findViewById(R.id.passwordEditTextLogin);

        errorMessage = (TextView) findViewById(R.id.error_display_text_login);

        loginButton = (Button) findViewById(R.id.loginButton);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                errorMessage.setVisibility(View.INVISIBLE);

                // Melakukan sign in
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("SIGNIN", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "Welcome, " + user.getDisplayName() + " Login SUCCESS!. Redirecting...",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("SIGNIN", "signInWithEmail:failure", task.getException());
                                    String status = "Login failed. Check your email or password";
                                    Toast.makeText(LoginActivity.this, status,
                                            Toast.LENGTH_LONG).show();
                                    errorMessage.setText(status);
                                    errorMessage.setVisibility(View.VISIBLE);
                                    passwordEditText.setText("");
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}