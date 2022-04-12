package com.regan.kabar;

import java.util.ArrayList;

public class User {
    String display_name;
    String email;
    String username;
    String profile_picture_link;
    String uid;
    ArrayList<String> chats = new ArrayList<>();
    ArrayList<String> contacts = new ArrayList<>();

    public User() {
    }

    public User(String display_name, String email, String username, String profile_picture_link, String uid) {
        this.display_name = display_name;
        this.email = email;
        this.username = username;
        this.profile_picture_link = profile_picture_link;
        this.uid = uid;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_picture_link() {
        return profile_picture_link;
    }

    public void setProfile_picture_link(String profile_picture_link) {
        this.profile_picture_link = profile_picture_link;
    }

    public ArrayList<String> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getChats() {
        return chats;
    }

    public void setChats(ArrayList<String> chats) {
        this.chats = chats;
    }
}
