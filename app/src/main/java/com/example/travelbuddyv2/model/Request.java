package com.example.travelbuddyv2.model;

import androidx.annotation.NonNull;

public class Request {

    private String requestType;
    private String tripName;
    private String tripID;
    private String inviter;

    public Request() {
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    @NonNull
    @Override
    public String toString() {
        return requestType + " " + tripName + " " + tripID ;
    }
}
