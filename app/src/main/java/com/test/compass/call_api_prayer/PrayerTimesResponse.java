package com.test.compass.call_api_prayer;

import com.google.gson.annotations.SerializedName;

public class PrayerTimesResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private PrayerTimesData data;

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public PrayerTimesData getData() {
        return data;
    }
}
