package com.example.travelbuddyv2.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.GroupTripDetailActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.TripDetailActivity;
import com.example.travelbuddyv2.model.tripModel;

import java.util.List;

public class GroupTripAdapter extends RecyclerView.Adapter<GroupTripAdapter.GroupTripHolder> {


    List<tripModel> groupTripList;
    AdapterCallback adapterCallback;


    public GroupTripAdapter(List<tripModel> lists,AdapterCallback adapterCallback) {
        this.groupTripList = lists;
        this.adapterCallback = adapterCallback;

    }

    @NonNull
    @Override
    public GroupTripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mytripview,parent,false);

        return new GroupTripHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupTripHolder holder, int position) {

        tripModel tmp = groupTripList.get(position);

        holder.tripname.setText(tmp.getTripName());
        holder.tripdate.setText(tmp.getStartDate() + " " + tmp.getEndDate());


    }

    @Override
    public int getItemCount() {
        return groupTripList.size();
    }

    class GroupTripHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView tripname;
        private final TextView tripdate ;
        private final Button btnInviteFriend;

        public GroupTripHolder(@NonNull View itemView) {
            super(itemView);
            tripname = itemView.findViewById(R.id.tripNameListAdapter);
            tripdate = itemView.findViewById(R.id.tripDateListAdapter);
            btnInviteFriend = itemView.findViewById(R.id.tripInviteFriend);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            tripModel tmp = groupTripList.get(getAdapterPosition());

            Intent i = new Intent(itemView.getContext(), GroupTripDetailActivity.class);
            i.putExtra("TRIP_STRING_ID",tmp.getStringID());
            i.putExtra("TRIP_OWNER",tmp.getOwner());
            itemView.getContext().startActivity(i);

         //   adapterCallback.onMethodCallback(getAdapterPosition());


        }
    }

    public interface AdapterCallback{
        void onMethodCallback(int position);
    }



}
