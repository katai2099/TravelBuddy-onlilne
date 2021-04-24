package com.example.travelbuddyv2.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.Helper;
import com.example.travelbuddyv2.MapsActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.TripSection;

import java.util.List;

public class ParentTripDetailAdapter extends RecyclerView.Adapter<ParentTripDetailAdapter.ParentTripDetailHolder> {

    List<TripSection> tripSectionList;
    String tripStringId ;
    ParentTripDetailAdapterCallBack parentTripDetailAdapterCallBack;
    ChildTripDetailAdapter.ChildTripDetailAdapterCallBack childTripDetailAdapterCallBack;

    public ParentTripDetailAdapter(List<TripSection> tripSectionsList, String tripStringId,
                                   ChildTripDetailAdapter.ChildTripDetailAdapterCallBack childTripDetailAdapterCallBack,
                                   ParentTripDetailAdapterCallBack parentTripDetailAdapterCallBack) {
        this.tripSectionList = tripSectionsList;
        this.tripStringId = tripStringId;
        this.childTripDetailAdapterCallBack = childTripDetailAdapterCallBack;
       this.parentTripDetailAdapterCallBack = parentTripDetailAdapterCallBack;
    }

    @NonNull
    @Override
    public ParentTripDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_row,parent,false);
        return new ParentTripDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ParentTripDetailHolder holder, final int position) {

        TripSection tripSection = tripSectionList.get(position);
        final String date = tripSection.getDate();
        String toShowDate = Helper.changeDateFormatSuitableForTripScreen(date);
        String positionString = String.valueOf(position+1);
        holder.sectionTextView.setText("Day " + positionString + ":   "+ toShowDate);
        if(tripSectionList.get(position).getDestinations().size()>0){
            holder.sectionStartTime.setVisibility(View.VISIBLE);
            holder.sectionStartTime.setText("Start : " + tripSectionList.get(position).getDestinations().get(0).getStartTime());
            holder.sectionStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentTripDetailAdapterCallBack.onStartTimeChangeClicked(position);
                }
            });
        }else{
            holder.sectionStartTime.setVisibility(View.GONE);
        }

        List<Destination> destinationList = tripSection.getDestinations();
        ChildTripDetailAdapter childTripDetailAdapter = new ChildTripDetailAdapter(destinationList,childTripDetailAdapterCallBack);
        holder.childRecyclerView.setAdapter(childTripDetailAdapter);

        holder.btnAddTripDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemView.getContext(), MapsActivity.class);
                i.putExtra("tripStringID",tripStringId);
                i.putExtra("dateOfTrip",date);
                i.putExtra("isCurrentUserAMember",false);
                holder.itemView.getContext().startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return tripSectionList.size();
    }

    class ParentTripDetailHolder extends RecyclerView.ViewHolder {

        TextView sectionTextView,sectionStartTime;
        RecyclerView childRecyclerView;
        Button btnAddTripDetail;

        public ParentTripDetailHolder(@NonNull View itemView) {
            super(itemView);
            sectionTextView = itemView.findViewById(R.id.sectionNameTextView);
            sectionStartTime = itemView.findViewById(R.id.sectionStartTime);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
            btnAddTripDetail = itemView.findViewById(R.id.section_row_btnAddTripDetail);
        }
    }

    public interface ParentTripDetailAdapterCallBack{
        void onStartTimeChangeClicked(int position);
    }


}
