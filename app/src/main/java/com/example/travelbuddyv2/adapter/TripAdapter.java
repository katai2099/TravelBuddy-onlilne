package com.example.travelbuddyv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.tripModel;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.tripHolder> {

    List<tripModel> list;

    public TripAdapter(List<tripModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public tripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mytripview,parent,false);
        return new tripHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull tripHolder holder, int position) {

        tripModel currentTrip = list.get(position);

        holder.tripname.setText(currentTrip.getTripName());
        holder.tripdate.setText(currentTrip.getStartDate() + " " + currentTrip.getEndDate());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<tripModel> lists){
        this.list = lists;
        notifyDataSetChanged();
    }

    class tripHolder extends RecyclerView.ViewHolder{

        private TextView tripname , tripdate ;


        public tripHolder(@NonNull View itemView) {
            super(itemView);
            tripname = itemView.findViewById(R.id.tripNameListAdapter);
            tripdate = itemView.findViewById(R.id.tripDateListAdapter);
        }
    }

}
