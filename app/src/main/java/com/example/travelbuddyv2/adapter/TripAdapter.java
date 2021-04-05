package com.example.travelbuddyv2.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.InviteFriendActivity;
import com.example.travelbuddyv2.MemberActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.TripDetailActivity;
import com.example.travelbuddyv2.model.tripModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.tripHolder> {

    List<tripModel> list;
    private final String tag = "TRIP_ADAPTER";
    private TripAdapterCallBack tripAdapterCallBack;

    public TripAdapter(List<tripModel> list,TripAdapterCallBack tripAdapterCallBack) {
        this.list = list;
        this.tripAdapterCallBack = tripAdapterCallBack;
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
               tripAdapterCallBack.onInviteFriendClicked(position);
            }
        });
        holder.btnDeleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripAdapterCallBack.onDeleteTripClicked(position);
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
        private final Button btnInviteFriend , btnDeleteTrip;
        public tripHolder(@NonNull final View itemView) {
            super(itemView);
            tripname = itemView.findViewById(R.id.tripNameListAdapter);
            tripdate = itemView.findViewById(R.id.tripDateListAdapter);
            btnInviteFriend = itemView.findViewById(R.id.tripInviteFriend);
            btnDeleteTrip = itemView.findViewById(R.id.tripRemove);
            btnDeleteTrip.setBackgroundResource(R.drawable.ic_baseline_delete_forever_24);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            tripAdapterCallBack.onTripClicked(getAdapterPosition());
        }
    }

    public interface TripAdapterCallBack{
        void onInviteFriendClicked(int position);
        void onDeleteTripClicked(int position);
        void onTripClicked(int position);
    }

}
