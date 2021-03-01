package com.example.travelbuddyv2.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Request {

    private String requestType;
    private String tripName;
    private String tripID;
    private String inviter;
    private String requestID;

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

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestNumber) {
        this.requestID = requestNumber;
    }

    @NonNull
    @Override
    public String toString() {
        return requestType + " " + tripName + " " + tripID ;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof Request))
            return false;

        Request c = (Request) obj;

        return inviter.equals(c.getInviter()) && tripID.equals(c.getTripID()) && tripName.equals(c.getTripName()) && requestType.equals(c.getRequestType()) ;

    }
}
