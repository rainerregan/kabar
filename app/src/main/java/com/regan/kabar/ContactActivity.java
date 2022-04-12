package com.regan.kabar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    // Current User
    User currentUser;

    // Current User Contact List
    ArrayList<User> userContactList = new ArrayList<>();

    // Firebase
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference usersDatabaseReference;

    // View
    ImageView backButton;

    // Recycler View Things
    RecyclerView recyclerView;
    RecyclerView.Adapter programAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Set View
        backButton = (ImageView) findViewById(R.id.back_button3);
        backButton.setClickable(true);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference("users");

        // Layout Manager
        recyclerView = findViewById(R.id.contact_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Get Current User Contacts
        getUserContact();

    }

    private void getUserContact() {
        usersDatabaseReference.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userContactList.clear();
                        currentUser = snapshot.getValue(User.class);

                        // Get currentUser contacts into the arraylist
                        // For every uid in contact
                        for (String uid : currentUser.getContacts()){
                            Log.i("Friend_list", uid);

                            usersDatabaseReference.child(uid)
                                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    // Add to ArrayList of User
                                    User contact = task.getResult().getValue(User.class);
                                    userContactList.add(contact);

                                    // Update recyclerView
                                    // Program Adapter
                                    programAdapter = new ContactProgramAdapter(ContactActivity.this, userContactList, currentUser);
                                    recyclerView.setAdapter(programAdapter);
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}