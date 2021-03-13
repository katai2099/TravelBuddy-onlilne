package com.example.travelbuddyv2.adapter;

//This is meant to be used when user select want to add new trip Detail from MapFragment

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.DateSelectionActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.tripModel;

import java.util.List;

public class TripSelectionAdapter extends RecyclerView.Adapter<TripSelectionAdapter.TripListHolder>{

    List<tripModel> tripModelList;
    TripSelectionAdapterCallback tripSelectionAdapterCallback;

    public TripSelectionAdapter(List<tripModel> tripModelList,TripSelectionAdapterCallback tripSelectionAdapterCallback) {
        this.tripModelList = tripModelList;
        this.tripSelectionAdapterCallback = tripSelectionAdapterCallback;
    }

    @NonNull
    @Override
    public TripListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_row,parent,false);

        return new TripListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripListHolder holder, final int position) {

        final tripModel trip = tripModelList.get(position);

        holder.textView.setText(trip.getTripName());

    }

    @Override
    public int getItemCount() {
        return tripModelList.size();
    }

    class TripListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView textView;

        public TripListHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvBasicRow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            tripSelectionAdapterCallback.onTripClicked(getAdapterPosition());

        }
    }

    public interface TripSelectionAdapterCallback{
        void onTripClicked(int position);
    }

}
