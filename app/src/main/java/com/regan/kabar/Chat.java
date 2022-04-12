package com.regan.kabar;

import android.net.Uri;

import java.util.ArrayList;

public class Chat {
    String last_message;
    String last_message_time;
    ArrayList<String> participants = new ArrayList<>();
    String room_id;
    String room_name;
    String room_picture_url;
    long timestamp;

    public Chat() {
    }

    public Chat(String room_id) {
        this.room_id = room_id;
    }

    public Chat(String last_message, String last_message_time, String room_id, String room_name) {
        this.last_message = last_message;
        this.last_message_time = last_message_time;
        this.room_id = room_id;
        this.room_name = room_name;
        this.room_picture_url = "https://firebasestorage.googleapis.com/v0/b/kabar-messsaging-app.appspot.com/o/default_profile_picture.jpg?alt=media&token=adf2a3d3-a8c0-4870-b88d-b7e25751ff7e";

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRoom_picture_url() {
        return room_picture_url;
    }

    public void setRoom_picture_url(String room_picture_url) {
        this.room_picture_url = room_picture_url;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getLast_message_time() {
        return last_message_time;
    }

    public void setLast_message_time(String last_message_time) {
        this.last_message_time = last_message_time;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }
}
