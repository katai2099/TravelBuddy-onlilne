package com.example.travelbuddyv2.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.GroupTripDetailActivity;
import com.example.travelbuddyv2.Main2Activity;
import com.example.travelbuddyv2.MemberActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.TripDetailActivity;
import com.example.travelbuddyv2.model.tripModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GroupTripAdapter extends RecyclerView.Adapter<GroupTripAdapter.GroupTripHolder> {


    private final String tag = "GROUPTRIPADAPTER";
    List<tripModel> groupTripList;
    GroupTripAdapterCallback groupTripadapterCallback;
    Activity activity ;


    public GroupTripAdapter(List<tripModel> lists,GroupTripAdapterCallback adapterCallback,Activity activity) {
        this.groupTripList = lists;
        this.groupTripadapterCallback = adapterCallback;
        this.activity = activity;

    }

    @NonNull
    @Override
    public GroupTripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mytripview,parent,false);

        return new GroupTripHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupTripHolder holder, final int position) {

        final tripModel currentTrip = groupTripList.get(position);

        holder.tripname.setText(currentTrip.getTripName());
        holder.tripdate.setText(currentTrip.getStartDate() + " " + currentTrip.getEndDate());

        holder.btnInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemView.getContext(), MemberActivity.class);
                i.putExtra("TripName",currentTrip.getTripName());
                i.putExtra("TripStringID",currentTrip.getStringID());
                i.putExtra("fromWho","groupTrip");
                i.putExtra("TripOwnerID",currentTrip.getOwner());
                holder.itemView.getContext().startActivity(i);
            }
        });

        holder.btnLeaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                groupTripadapterCallback.onLeaveGroupClicked(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return groupTripList.size();
    }

    class GroupTripHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView tripname;
        private final TextView tripdate ;
        private final Button btnInviteFriend, btnLeaveGroup;

        public GroupTripHolder(@NonNull View itemView) {
            super(itemView);
            tripname = itemView.findViewById(R.id.tripNameListAdapter);
            tripdate = itemView.findViewById(R.id.tripDateListAdapter);
            btnInviteFriend = itemView.findViewById(R.id.tripInviteFriend);
            btnLeaveGroup = itemView.findViewById(R.id.tripRemove);
            btnLeaveGroup.setBackgroundResource(R.drawable.ic_baseline_directions_run_24);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            tripModel tmp = groupTripList.get(getAdapterPosition());

            Intent i = new Intent(itemView.getContext(), GroupTripDetailActivity.class);
            i.putExtra("TRIP_STRING_ID",tmp.getStringID());
            i.putExtra("TRIP_OWNER",tmp.getOwner());
            i.putExtra("isPersonal",false);
            itemView.getContext().startActivity(i);

         //   adapterCallback.onMethodCallback(getAdapterPosition());


        }
    }

    public interface GroupTripAdapterCallback{
        void onLeaveGroupClicked(int position);
    }







}
