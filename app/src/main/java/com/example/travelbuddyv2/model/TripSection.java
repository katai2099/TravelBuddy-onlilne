package com.example.travelbuddyv2.model;

import java.util.List;

public class TripSection {

    String date;
    List<Destination> destinations;

    public TripSection(String date, List<Destination> destinations) {
        this.date = date;
        this.destinations = destinations;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }
}
