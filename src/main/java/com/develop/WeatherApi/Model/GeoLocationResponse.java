package com.develop.WeatherApi.Model;

// Class to represent the city data with lat/lon and other details
public class GeoLocationResponse {
    private String name;
    private LocalNames local_names;
    private double lat;
    private double lon;
    private String country;
    private String state;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalNames getLocal_names() {
        return local_names;
    }

    public void setLocal_names(LocalNames local_names) {
        this.local_names = local_names;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
