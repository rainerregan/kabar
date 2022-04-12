package com.regan.kabar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatFunctions {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference userDB, chatDB;

    public ChatFunctions() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userDB = database.getReference("users");
        chatDB = database.getReference("chat_rooms");
    }

    public String generateRandomChatRoomId(){
        return chatDB.push().getKey();
    }

    public void createNewChat(Chat newChat, User currentUser, User userDestination){
        String chat_room_id = newChat.getRoom_id();

        // Add Current User
        newChat.getParticipants().add(mAuth.getCurrentUser().getUid());
        // Add dest user
        newChat.getParticipants().add(userDestination.getUid());

        newChat.setRoom_name(userDestination.getDisplay_name());
        newChat.setRoom_picture_url(userDestination.getProfile_picture_link());

        currentUser.getChats().add(chat_room_id);
        userDB.child(mAuth.getCurrentUser().getUid()).child("chats").setValue(currentUser.getChats());

        chatDB.child(chat_room_id).setValue(newChat);
    }

    public void openChatActivity(Context context, String chatId, ArrayList<String> chat_participants_uid){
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra("CHAT_ID", chatId);
        intent.putExtra("CHAT_PARTICIPANTS", chat_participants_uid);
        context.startActivity(intent);
    }
}
