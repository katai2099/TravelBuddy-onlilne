package com.example.travelbuddyv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.TripSection;

import java.util.ArrayList;
import java.util.List;

public class ParentTripDetailAdapter extends RecyclerView.Adapter<ParentTripDetailAdapter.ParentTripDetailHolder> {

    List<TripSection> tripSectionList;
    public ParentTripDetailAdapter(List<TripSection> tripSectionsList) {
        this.tripSectionList = tripSectionsList;
    }

    @NonNull
    @Override
    public ParentTripDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_row,parent,false);
        return new ParentTripDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentTripDetailHolder holder, int position) {

        TripSection tripSection = tripSectionList.get(position);
        String date = tripSection.getDate();
        holder.sectionTextview.setText(date);

        List<Destination> destinationList = tripSection.getDestinations();
        ChildTripDetailAdapter childTripDetailAdapter = new ChildTripDetailAdapter(destinationList);
        holder.childRecyclerView.setAdapter(childTripDetailAdapter);

    }

    @Override
    public int getItemCount() {
        return tripSectionList.size();
    }

    class ParentTripDetailHolder extends RecyclerView.ViewHolder {

        TextView sectionTextview;
        RecyclerView childRecyclerView;

        public ParentTripDetailHolder(@NonNull View itemView) {
            super(itemView);
            sectionTextview = itemView.findViewById(R.id.sectionNameTextView);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
        }
    }


}
