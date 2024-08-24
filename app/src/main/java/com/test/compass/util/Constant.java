package com.test.compass.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Constant {
    public static String IS_QIBLA = "IS_QIBLA";
    public static String IS_VIBRATE = "IS_VIBRATE";
    public static String THEME_HOME_SELECT = "THEME_HOME_SELECT";
    public static String CONSENT_CHECK = "CONSENT_CHECK";

    public static String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(currentDate);
    }

    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static String getTimeZone() {
        TimeZone timeZone = TimeZone.getDefault();
        Date currentDate = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        timeFormat.setTimeZone(timeZone);
        return timeFormat.format(currentDate);
    }
}
