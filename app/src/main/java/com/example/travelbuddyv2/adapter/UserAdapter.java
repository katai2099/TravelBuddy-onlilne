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
import com.example.travelbuddyv2.networkManager.NetworkObserver;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private final String tag="USER_ADAPTER";

    int requestID =0;

    List<User> users;
    String name, id ;
    boolean isAlreadyAMember;

    public UserAdapter(List<User> users,String name,String id) {
        this.users = users;
        this.name  = name;
        this.id = id;
        isAlreadyAMember = false;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_row,parent,false);

        return new UserHolder(itemView,name,id);

    }

    @Override
    public void onBindViewHolder(@NonNull final UserHolder holder, int position) {
        final User user = users.get(position);
        holder.tvFriendName.setText(user.getName());
        holder.tvFriendEmail.setText(user.getEmail());
        holder.imgFriendProfile.setImageResource(R.drawable.ic_baseline_person_24);
        holder.tvPending.setVisibility(View.GONE);
        holder.btnInviteFriend.setVisibility(View.VISIBLE);
        if(user.getProfile_image()!=null && !(TextUtils.isEmpty(user.getProfile_image()))){
            Picasso.get().load(user.getProfile_image()).fit().into(holder.imgFriendProfile);
            Log.d(tag,"Picasso called");
        }
        final Request requester = new Request();
        requester.setRequestType("sent");
        requester.setTripID(holder.id);
        requester.setTripName(holder.name);
        requester.setInviter(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final Request receiver = new Request();
        receiver.setRequestType("received");
        receiver.setTripID(holder.id);
        receiver.setTripName(holder.name);
        receiver.setInviter(FirebaseAuth.getInstance().getCurrentUser().getUid());
        checkIfAlreadyAMember(user);

        holder.btnInviteFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(tag,String.valueOf(isAlreadyAMember));
             if(!isAlreadyAMember)
                 addRequest(user,requester,receiver,holder);
             else{
                 holder.tvPending.setVisibility(View.VISIBLE);
                 holder.btnInviteFriend.setVisibility(View.GONE);
             }

            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {

        private final ImageView imgFriendProfile;
        private final TextView tvFriendEmail,tvFriendName , tvPending;
        private final Button btnInviteFriend;

        private final String name , id; // tripname , tripStringID

        public UserHolder(@NonNull View itemView,String name,String id) {
           super(itemView);
            this.name = name;
            this.id = id ;
           imgFriendProfile = itemView.findViewById(R.id.friendProfile);
           tvFriendEmail = itemView.findViewById(R.id.friendEmail);
           tvFriendName = itemView.findViewById(R.id.friendName);
           btnInviteFriend = itemView.findViewById(R.id.btnInviteFriend);
           tvPending = itemView.findViewById(R.id.tvPending);

        }
    }

    private void addToKnownList(final User user){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Known_lists")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exist = false;
                for(DataSnapshot data:snapshot.getChildren()){
                    User tmp = data.getValue(User.class);
                    if(tmp.equals(user))
                    {
                        exist = true;
                        break;
                    }
                }
              //  Log.d(tag,String.valueOf(exist));
                if(!exist){
                    Log.d(tag,"Gonna add to knownList");
                    String key = FirebaseDatabase.getInstance().getReference().child("Known_lists")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                    FirebaseDatabase.getInstance().getReference().child("Known_lists")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(key).setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void addRequest(final User user, final Request requester, final Request receiver, final UserHolder holder){
      //  List<Request> requests = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(user.getUser_id());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean exist = false;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Request tmp = data.getValue(Request.class);
                    if(tmp.equals(requester))
                    {
                        exist = true ;
                        break;
                    }
                }
          //      Log.d(tag,String.valueOf(exist));
                if(!exist)
                {

                    requestID = 0 ;
                    final List<Integer> idList = new ArrayList<>();

                    FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(user.getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                if(snapshot.exists()){
                                    Request request= snapshot.getValue(Request.class);
                                    idList.add(StringToInt(snapshot.getKey()));
                                }
                            }
                            if(idList.size()!=0){
                                Collections.sort(idList);
                                requestID = idList.get(idList.size()-1)+1;
                                requester.setRequestID("r"+requestID);

                            }else{
                       //         Log.d(tag,"LIST EMPTY");
                                requester.setRequestID("r"+requestID);
                            }

                        //    Log.d(tag, String.valueOf(requestID));

                            FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(user.getUser_id())
                                    .child("r"+requestID)
                                    .setValue(requester).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    requestID = 0 ;
                                    idList.clear();

                                    FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                                            .child(user.getUser_id())
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                        @Override
                                        public void onSuccess(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                                Request request= snapshot.getValue(Request.class);
                                                idList.add(StringToInt(snapshot.getKey()));
                                            }
                                            if(idList.size()!=0){
                                                Collections.sort(idList);
                                                requestID = idList.get(idList.size()-1)+1;
                                                receiver.setRequestID("r"+requestID);

                                            }else{
                                        //        Log.d(tag,"LIST EMPTY");
                                                receiver.setRequestID("r"+requestID);
                                            }

                                       //     Log.d(tag, String.valueOf(requestID));

                                            FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                                                    .child(user.getUser_id())
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("r"+requestID)
                                                    .setValue(receiver).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(holder.itemView.getContext(),"Sent success",Toast.LENGTH_SHORT).show();
                                                    //add to knownlist
                                                    addToNotificationNode(FirebaseAuth.getInstance().getCurrentUser().getUid(),user.getUser_id());
                                                    addToKnownList(user);
                                                    holder.tvPending.setVisibility(View.VISIBLE);
                                                    holder.btnInviteFriend.setVisibility(View.GONE);
                                                }
                                            });

                                        }
                                    });

                                }
                            });

                        }
                    });

                }else{
                   holder.btnInviteFriend.setVisibility(View.GONE);
                   holder.tvPending.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private int StringToInt(String ID){

        StringBuilder tmp = new StringBuilder();

        for(int i=1;i<ID.length();i++){
            tmp.append(ID.charAt(i));
        }

        return Integer.parseInt(tmp.toString());

    }

    private void checkIfAlreadyAMember(final User receiver){
        DatabaseReference memberNodeReference = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(id);


        memberNodeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot memberNode: snapshot.getChildren()){
                    Member member = memberNode.getValue(Member.class);
                    if(member.getID().equals(receiver.getUser_id()))
                    {
                        isAlreadyAMember = true;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToNotificationNode(String senderUUID,String receiverUUID){
        HashMap<String,String> invitationRequest = new HashMap<>();

        invitationRequest.put("from",senderUUID);
        invitationRequest.put("type","request");
        invitationRequest.put("tripName",name);

        DatabaseReference notificationNode = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationNode.child(receiverUUID).push().setValue(invitationRequest);

    }


}
