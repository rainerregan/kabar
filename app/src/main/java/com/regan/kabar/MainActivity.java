package com.regan.kabar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.regan.kabar.comparators.TimestampComparator;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference chatsDatabaseReference, userDatabaseReference;

    // For chat list recyclerview
    RecyclerView chatListRecyclerView;
    ChatDataProgramAdapter programAdapter;
    RecyclerView.LayoutManager layoutManager;

    // For Main Page DrawerLayout
    DrawerLayout mainDrawerLayout;
    TextView navigationMenuDisplayName, navigationMenuEmail, have_no_chat_text;
    ImageView navigationMenuProfileImage, addChatImageButton;

    NavigationView navigationView;
    View navigationHeaderView;

    // Memasukkan list chat kedalam arraylist
    ArrayList<Chat> chatRoomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        addChatImageButton = (ImageView) findViewById(R.id.add_chat_button);
        addChatImageButton.setClickable(true);

        // Databases
        database = FirebaseDatabase.getInstance();
        chatsDatabaseReference = database.getReference("chat_rooms");
        userDatabaseReference = database.getReference("users");

        // For chat list recycler view
        chatListRecyclerView = findViewById(R.id.chat_list);
        chatListRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        chatListRecyclerView.setLayoutManager(layoutManager);

        // Untuk men-list chat yang ada pada user
        populateArrayList();

        // For drawerLayout
        mainDrawerLayout = findViewById(R.id.drawerLayoutMain);
        findViewById(R.id.side_menu_bar_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Navigation Menu
        navigationView = (NavigationView) findViewById(R.id.navigationViewMain);
        navigationHeaderView = navigationView.getHeaderView(0);
        navigationMenuDisplayName = (TextView) navigationHeaderView.findViewById(R.id.navigation_bar_profile_display_name);
        navigationMenuEmail = (TextView) navigationHeaderView.findViewById(R.id.navigation_bar_profile_email);
        navigationMenuProfileImage = (ImageView) navigationHeaderView.findViewById(R.id.navigation_bar_profile_image);

        have_no_chat_text = (TextView) findViewById(R.id.have_no_chat_text);

        // Set sideMenu bar
        setNavigationViewListener();

        // Add Chat Button
        addChatImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
            }
        });

    }

    private void populateArrayList() {

//        programAdapter = new ChatDataProgramAdapter(MainActivity.this, chatRoomList);
//        chatListRecyclerView.setAdapter(programAdapter);

        userDatabaseReference.child(currentUser.getUid()).child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatRoomList.clear();

                // Jika sudah ada chat
                if(snapshot.exists()) {

                    // Membuat recycler view visible
                    chatListRecyclerView.setVisibility(View.VISIBLE);

                    // Membuat reminder invis
                    have_no_chat_text.setVisibility(View.INVISIBLE);

                    // Menangkap perubahan data jika ada room baru
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String id = data.getValue().toString();

                        chatsDatabaseReference.child(id).child("last_message").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Chat remove = null;

                                for (Chat c : chatRoomList){
                                    if (c.getRoom_id().equals(id)){
                                        remove = c;
                                    }
                                }
                                chatRoomList.remove(remove);

                                chatsDatabaseReference.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        Chat chatCurr = task.getResult().getValue(Chat.class);

//                                        Log.i("new_message", chatCurr.getLast_message());
                                        chatRoomList.add(chatCurr);

                                        Collections.sort(chatRoomList, new TimestampComparator());

                                        programAdapter = new ChatDataProgramAdapter(MainActivity.this, chatRoomList);
                                        chatListRecyclerView.setAdapter(programAdapter);

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }
                // Jika chat belum ada
                else{
                    // Membuat recycler view invis
                    chatListRecyclerView.setVisibility(View.INVISIBLE);

                    // Membuat reminder visible
                    have_no_chat_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setViewMenuBar() {
        navigationMenuDisplayName.setText(currentUser.getDisplayName());
        navigationMenuEmail.setText(currentUser.getEmail());
        Picasso.get().load(currentUser.getPhotoUrl()).into(navigationMenuProfileImage);
    }

    @Override
    protected void onStart() {
        // To check whether the user is signed in or not
        super.onStart();

        if (currentUser == null){
            startActivity(new Intent(MainActivity.this, SplashScreenActivity.class));
            finish();
        }

        // Set profile on side menu
        setViewMenuBar();
    }

    // Listener untuk drawer menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Mengecek apakah drawer di select
        switch (item.getItemId()){
            case R.id.menu_settings:{
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            }

            case R.id.menu_add_friend:{
                startActivity(new Intent(MainActivity.this, AddFriendActivity.class));
                break;
            }

            case R.id.menu_contact:{
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
            }
        }
        // Close drawer nav bar
        mainDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        navigationView.setNavigationItemSelectedListener(this);
    }
}