package com.example.travelbuddyv2.model;

public class Destination {

    private int ID;
    private String name;
    private String startTime;
    private String endTime;
    private String startDate;
    private String endDate;
    private double longitude;
    private double latitude;
    private String placeId;
    private String destinationStringID;

    public Destination() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getDestinationStringID() {
        return destinationStringID;
    }

    public void setDestinationStringID(String destinationStringID) {
        this.destinationStringID = destinationStringID;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "name='" + name + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", startDate='" + startDate + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", placeId='" + placeId + '\'' +
                ", destinationStringID='" + destinationStringID + '\'' +
                '}';
    }
}
