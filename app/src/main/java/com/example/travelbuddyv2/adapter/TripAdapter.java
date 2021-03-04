package com.example.travelbuddyv2.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.InviteFriendActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.TripDetailActivity;
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
    public void onBindViewHolder(@NonNull final tripHolder holder, final int position) {

        final tripModel currentTrip = list.get(position);

        holder.tripname.setText(currentTrip.getTripName());
        holder.tripdate.setText(currentTrip.getStartDate() + " " + currentTrip.getEndDate());

        holder.btnInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemView.getContext(),InviteFriendActivity.class);
                i.putExtra("TripName",currentTrip.getTripName());
                i.putExtra("TripStringID",currentTrip.getStringID());
                holder.itemView.getContext().startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<tripModel> lists){
        this.list = lists;
        notifyDataSetChanged();
    }




    class tripHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        private final TextView tripname;
        private final TextView tripdate ;
        private final Button btnInviteFriend;




        public tripHolder(@NonNull final View itemView) {
            super(itemView);
            tripname = itemView.findViewById(R.id.tripNameListAdapter);
            tripdate = itemView.findViewById(R.id.tripDateListAdapter);
            btnInviteFriend = itemView.findViewById(R.id.tripInviteFriend);

            itemView.setOnClickListener(this);



        }

        @Override
        public void onClick(View v) {
            tripModel tmp = list.get(getAdapterPosition());
         //   Toast.makeText(itemView.getContext(),tmp.getTripName(),Toast.LENGTH_SHORT).show();
            Intent i = new Intent(itemView.getContext(), TripDetailActivity.class);
            i.putExtra("TRIP_STRING_ID",tmp.getStringID());
            itemView.getContext().startActivity(i);
        }
    }

}
