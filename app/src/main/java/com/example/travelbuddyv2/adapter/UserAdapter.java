package com.example.travelbuddyv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    List<User> users;

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_row,parent,false);

        return new UserHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {

        User user = users.get(position);

        holder.tvFriendName.setText(user.getName());
        holder.tvFriendEmail.setText(user.getEmail());
        holder.img.setImageResource(holder.itemView.getResources().getIdentifier("@drawable/email_confirmmation",null,holder.itemView.getContext().getPackageName()));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {

        private final ImageView img;
        private final TextView tvFriendEmail,tvFriendName;


        public UserHolder(@NonNull View itemView) {
           super(itemView);

           img = itemView.findViewById(R.id.friendProfile);
           tvFriendEmail = itemView.findViewById(R.id.friendEmail);
           tvFriendName = itemView.findViewById(R.id.friendName);

        }
    }

}
