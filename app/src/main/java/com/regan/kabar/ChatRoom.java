package com.regan.kabar;

import android.net.Uri;

import java.util.ArrayList;

public class ChatRoom {
    String roomId;
    String roomName;
    ArrayList<String> participantsUID = new ArrayList<String>();
    String lastMessage;
    String lastMessageTime;
    Uri chatPictureUri;

    public ChatRoom(String roomId, String roomName, ArrayList<String> participantsUID, String lastMessage, String lastMessageTime) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.participantsUID = participantsUID;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public Uri getChatPictureUri() {
        return chatPictureUri;
    }

    public void setChatPictureUri(Uri chatPictureUri) {
        this.chatPictureUri = chatPictureUri;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public ArrayList<String> getParticipantsUID() {
        return participantsUID;
    }

    public void setParticipantsUID(ArrayList<String> participantsUID) {
        this.participantsUID = participantsUID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
