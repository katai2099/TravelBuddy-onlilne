package com.example.travelbuddyv2;

public class tripModel {

    private int id;
    private int idforListDetail;
    private String tripName;
    private String startDate;
    private String currentDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String destination;

    //To Retrieve Date from database
    public tripModel(int id, String tripName, String startDate, String endDate) {
        this.id = id;
        this.tripName = tripName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //To Retrieve Date from database
    public tripModel(int id, String tripName, String currentDate, String startTime, String endTime, String Destination) {
        this.id = id;
        this.tripName = tripName;
        this.currentDate = currentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.destination = Destination;
    }

    //To insert into database



    //To insert into database
    public tripModel(String tripName, String startDate, String endDate) {
        this.tripName = tripName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //To insert into database

    public tripModel(String tripName, String currentDate, String startTime, String endTime, String location) {
        this.tripName = tripName;
        this.currentDate = currentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.destination = location;
    }

    @Override
    public String toString() {
        return "tripModel{" +
                "id=" + id +
                ", tripName='" + tripName + '\'' +
                ", startDate='" + startDate + '\'' +
                ", currentDate='" + currentDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }

    public int getIdforListDetail() {
        return idforListDetail;
    }

    public int getId() {
        return id;
    }

    public String getTripName() {
        return tripName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDestination() {
        return destination;
    }
}
