package com.regan.kabar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AddFriendActivity extends AppCompatActivity {

    // Components
    ImageView backButton;
    Button searchButton, addFriendButton;
    EditText searchQueryEditText;

    // Result
    ConstraintLayout layoutResult;
    ImageView resultImage;
    TextView resultDisplayName, resultUsername, errorText;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference userReference;
    DatabaseReference usernameReference;

    User searchedUser;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        // TextView
        errorText = (TextView) findViewById(R.id.search_error_message);

        // Buttons
        searchButton = (Button) findViewById(R.id.search_friend_button);
        addFriendButton = (Button) findViewById(R.id.add_friend_button);

        // Edit Text
        searchQueryEditText = (EditText) findViewById(R.id.search_friend_edit_text);

        // Res
        resultImage = (ImageView) findViewById(R.id.result_profile_picture);
        resultDisplayName = (TextView) findViewById(R.id.result_display_name);
        resultUsername = (TextView) findViewById(R.id.result_username);

        backButton = (ImageView) findViewById(R.id.back_button2);
        backButton.setClickable(true);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layoutResult = findViewById(R.id.search_result_layout);
        layoutResult.setVisibility(View.INVISIBLE);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usernameReference = database.getReference("usernames_list");
        userReference = database.getReference("users");

        // Click listener for search
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutResult.setVisibility(View.INVISIBLE);
                errorText.setVisibility(View.INVISIBLE);
                String username = searchQueryEditText.getText().toString();

                // Close Keyboard
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                usernameReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            // Jika ketemu
                            String uid = snapshot.getValue().toString();
                            Log.i("SEARCH_USER", "Username: " + username + ", uid: " + uid);

                            userReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    searchedUser = snapshot.getValue(User.class);

                                    Picasso.get().load(searchedUser.getProfile_picture_link()).into(resultImage);
                                    resultDisplayName.setText(searchedUser.getDisplay_name());
                                    resultUsername.setText(searchedUser.getUsername());
                                    layoutResult.setVisibility(View.VISIBLE);

                                    // Jika klik add friend
                                    addFriendButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            // Memastikan jika yang dicari bukan kita
                                            if(!searchedUser.getUid().equals(mAuth.getCurrentUser().getUid())) {
                                                // Get current user data
                                                userReference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        currentUser = snapshot.getValue(User.class);

                                                        // Jika kontak belum pernah di add
                                                        if(!currentUser.getContacts().contains(searchedUser.getUid())) {
                                                            currentUser.getContacts().add(searchedUser.getUid());

                                                            // Set Value user kita
                                                            userReference.child(mAuth.getCurrentUser().getUid()).setValue(currentUser);

                                                            // Alert Dialog keterangan sukses
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendActivity.this);
                                                            builder.setMessage(R.string.success_contact_added)
                                                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    layoutResult.setVisibility(View.INVISIBLE);
                                                                }
                                                            });

                                                            AlertDialog dialog = builder.create();
                                                            dialog.show();
                                                        }else{
                                                            errorText.setText(R.string.error_friend_already_added);
                                                            errorText.setVisibility(View.VISIBLE);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }else{
                                                // Jika kita menambahkan diri sendiri
                                                errorText.setText(R.string.error_cannot_add_yourself);
                                                errorText.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else{
                            // Jika tidak ditemukan
//                            Log.i("SEARCH_USER", R.string.error_not_found);
                            Toast.makeText(getBaseContext(), R.string.error_not_found, Toast.LENGTH_SHORT).show();
                            errorText.setText(R.string.error_not_found);
                            errorText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}