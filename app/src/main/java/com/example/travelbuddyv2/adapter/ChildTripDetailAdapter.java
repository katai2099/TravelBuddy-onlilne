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
import java.util.zip.Inflater;

public class ChildTripDetailAdapter extends RecyclerView.Adapter<ChildTripDetailAdapter.ChildTripDetailHolder> {

    List<Destination> destinations;

    public ChildTripDetailAdapter(List<Destination> destinationList) {
        this.destinations = destinationList;
    }

    @NonNull
    @Override
    public ChildTripDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row,parent,false);

        return new ChildTripDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildTripDetailHolder holder, int position) {
        holder.itemTextView.setText(destinations.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    class ChildTripDetailHolder extends RecyclerView.ViewHolder {

        TextView itemTextView;

        public ChildTripDetailHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
        }
    }

}
