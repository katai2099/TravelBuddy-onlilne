package com.example.travelbuddyv2.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Request;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {

    private final String tag = "REQUEST_ADAPTER";
    List<Request> requestList;

    public RequestAdapter(List<Request>requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_row,parent,false);

        return new RequestHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestHolder holder, int position) {

        final Request request = requestList.get(position);
        holder.tvFriendRequestNotification.setText("You got an invitation from "+ request.getInviter() + " to join " + request.getTripName() +"  trip");

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get inviter node
                DatabaseReference inviterReference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                        .child(request.getInviter());
                //get firebase Group current_user node
                final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Group")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(request.getInviter());

                inviterReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userReference.setValue(snapshot.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(holder.itemView.getContext(),"Add to group",Toast.LENGTH_SHORT).show();

                                DatabaseReference receiverRequest = FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(request.getInviter())
                                        .child(request.getRequestID());

                               receiverRequest.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       Log.d(tag,request.getRequestID() );

                                       DatabaseReference inviterRequest = FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                                               .child(request.getInviter())
                                               .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


                                       inviterRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                                               Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                                               while(items.hasNext()){
                                                   DataSnapshot item = items.next();
                                                   Request tmp = item.getValue(Request.class);
                                                   if(tmp.getInviter().equals(request.getInviter()) && tmp.getRequestType().equals("sent")
                                                           && tmp.getTripID().equals(request.getTripID()) && tmp.getTripName().equals(request.getTripName()))
                                                   {
                                                       Log.d(tag,"FIND EQUAL ONE");
                                                       item.getRef().removeValue();
                                                       break;
                                                   }
                                                   Log.d(tag,item.getRef().toString());

                                               }

                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError error) {

                                           }
                                       });

                                   }
                               });




                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    class RequestHolder extends RecyclerView.ViewHolder {

        private final TextView tvFriendRequestNotification;
        private final Button btnReject , btnAccept;

        public RequestHolder(@NonNull View itemView) {
            super(itemView);
            tvFriendRequestNotification = itemView.findViewById(R.id.tvfriendRequestNotification);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnAccept = itemView.findViewById(R.id.btnAccept);

        }
    }

}
