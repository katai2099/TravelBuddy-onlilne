package com.example.travelbuddyv2.adapter;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileReader;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

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
        final Request receiver = new Request();
        receiver.setRequestType("received");
        receiver.setTripID(holder.id);
        receiver.setTripName(holder.name);

        holder.btnInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user.getUser_id())
                         .child(holder.id)
                        .setValue(requester)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference().child("Invitation_Request")
                                        .child(user.getUser_id())
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(holder.id)
                                        .setValue(receiver)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
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

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {

        private final ImageView img;
        private final TextView tvFriendEmail,tvFriendName;
        private final Button btnInviteFriend;

        private final String name , id;

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

}
