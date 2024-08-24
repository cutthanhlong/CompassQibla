package com.test.compass.util;

public class TimeCustom {
    private String time;
    private String name_time;

    public TimeCustom(String time, String name_time) {
        this.time = time;
        this.name_time = name_time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName_time() {
        return name_time;
    }

    public void setName_time(String name_time) {
        this.name_time = name_time;
    }
}
