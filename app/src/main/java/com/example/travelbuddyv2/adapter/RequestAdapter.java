package com.example.travelbuddyv2.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Member;
import com.example.travelbuddyv2.model.Request;
import com.example.travelbuddyv2.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {

    private final String tag = "REQUEST_ADAPTER";
    List<Request> requestList;
    String name ;
    User inviter;
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
        getInviterName(request,holder);
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeInvitationRequest(request);
            }
        });
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get inviter node
                DatabaseReference inviterReference = FirebaseDatabase.getInstance().getReference().child("Trips")
                        .child(request.getInviter())
                        .child(request.getTripID());
                //get firebase Group current_user node
                final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Group")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(request.getInviter())
                        .child(request.getTripID());

                inviterReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userReference.setValue(snapshot.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(holder.itemView.getContext(),"Add to group",Toast.LENGTH_SHORT).show();
                                removeInvitationRequest(request);
                                addToMemberNode(request);
                                subscribeToUpcomingTripNotification(request);
                                addToKnownList();
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
     //   private final ImageView requesterProfileImage;
        private final CircleImageView requesterProfileImage;

        public RequestHolder(@NonNull View itemView) {
            super(itemView);
            tvFriendRequestNotification = itemView.findViewById(R.id.tvfriendRequestNotification);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            requesterProfileImage = itemView.findViewById(R.id.requesterImage);

        }
    }


    //remove InvitationRequest from firebase after user click on reject or accept button
    private void removeInvitationRequest(final Request request){
        // get receiver Request Node
        DatabaseReference receiverRequest = FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(request.getInviter())
                .child(request.getRequestID());
        //get inviter Request Node
        final DatabaseReference inviterRequest = FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                .child(request.getInviter())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        receiverRequest.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                inviterRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Request tmp = item.getValue(Request.class);
                            if (tmp.getInviter().equals(request.getInviter()) && tmp.getRequestType().equals("sent")
                                    && tmp.getTripID().equals(request.getTripID()) && tmp.getTripName().equals(request.getTripName())) {
                                Log.d(tag, "FIND EQUAL ONE");
                                item.getRef().removeValue();
                                break;
                            }
                            Log.d(tag, item.getRef().toString());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void addToMemberNode(Request request){
        //get Inviter Member Node
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(request.getInviter())
                .child(request.getTripID())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
       // String uniqueKey = reference.push().getKey();
        final Member tmp = new Member();
        tmp.setID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        tmp.setPermission("edit");
        DatabaseReference userNameReference = FirebaseDatabase.getInstance().getReference().child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =  snapshot.getValue(User.class);
                tmp.setName(user.getName());
                tmp.setEmail(user.getEmail());
                tmp.setProfileImg(user.getProfile_image());
                reference.setValue(tmp).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(tag,"ADD TO MEMBER NODE SUCCESS");
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void subscribeToUpcomingTripNotification(Request request){
        final DatabaseReference upcomingTripNotificationNode = FirebaseDatabase.getInstance().getReference().child("upcomingTripNotification")
                .child(inviter.getUser_id())
                .child(request.getTripID());
        DatabaseReference userNode = FirebaseDatabase.getInstance().getReference().child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userNode.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                upcomingTripNotificationNode.child(currentUser.getDeviceToken()).setValue("");
            }
        });
    }


    private void addToKnownList(){
        final DatabaseReference knownListNode = FirebaseDatabase.getInstance().getReference().child("Known_lists")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //check first if user is already in knownList
        knownListNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exist = false;
                for(DataSnapshot data:snapshot.getChildren()){
                    User tmp = data.getValue(User.class);
                    if(tmp.equals(inviter))
                    {
                        exist = true;
                        break;
                    }
                }
                //  Log.d(tag,String.valueOf(exist));
                if(!exist){
                    Log.d(tag,"Gonna add to knownList");
                    String randomKey = knownListNode.push().getKey();
                    knownListNode.child(randomKey).setValue(inviter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void getInviterName(final Request request, final RequestHolder holder){
        DatabaseReference userNode = FirebaseDatabase.getInstance().getReference().child("User")
                .child(request.getInviter());
        inviter = new User();
        userNode.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                inviter.setProfile_image(user.getProfile_image());
                inviter.setUser_id(user.getUser_id());
                inviter.setName(user.getName());
                inviter.setEmail(user.getEmail());
                holder.tvFriendRequestNotification.setText("You got an invitation from "+ user.getName() + " to join " + request.getTripName() +"  trip");
                if(user.getProfile_image()!=null && !(TextUtils.isEmpty(user.getProfile_image())) ){
                    Picasso.get().load(user.getProfile_image()).fit().into(holder.requesterProfileImage);
                }
            }
        });

    }


}
