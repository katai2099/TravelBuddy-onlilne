package com.example.travelbuddyv2.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.adapter.RequestAdapter;
import com.example.travelbuddyv2.model.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private final String tag = "NOTIFICATION_FRAGMENT";
    private RecyclerView rcvNotification;
    private List<Request> requestList ;
    private RequestAdapter requestAdapter;
    private NotificationsViewModel notificationsViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        requestList = new ArrayList<>();
        fillFriendRequestNotificationList();
        rcvNotification = root.findViewById(R.id.rcvFragmentNotificationList);
        rcvNotification.setLayoutManager(new LinearLayoutManager(root.getContext()));
        requestAdapter = new RequestAdapter(requestList);
        rcvNotification.setAdapter(requestAdapter);
        return root;
    }

    private void fillFriendRequestNotificationList(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Invitation_Request")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        Request request = data.getValue(Request.class);
                        if(request.getRequestType().equals("received"))
                        requestList.add(request);
                        Log.d(tag,request.toString());
                    }
                }
                requestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}