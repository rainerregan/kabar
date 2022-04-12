package com.regan.kabar.comparators;

import com.regan.kabar.Chat;

import java.util.Comparator;

public class TimestampComparator implements Comparator<Chat> {

    @Override
    public int compare(Chat c1, Chat c2) {
        return Long.compare(c2.getTimestamp(), c1.getTimestamp());
    }
}
