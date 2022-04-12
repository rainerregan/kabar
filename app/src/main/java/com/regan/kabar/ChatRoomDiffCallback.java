package com.regan.kabar;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomDiffCallback extends DiffUtil.Callback {

    ArrayList<Chat> oldChatRooms;
    ArrayList<Chat> newChatRooms;

    public ChatRoomDiffCallback(ArrayList<Chat> oldChatRooms, ArrayList<Chat> newChatRooms) {
        this.oldChatRooms = oldChatRooms;
        this.newChatRooms = newChatRooms;
    }

    @Override
    public int getOldListSize() {
        return oldChatRooms.size();
    }

    @Override
    public int getNewListSize() {
        return newChatRooms.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldChatRooms == newChatRooms;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldChatRooms.get(oldItemPosition) == newChatRooms.get(newItemPosition);
    }
}
