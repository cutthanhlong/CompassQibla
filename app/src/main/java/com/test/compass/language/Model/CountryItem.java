package com.test.compass.language.Model;

public class CountryItem {

    private String countryName;
    private String cityName;
    private int flagImage;


    public CountryItem(String countryName) {
        this.countryName = countryName;
    }

    public CountryItem(int flagImage, String countryName, String cityName) {
        this.flagImage = flagImage;
        this.countryName = countryName;
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getFlagImage() {
        return flagImage;
    }

    public void setFlagImage(int flagImage) {
        this.flagImage = flagImage;
    }

}
