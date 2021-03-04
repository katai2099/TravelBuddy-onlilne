package com.example.travelbuddyv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Destination;

import java.util.List;

public class ChildGroupTripDetailAdapter extends RecyclerView.Adapter<ChildGroupTripDetailAdapter.ChildGroupTripDetailHolder> {

    List<Destination> destinations;

    public ChildGroupTripDetailAdapter(List<Destination> destinations) {
        this.destinations = destinations;

    }

    @NonNull
    @Override
    public ChildGroupTripDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row,parent,false);
        return new ChildGroupTripDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildGroupTripDetailHolder holder, int position) {
        holder.itemTextView.setText(destinations.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    class ChildGroupTripDetailHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        public ChildGroupTripDetailHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
        }
    }


}
