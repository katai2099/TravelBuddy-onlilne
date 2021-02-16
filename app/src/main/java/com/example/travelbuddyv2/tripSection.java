package com.example.travelbuddyv2;

import com.example.travelbuddyv2.model.tripModel;

import java.util.Collections;
import java.util.List;

public class tripSection {

    String date;
    List<tripModel> tripList;

    public tripSection(String date, List<tripModel> tripList) {
        this.date = date;
        this.tripList = tripList;
    }

    public String getDate() {
        return date;
    }

    public List<tripModel> getTripList() {
        return tripList;
    }

    public void sortTrip()
    {
        //Collections.sort(tripList,new tripModel.SortbystartTime());
    }

}


