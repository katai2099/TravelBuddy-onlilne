package com.example.travelbuddyv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Destination;

import java.util.List;
import java.util.zip.Inflater;

public class ChildTripDetailAdapter extends RecyclerView.Adapter<ChildTripDetailAdapter.ChildTripDetailHolder> {

    List<Destination> destinations;
    ChildTripDetailAdapterCallBack childTripDetailAdapterCallBack;


    public ChildTripDetailAdapter(List<Destination> destinations, ChildTripDetailAdapterCallBack childTripDetailAdapterCallBack) {
        this.destinations = destinations;
        this.childTripDetailAdapterCallBack = childTripDetailAdapterCallBack;
    }

    @NonNull
    @Override
    public ChildTripDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row,parent,false);

        return new ChildTripDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildTripDetailHolder holder, final int position) {
        holder.itemTextView.setText(destinations.get(position).getName());
        holder.startTime.setText(destinations.get(position).getStartTime());
        holder.endTime.setText(destinations.get(position).getEndTime());

        holder.btnRemoveDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childTripDetailAdapterCallBack.onDeleteDestinationClick(destinations.get(position).getStartDate(),destinations.get(position).getDestinationStringID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    class ChildTripDetailHolder extends RecyclerView.ViewHolder {

        TextView itemTextView , startTime , endTime;
        Button btnRemoveDestination;

        public ChildTripDetailHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            startTime = itemView.findViewById(R.id.tvStartTime);
            endTime = itemView.findViewById(R.id.tvEndTime);
            btnRemoveDestination = itemView.findViewById(R.id.btnDeleteDestination);
        }
    }

    public interface ChildTripDetailAdapterCallBack{
        void onDeleteDestinationClick(String date,String destinationStringID);
    }


}
