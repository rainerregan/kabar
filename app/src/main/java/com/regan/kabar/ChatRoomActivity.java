package com.regan.kabar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {

    ImageView sendButton, attachmentButton, backButton;
    EditText chatMessageEditText;
    TextView chat_room_name;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference userDB, chatRoomDB, messagesForCurrentUserDB;

    String chat_ID;
    FirebaseUser currentUser;

    Chat currentChatRoom;

    User sender;

    // MessageList untuk Current User
    ArrayList<Message> messagesList = new ArrayList<>();

    // Recycler View
    RecyclerView recyclerView;
    ChatRoomProgramAdapter programAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        // Views
        sendButton = (ImageView) findViewById(R.id.chat_send_button);
        chatMessageEditText = (EditText) findViewById(R.id.chat_text_edit_text);
        backButton = (ImageView) findViewById(R.id.back_button4);
        sendButton.setClickable(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        chat_room_name = (TextView) findViewById(R.id.chat_room_name);

        chat_ID = getIntent().getStringExtra("CHAT_ID");

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        currentUser = mAuth.getCurrentUser();
        userDB = database.getReference("users");
        chatRoomDB = database.getReference("chat_rooms");
        messagesForCurrentUserDB = database.getReference("chat_rooms")
                .child(chat_ID).child("messages").child(currentUser.getUid());

        // Layout Manager & Recycler View
        recyclerView = findViewById(R.id.chat_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Mendapatkan Chat Yang Terupdate
        getUpdatedChat(chat_ID);

        // Populate chat arraylist
        populateChatArrayList();

        // Set clicklistener for send button
        // Kirim pesan
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatMessageEditText.getText().toString();

                chatMessageEditText.setText("");
//                recyclerView.smoothScrollToPosition(messagesList.size() - 1);

                userDB.child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        sender = task.getResult().getValue(User.class);

                        String messageId = chatRoomDB.push().getKey();

                        Message messageClass = new Message();
                        messageClass.setGroup(false);
                        messageClass.setSender(sender);
                        messageClass.setRoom_id(currentChatRoom.getRoom_id());
                        messageClass.setText(message);
                        messageClass.setMessage_id(messageId);

                        for(String uid : currentChatRoom.getParticipants()){
                            chatRoomDB.child(currentChatRoom.getRoom_id()).child("messages")
                                    .child(uid).child(messageId)
                                    .setValue(messageClass);
                            chatRoomDB.child(currentChatRoom.getRoom_id()).child("messages")
                                    .child(uid).child(messageId)
                                    .child("timestamp").setValue(ServerValue.TIMESTAMP);
                        }

                        // Set Last Message
                        String last_message = message;
                        if(last_message.length() > 15){
                            last_message = last_message.substring(0, 15) + "...";
                        }
                        chatRoomDB.child(chat_ID).child("last_message").setValue(last_message);

                        // SET LAST TIMESTAMP
                        chatRoomDB.child(chat_ID).child("timestamp").setValue(ServerValue.TIMESTAMP);

                        // Add to recipients data
                        for (String uid : currentChatRoom.getParticipants()){
                            userDB.child(uid).child("chats").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    ArrayList<String> chat_list = (ArrayList<String>) task.getResult().getValue();
                                    if(!chat_list.contains(chat_ID)) {
                                        chat_list.add(chat_ID);
                                    }
                                    userDB.child(uid).child("chats").setValue(chat_list);
                                }
                            });
                        }

                    }
                });
            }
        });


    }

    /*
        TODO
        Benerin kode dibawah supaya chat bisa ditampilkan di chat room
     */
    private void populateChatArrayList() {

        messagesForCurrentUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    messagesList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Message message = data.getValue(Message.class);
                        messagesList.add(message);

//                    Log.i("Message Id", message.getMessage_id());
                    }

                    programAdapter = new ChatRoomProgramAdapter(ChatRoomActivity.this, messagesList);
                    recyclerView.setAdapter(programAdapter);

                    recyclerView.smoothScrollToPosition(messagesList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Khusus untuk update data chat room
    private void getUpdatedChat(String chat_ID) {
        chatRoomDB.child(chat_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currentChatRoom = snapshot.getValue(Chat.class);

                    // Mengubah nama room
                    for (String uid: currentChatRoom.getParticipants()){
                        if (!uid.equals(currentUser.getUid())){
                            userDB.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    User user = task.getResult().getValue(User.class);

                                    String room_name = user.getDisplay_name();
                                    chat_room_name.setText(room_name);
                                }
                            });
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}