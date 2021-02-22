package com.example.travelbuddyv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Request;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {

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
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {

        final Request request = requestList.get(position);
        holder.tvFriendRequestNotification.setText("You got an invitation from blabla to join " + request.getTripName() +"  trip");



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
