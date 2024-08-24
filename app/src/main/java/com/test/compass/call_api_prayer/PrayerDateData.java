package com.test.compass.call_api_prayer;

import com.google.gson.annotations.SerializedName;

public class PrayerDateData {
    @SerializedName("timings")
    private PrayerTimings prayerTimings;
    @SerializedName("date")
    private DateInfo dateInfo;
    @SerializedName("meta")
    private Meta meta;

    public PrayerDateData(PrayerTimings prayerTimings, DateInfo dateInfo, Meta meta) {
        this.prayerTimings = prayerTimings;
        this.dateInfo = dateInfo;
        this.meta = meta;
    }

    public PrayerTimings getPrayerTimings() {
        return prayerTimings;
    }

    public DateInfo getDateInfo() {
        return dateInfo;
    }

    public Meta getMeta() {
        return meta;
    }

    public class PrayerTimings {
        @SerializedName("Fajr")
        private String fajr;
        @SerializedName("Sunrise")
        private String sunrise;
        @SerializedName("Dhuhr")
        private String dhuhr;
        @SerializedName("Asr")
        private String asr;
        @SerializedName("Sunset")
        private String sunset;
        @SerializedName("Maghrib")
        private String maghrib;
        @SerializedName("Isha")
        private String isha;
        @SerializedName("Imsak")
        private String imsak;

        private String midnight;
        private String firstthird;
        private String lastthird;

        public PrayerTimings(String fajr, String sunrise, String dhuhr, String asr, String sunset, String maghrib, String isha, String imsak, String midnight, String firstthird, String lastthird) {
            this.fajr = fajr;
            this.sunrise = sunrise;
            this.dhuhr = dhuhr;
            this.asr = asr;
            this.sunset = sunset;
            this.maghrib = maghrib;
            this.isha = isha;
            this.imsak = imsak;
            this.midnight = midnight;
            this.firstthird = firstthird;
            this.lastthird = lastthird;
        }

        public String getFajr() {
            return fajr;
        }

        public String getSunrise() {
            return sunrise;
        }

        public String getDhuhr() {
            return dhuhr;
        }

        public String getAsr() {
            return asr;
        }

        public String getSunset() {
            return sunset;
        }

        public String getMaghrib() {
            return maghrib;
        }

        public String getIsha() {
            return isha;
        }

        public String getImsak() {
            return imsak;
        }

        public String getMidnight() {
            return midnight;
        }

        public String getFirstthird() {
            return firstthird;
        }

        public String getLastthird() {
            return lastthird;
        }
    }

    public class DateInfo {
        @SerializedName("readable")
        private String readable;
        @SerializedName("timestamp")
        private String timestamp;
        @SerializedName("gregorian")
        private GregorianDate gregorian;
        @SerializedName("hijri")
        private HijriDate hijri;

        public DateInfo(String readable, String timestamp, GregorianDate gregorian, HijriDate hijri) {
            this.readable = readable;
            this.timestamp = timestamp;
            this.gregorian = gregorian;
            this.hijri = hijri;
        }

        public String getReadable() {
            return readable;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public GregorianDate getGregorian() {
            return gregorian;
        }

        public HijriDate getHijri() {
            return hijri;
        }
    }

    public class GregorianDate {
        @SerializedName("date")
        private String date;
        @SerializedName("format")
        private String format;
        @SerializedName("day")
        private String day;
        @SerializedName("weekday")
        private Weekday weekday;
        @SerializedName("month")
        private Month month;
        @SerializedName("year")
        private String year;
        @SerializedName("designation")
        private Designation designation;

        public GregorianDate(String date, String format, String day, Weekday weekday, Month month, String year, Designation designation) {
            this.date = date;
            this.format = format;
            this.day = day;
            this.weekday = weekday;
            this.month = month;
            this.year = year;
            this.designation = designation;
        }

        public String getDate() {
            return date;
        }

        public String getFormat() {
            return format;
        }

        public String getDay() {
            return day;
        }

        public Weekday getWeekday() {
            return weekday;
        }

        public Month getMonth() {
            return month;
        }

        public String getYear() {
            return year;
        }

        public Designation getDesignation() {
            return designation;
        }
    }

    public class HijriDate {
        @SerializedName("date")
        private String date;
        @SerializedName("format")
        private String format;
        @SerializedName("day")
        private String day;
        @SerializedName("weekday")
        private Weekday weekday;
        @SerializedName("month")
        private Month month;
        @SerializedName("year")
        private String year;
        @SerializedName("designation")
        private Designation designation;

