package com.example.travelbuddyv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
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
            //    childTripDetailAdapterCallBack.onDeleteDestinationClick(destinations.get(position).getStartDate(),destinations.get(position).getDestinationStringID());
                childTripDetailAdapterCallBack.onDeleteDestinationClicked(destinations.get(position).getStartDate(),destinations.get(position).getDestinationStringID(),position);
            }
        });

        String midNight = "00:00" ;
        String endTime = destinations.get(position).getEndTime();
        int extra = destinations.get(position).getExtraDay();

        if(extra!=1 && endTime.equals(midNight)){
            holder.extraDay.setText("+ " + (extra - 1));
        }else if(extra !=0 && !(endTime.equals(midNight))){
            holder.extraDay.setText("+ " + (extra));
        }

        holder.editDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curDate = destinations.get(position).getStartDate();
                String startTime = destinations.get(position).getStartTime();

                childTripDetailAdapterCallBack.onDurationEditingClicked(position,curDate);
            }
        });

    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    class ChildTripDetailHolder extends RecyclerView.ViewHolder {

        TextView itemTextView , startTime , endTime , extraDay;
        Button btnRemoveDestination;
        View editDuration ;

        public ChildTripDetailHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            startTime = itemView.findViewById(R.id.tvStartTime);
            endTime = itemView.findViewById(R.id.tvEndTime);
            extraDay = itemView.findViewById(R.id.tvExtraDay);
            btnRemoveDestination = itemView.findViewById(R.id.btnDeleteDestination);
            editDuration = itemView.findViewById(R.id.layoutEditDuration);
        }
    }

    public interface ChildTripDetailAdapterCallBack{
        void onDeleteDestinationClick(String date,String destinationStringID);
        void onDeleteDestinationClicked(String date,String destinationStringID,int position);
        void onDurationEditingClicked(int position,String curDate);
    }


}
