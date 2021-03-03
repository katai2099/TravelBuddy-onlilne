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
import com.example.travelbuddyv2.model.tripModel;

import java.util.List;

public class TripSelectionAdapter extends RecyclerView.Adapter<TripSelectionAdapter.TripListHolder>{

    List<tripModel> tripModelList;

    public TripSelectionAdapter(List<tripModel> tripModelList) {
        this.tripModelList = tripModelList;
    }

    @NonNull
    @Override
    public TripListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_row,parent,false);

        return new TripListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripListHolder holder, int position) {

        final tripModel trip = tripModelList.get(position);

        holder.textView.setText(trip.getTripName());

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), DateSelectionActivity.class);
                i.putExtra("tripStringId",trip.getStringID());
                v.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tripModelList.size();
    }

    class TripListHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        public TripListHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvBasicRow);
        }
    }

}
