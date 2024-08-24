package com.test.compass.call_api_prayer;

import com.google.gson.annotations.SerializedName;

public class PrayerTimesData {

/*  Muslim id:3 MWL Muslim World League
    Egytian id:5 EGYPT  Egyptian General ,Authority of Survey
    Umm AI - Qurra University id:4 MAKKAH  ,Umm Al-Qura University, Makkah
    Karachi Univ. of Islamic Sciences id:1 KARACHI  ,University of Islamic Sciences, Karachi
    Islamic Society of North America 2 ISNA , Islamic Society of North America
    Union of Islamic Org in France id:12 FRANCE ,Union Organization Islamic de France
    Kuwait Ministry of Awquaf id:9  KUWAIT, Kuwait*/
    @SerializedName("MWL")
    private MethodData MWL;
    @SerializedName("EGYPT")
    private MethodData EGYPT;
    @SerializedName("MAKKAH")
    private MethodData MAKKAH;
    @SerializedName("KARACHI")
    private MethodData KARACHI;
    @SerializedName("ISNA")
    private MethodData ISNA;
    @SerializedName("TEHRAN")
    private MethodData TEHRAN;
    @SerializedName("FRANCE")
    private MethodData FRANCE;
    @SerializedName("KUWAIT")
    private MethodData KUWAIT;

    public PrayerTimesData(MethodData MWL, MethodData EGYPT, MethodData MAKKAH, MethodData KARACHI, MethodData ISNA, MethodData TEHRAN, MethodData FRANCE, MethodData KUWAIT) {
        this.MWL = MWL;
        this.EGYPT = EGYPT;
        this.MAKKAH = MAKKAH;
        this.KARACHI = KARACHI;
        this.ISNA = ISNA;
        this.TEHRAN = TEHRAN;
        this.FRANCE = FRANCE;
        this.KUWAIT = KUWAIT;
    }

    public MethodData getMWL() {
        return MWL;
    }

    public MethodData getEGYPT() {
        return EGYPT;
    }

    public MethodData getMAKKAH() {
        return MAKKAH;
    }

    public MethodData getKARACHI() {
        return KARACHI;
    }

    public MethodData getISNA() {
        return ISNA;
    }

    public MethodData getTEHRAN() {
        return TEHRAN;
    }

    public MethodData getFRANCE() {
        return FRANCE;
    }

    public MethodData getKUWAIT() {
        return KUWAIT;
    }

    public class MethodData {

        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("params")
        private MethodParams params;

        @SerializedName("location")
        private LocationData location;

        public MethodData(int id, String name, MethodParams params, LocationData location) {
            this.id = id;
            this.name = name;
            this.params = params;
            this.location = location;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public MethodParams getParams() {
            return params;
        }

        public LocationData getLocation() {
            return location;
        }
    }

    public class MethodParams {

        @SerializedName("Fajr")
        private double fajr;

        @SerializedName("Isha")
        private Object isha;

        public MethodParams(double fajr, Object isha) {
            this.fajr = fajr;
            this.isha = isha;
        }

        public double getFajr() {
            return fajr;
        }

        public Object getIsha() {
            return isha;
        }
    }

    public class LocationData {

        @SerializedName("latitude")
        private double latitude;

        @SerializedName("longitude")
        private double longitude;

        public LocationData(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

}
