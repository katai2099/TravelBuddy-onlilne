package com.example.travelbuddyv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.tripModel;

import java.util.List;

public class TripDetailAdapter extends RecyclerView.Adapter<TripDetailAdapter.TripDetailHolder> {


    List<Destination> destinationList;

    public TripDetailAdapter(List<Destination> destinationList){
        this.destinationList = destinationList;
    }

    @NonNull
    @Override
    public TripDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row,parent,false);

        return new TripDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripDetailHolder holder, int position) {

        Destination destination = destinationList.get(position);
        holder. destinationName.setText( destination.getName());
        holder.StartDateEndDate.setText(destination.getStartDate() + " " + destination.getEndDate());

    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }

    public void setList(List<Destination> lists){
        this.destinationList = lists;
        notifyDataSetChanged();
    }

    class TripDetailHolder extends RecyclerView.ViewHolder{

        private TextView destinationName , StartDateEndDate;

        public TripDetailHolder(@NonNull View itemView) {

            super(itemView);

            destinationName = itemView.findViewById(R.id.destinationTripDetail);
            StartDateEndDate = itemView.findViewById(R.id.startTimeEndTimeTripDetail);


        }
    }


}
