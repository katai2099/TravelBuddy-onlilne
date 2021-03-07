package com.example.travelbuddyv2.adapter;

import android.content.Context;
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
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.TripSection;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ParentGroupTripDetailAdapter extends RecyclerView.Adapter<ParentGroupTripDetailAdapter.ParentGroupTripDetailHolder> {

    private final String tag = "PARENT_DETAIL_ADAPTER";
    List<TripSection> tripSectionList;
    boolean hasPermission;
    ParentGroupTripDetailAdapterCallback parentGroupTripDetailAdapterCallback;

    public ParentGroupTripDetailAdapter(List<TripSection> tripSectionList,ParentGroupTripDetailAdapterCallback parentGroupTripDetailAdapterCallback) {
        this.tripSectionList = tripSectionList;
        this.parentGroupTripDetailAdapterCallback = parentGroupTripDetailAdapterCallback;
        hasPermission = false;
    }

    @NonNull
    @Override
    public ParentGroupTripDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_row,parent,false);
        return new ParentGroupTripDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ParentGroupTripDetailHolder holder, final int position) {

        TripSection tripSection = tripSectionList.get(position);
        final String date = tripSection.getDate();
        holder.sectionTextview.setText(date);

        List<Destination> destinationList = tripSection.getDestinations();
        ChildTripDetailAdapter childTripDetailAdapter = new ChildTripDetailAdapter(destinationList);
        holder.childRecyclerView.setAdapter(childTripDetailAdapter);

        holder.btnAddTripDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // parentGroupTripDetailAdapterCallBack.onListClicked(position);
                if(hasPermission)
                {
                    //Toast.makeText(holder.itemView.getContext(),"You may proceed",Toast.LENGTH_SHORT).show();
                    parentGroupTripDetailAdapterCallback.addNewAttractionClicked(position);
                }
                else{
                    Toast.makeText(holder.itemView.getContext(),"You dont have permission to edit",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripSectionList.size();
    }

    class ParentGroupTripDetailHolder extends RecyclerView.ViewHolder {

        TextView sectionTextview;
        RecyclerView childRecyclerView;
        Button btnAddTripDetail;

        public ParentGroupTripDetailHolder(@NonNull View itemView) {
            super(itemView);
            sectionTextview = itemView.findViewById(R.id.sectionNameTextView);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
            btnAddTripDetail = itemView.findViewById(R.id.section_row_btnAddTripDetail);
        }
    }

    //check whether user can view or can edit

    public void updateUserPermission(boolean permission){
        this.hasPermission = permission;
        Log.d(tag,"Permission of user is " + permission);
//        Toast.makeText(context,String.valueOf(hasPermission),Toast.LENGTH_SHORT).show();
    }

    public interface ParentGroupTripDetailAdapterCallback{
        void addNewAttractionClicked(int position);
    }



}
