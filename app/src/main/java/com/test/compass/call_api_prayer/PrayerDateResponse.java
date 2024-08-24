package com.test.compass.call_api_prayer;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PrayerDateResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private PrayerDateData data;

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public PrayerDateData getData() {
        return data;
    }
}
