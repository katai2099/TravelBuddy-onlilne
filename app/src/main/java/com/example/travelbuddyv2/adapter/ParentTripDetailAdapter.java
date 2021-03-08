package com.example.travelbuddyv2.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.MapsActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.TripSection;

import java.util.ArrayList;
import java.util.List;

public class ParentTripDetailAdapter extends RecyclerView.Adapter<ParentTripDetailAdapter.ParentTripDetailHolder> {

    List<TripSection> tripSectionList;
    String tripStringId ;
    ChildTripDetailAdapter.ChildTripDetailAdapterCallBack childTripDetailAdapterCallBack;

    public ParentTripDetailAdapter(List<TripSection> tripSectionsList, String tripStringId,
                                   ChildTripDetailAdapter.ChildTripDetailAdapterCallBack childTripDetailAdapterCallBack) {
        this.tripSectionList = tripSectionsList;
        this.tripStringId = tripStringId;
        this.childTripDetailAdapterCallBack = childTripDetailAdapterCallBack;
    }

    @NonNull
    @Override
    public ParentTripDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_row,parent,false);
        return new ParentTripDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ParentTripDetailHolder holder, int position) {

        TripSection tripSection = tripSectionList.get(position);
        final String date = tripSection.getDate();
        String positionString = String.valueOf(position+1);
        holder.sectionTextview.setText("Day " + positionString + ":   "+ date);

        List<Destination> destinationList = tripSection.getDestinations();
        ChildTripDetailAdapter childTripDetailAdapter = new ChildTripDetailAdapter(destinationList,childTripDetailAdapterCallBack);
        holder.childRecyclerView.setAdapter(childTripDetailAdapter);

        holder.btnAddTripDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemView.getContext(), MapsActivity.class);
                i.putExtra("tripStringID",tripStringId);
                i.putExtra("dateOfTrip",date);
                holder.itemView.getContext().startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return tripSectionList.size();
    }

    class ParentTripDetailHolder extends RecyclerView.ViewHolder {

        TextView sectionTextview;
        RecyclerView childRecyclerView;
        Button btnAddTripDetail;

        public ParentTripDetailHolder(@NonNull View itemView) {
            super(itemView);
            sectionTextview = itemView.findViewById(R.id.sectionNameTextView);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
            btnAddTripDetail = itemView.findViewById(R.id.section_row_btnAddTripDetail);
        }
    }


}
