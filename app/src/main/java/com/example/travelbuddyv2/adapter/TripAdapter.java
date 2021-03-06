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

                Intent i = new Intent(holder.itemView.getContext(), MemberActivity.class);
                i.putExtra("TripName",currentTrip.getTripName());
                i.putExtra("TripStringID",currentTrip.getStringID());
                i.putExtra("fromWho","personalTrip");
                i.putExtra("TripOwnerID",currentTrip.getOwner());
                holder.itemView.getContext().startActivity(i);

              /*  Intent i = new Intent(holder.itemView.getContext(),InviteFriendActivity.class);
                i.putExtra("TripName",currentTrip.getTripName());
                i.putExtra("TripStringID",currentTrip.getStringID());
                holder.itemView.getContext().startActivity(i);*/

            }
        });

        holder.btnDeleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure you want to delete this trip?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTrip(currentTrip);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(holder.itemView.getContext(),"Nah, I am not leaving",Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

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
            tripModel tmp = list.get(getAdapterPosition());
         //   Toast.makeText(itemView.getContext(),tmp.getTripName(),Toast.LENGTH_SHORT).show();
            Intent i = new Intent(itemView.getContext(), TripDetailActivity.class);
            i.putExtra("TRIP_STRING_ID",tmp.getStringID());
            itemView.getContext().startActivity(i);
        }
    }

    private void deleteTrip(final tripModel currentTrip){

        String userUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference memberNodeReference = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(userUUID)
                .child(currentTrip.getStringID());

        DatabaseReference tripDetailNodeReference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(userUUID)
                .child(currentTrip.getStringID());

        final DatabaseReference tripNodeReference = FirebaseDatabase.getInstance().getReference().child("Trips")
                .child(userUUID)
                .child(currentTrip.getStringID());
        
        final DatabaseReference groupNodeReference = FirebaseDatabase.getInstance().getReference().child("Group");

        groupNodeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot first : snapshot.getChildren()){

                    for (DataSnapshot data : first.getChildren()) {
                        if (data.getKey().equals(currentTrip.getOwner())) {
                            for (DataSnapshot trip : data.getChildren()) {
                                if (trip.getKey().equals(currentTrip.getStringID()))
                                    trip.getRef().removeValue();
                            }
                        }
                    }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        tripDetailNodeReference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                memberNodeReference.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        tripNodeReference.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Log.d(tag,"DONE DELETING GROUP");
                            }
                        });
                    }
                });
            }
        });

    }

}
