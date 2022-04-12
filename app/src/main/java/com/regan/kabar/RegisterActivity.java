package com.regan.kabar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    EditText nameEditText, usernameEditText, emailEditText, passwordEditText, passwordRetypeEditText;
    String name, username, email, password, retypePassword;

    TextView loginLink, errorMessageText;
    Button registerButton;

    String error_message;

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference userReferenceDB;
    DatabaseReference usernamesReferenceDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = (EditText) findViewById(R.id.nameEditTextRegister);
        usernameEditText = (EditText) findViewById(R.id.usernameEditTextRegister);
        emailEditText = (EditText) findViewById(R.id.emailEditTextRegister);
        passwordEditText = (EditText) findViewById(R.id.passwordEditTextRegister);
        passwordRetypeEditText = (EditText) findViewById(R.id.passwordRetypeEditTextRegister);

        loginLink = (TextView) findViewById(R.id.login_link_text_on_register);
        registerButton = (Button) findViewById(R.id.registerButton);
        errorMessageText = (TextView) findViewById(R.id.error_display_text);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userReferenceDB = database.getReference("users");
        usernamesReferenceDB = database.getReference("usernames_list");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString();
                username = usernameEditText.getText().toString();
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                retypePassword = passwordRetypeEditText.getText().toString();

                errorMessageText.setVisibility(View.INVISIBLE);

                // Check apakah password dan retype sama
                if(checkPasswordRetype(password, retypePassword)) {

                    // Chack apakah username sudah dibuat?
                    usernamesReferenceDB.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Jika username belum dibuat
                            if (!snapshot.exists()) {
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Log.d("SIGNUP", "createUserWithEmail:success");
                                                    Toast.makeText(RegisterActivity.this, "SIGN UP SUCCESS!.",
                                                            Toast.LENGTH_SHORT).show();
                                                    FirebaseUser currentUser = mAuth.getCurrentUser();

                                                    String profilePictureLink = "https://firebasestorage.googleapis.com/v0/b/kabar-messsaging-app.appspot.com/o/default_profile_picture.jpg?alt=media&token=adf2a3d3-a8c0-4870-b88d-b7e25751ff7e";

                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(name)
                                                            .setPhotoUri(Uri.parse(profilePictureLink))
                                                            .build();

                                                    currentUser.updateProfile(profileUpdates);

                                                    User newUser = new User(name, currentUser.getEmail(), username, profilePictureLink, currentUser.getUid());

                                                    userReferenceDB.child(currentUser.getUid()).setValue(newUser);

                                                    // Push data ke username lists
                                                    // username : userID
                                                    usernamesReferenceDB.child(username).setValue(currentUser.getUid());

                                                    startActivity(new Intent(RegisterActivity.this, SplashScreenActivity.class));
                                                    finish();
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Log.w("SIGNUP", "createUserWithEmail:failure", task.getException());
                                                    Toast.makeText(RegisterActivity.this, "Signup failed.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }else{
                                // Jika Username sudah dibuat
                                error_message = "Username already exist!";
                                errorMessageText.setText(error_message);
                                errorMessageText.setVisibility(View.VISIBLE);
                                passwordEditText.setText("");
                                passwordRetypeEditText.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    // Membuat akun di firebase
                }else{
                    // Jika password dan retype tidak sama
                    error_message = "Password doesn't match!";
                    errorMessageText.setText(error_message);
                    errorMessageText.setVisibility(View.VISIBLE);
                    passwordEditText.setText("");
                    passwordRetypeEditText.setText("");
                }
            }
        });

    }

    /*
        Mengecek 2 string, apakah sama atau tidak
     */
    private boolean checkPasswordRetype(String a, String b){
        return a.equals(b);
    }
}