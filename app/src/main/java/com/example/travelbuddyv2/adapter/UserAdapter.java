package com.example.travelbuddyv2.adapter;

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
import com.example.travelbuddyv2.model.Request;
import com.example.travelbuddyv2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private final String tag="USER_ADAPTER";

    int requestID =0;

    List<User> users;
    String name, id ;


    public UserAdapter(List<User> users,String name,String id) {
        this.users = users;
        this.name  = name;
        this.id = id;
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
        holder.img.setImageResource(holder.itemView.getResources().getIdentifier("@drawable/email_confirmmation",null,holder.itemView.getContext().getPackageName()));

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

        holder.btnInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag,"onClick called");
                requestID = 0 ;


                final List<Integer> idList = new ArrayList<>();
                int tmp;

                FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user.getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Request request= snapshot.getValue(Request.class);
                            idList.add(StringToInt(snapshot.getKey()));
                        }
                        if(idList.size()!=0){
                            Collections.sort(idList);
                            requestID = idList.get(idList.size()-1)+1;
                            requester.setRequestID("r"+requestID);

                        }else{
                            Log.d(tag,"LIST EMPTY");
                            requester.setRequestID("r"+requestID);
                        }

                        Log.d(tag, String.valueOf(requestID));

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
                                            Log.d(tag,"LIST EMPTY");
                                            receiver.setRequestID("r"+requestID);
                                        }

                                        Log.d(tag, String.valueOf(requestID));

                                        FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                                                .child(user.getUser_id())
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("r"+requestID)
                                                .setValue(receiver).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(holder.itemView.getContext(),"Sent success",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });

                            }
                        });

                    }
                });

              /*   FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user.getUser_id())
                         .child("r"+requestID)
                        .setValue(requester)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                requestID = 0 ;
                                 getRequestID(user.getUser_id(),FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Log.d(tag,"ID BEFORE RECEIVER SENT " + requestID);
                                FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                                        .child(user.getUser_id())
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("r"+requestID)
                                        .setValue(receiver)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(holder.itemView.getContext(),"Sent success",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });*/

            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {

        private final ImageView img;
        private final TextView tvFriendEmail,tvFriendName;
        private final Button btnInviteFriend;

        private final String name , id; // tripname , tripStringID

        public UserHolder(@NonNull View itemView,String name,String id) {
           super(itemView);
            this.name = name;
            this.id = id ;
           img = itemView.findViewById(R.id.friendProfile);
           tvFriendEmail = itemView.findViewById(R.id.friendEmail);
           tvFriendName = itemView.findViewById(R.id.friendName);
           btnInviteFriend = itemView.findViewById(R.id.btnInviteFriend);

        }
    }

    public void getRequestID(String requesterID,String receiverID){






    }

    private int StringToInt(String ID){

        StringBuilder tmp = new StringBuilder();

        for(int i=1;i<ID.length();i++){
            tmp.append(ID.charAt(i));
        }

        return Integer.parseInt(tmp.toString());

    }

}