        public HijriDate(String date, String format, String day, Weekday weekday, Month month, String year, Designation designation) {
            this.date = date;
            this.format = format;
            this.day = day;
            this.weekday = weekday;
            this.month = month;
            this.year = year;
            this.designation = designation;
        }

        public String getDate() {
            return date;
        }

        public String getFormat() {
            return format;
        }

        public String getDay() {
            return day;
        }

        public Weekday getWeekday() {
            return weekday;
        }

        public Month getMonth() {
            return month;
        }

        public String getYear() {
            return year;
        }

        public Designation getDesignation() {
            return designation;
        }
    }

    public class Weekday {
        @SerializedName("en")
        private String en;

        public Weekday(String en) {
            this.en = en;
        }

        public String getEn() {
            return en;
        }
    }

    public class Month {
        @SerializedName("number")
        private int number;
        @SerializedName("en")
        private String en;

        public Month(int number, String en) {
            this.number = number;
            this.en = en;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }
    }

    public class Designation {
        @SerializedName("abbreviated")
        private String abbreviated;
        @SerializedName("expanded")
        private String expanded;

        public Designation(String abbreviated, String expanded) {
            this.abbreviated = abbreviated;
            this.expanded = expanded;
        }

        public String getAbbreviated() {
            return abbreviated;
        }

        public String getExpanded() {
            return expanded;
        }
    }

    public class Meta {
        @SerializedName("latitude")
        private double latitude;
        @SerializedName("longitude")
        private double longitude;
        @SerializedName("timezone")
        private String timezone;
        @SerializedName("method")
        private Method method;
        @SerializedName("latitudeAdjustmentMethod")
        private String latitudeAdjustmentMethod;
        @SerializedName("midnightMode")
        private String midnightMode;
        @SerializedName("school")
        private String school;
        @SerializedName("offset")
        private Offset offset;

        public Meta(double latitude, double longitude, String timezone, Method method, String latitudeAdjustmentMethod, String midnightMode, String school, Offset offset) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.timezone = timezone;
            this.method = method;
            this.latitudeAdjustmentMethod = latitudeAdjustmentMethod;
            this.midnightMode = midnightMode;
            this.school = school;
            this.offset = offset;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getTimezone() {
            return timezone;
        }

        public Method getMethod() {
            return method;
        }

        public String getLatitudeAdjustmentMethod() {
            return latitudeAdjustmentMethod;
        }

        public String getMidnightMode() {
            return midnightMode;
        }

        public String getSchool() {
            return school;
        }

        public Offset getOffset() {
            return offset;
        }
    }

    public class Method {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("params")
        private Params params;
        @SerializedName("location")
        private Location location;

        public Method(int id, String name, Params params, Location location) {
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

        public Params getParams() {
            return params;
        }

        public Location getLocation() {
            return location;
        }
    }

    public class Params {
        @SerializedName("Fajr")
        private String Fajr;
        @SerializedName("Isha")
        private String Isha;

        public Params(String fajr, String isha) {
            Fajr = fajr;
            Isha = isha;
        }

        public String getFajr() {
            return Fajr;
        }

        public String getIsha() {
            return Isha;
        }
    }

    public class Location {
        @SerializedName("latitude")
        private double latitude;
        @SerializedName("longitude")
        private double longitude;

        public Location(double latitude, double longitude) {
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

    public class Offset {
        @SerializedName("Imsak")
        private int Imsak;
        @SerializedName("Fajr")
        private int Fajr;
        @SerializedName("Sunrise")
        private int Sunrise;
        @SerializedName("Dhuhr")
        private int Dhuhr;
        @SerializedName("Asr")
        private int Asr;
        @SerializedName("Maghrib")
        private int Maghrib;
        @SerializedName("Sunset")
        private int Sunset;
        @SerializedName("Isha")
        private int Isha;
        @SerializedName("Midnight")
        private int Midnight;

        public Offset(int imsak, int fajr, int sunrise, int dhuhr, int asr, int maghrib, int sunset, int isha, int midnight) {
            Imsak = imsak;
            Fajr = fajr;
            Sunrise = sunrise;
            Dhuhr = dhuhr;
            Asr = asr;
            Maghrib = maghrib;
            Sunset = sunset;
            Isha = isha;
            Midnight = midnight;
        }

        public int getImsak() {
            return Imsak;
        }

        public int getFajr() {
            return Fajr;
        }

        public int getSunrise() {
            return Sunrise;
        }

        public int getDhuhr() {
            return Dhuhr;
        }

        public int getAsr() {
            return Asr;
        }

        public int getMaghrib() {
            return Maghrib;
        }

        public int getSunset() {
            return Sunset;
        }

        public int getIsha() {
            return Isha;
        }

        public int getMidnight() {
            return Midnight;
        }
    }

}
