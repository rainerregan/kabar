package com.regan.kabar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFunctions {

    public String convertLongToHours(long time){
        Date date = new Date(time);
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm",
                Locale.getDefault());
        String time_formatted = sfd.format(date);

        return time_formatted;
    }

}
