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

import com.example.travelbuddyv2.Helper;
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
    ChildTripDetailAdapter.ChildTripDetailAdapterCallBack childTripDetailAdapterCallBack;

    public ParentGroupTripDetailAdapter(List<TripSection> tripSectionList, ParentGroupTripDetailAdapterCallback parentGroupTripDetailAdapterCallback,
                                        ChildTripDetailAdapter.ChildTripDetailAdapterCallBack childTripDetailAdapterCallBack) {

        this.tripSectionList = tripSectionList;
        this.parentGroupTripDetailAdapterCallback = parentGroupTripDetailAdapterCallback;
        this.childTripDetailAdapterCallBack = childTripDetailAdapterCallBack;
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
        String toShowDate = Helper.changeDateFormatSuitableForTripDetailScreen(date);
        String positionString = String.valueOf(position+1);
        holder.sectionTextview.setText("Day " + positionString + ":   "+ toShowDate);
        if(tripSectionList.get(position).getDestinations().size()>0){
            holder.sectionStartTime.setVisibility(View.VISIBLE);
            holder.sectionStartTime.setText("Start : " + tripSectionList.get(position).getDestinations().get(0).getStartTime());
            holder.sectionStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentGroupTripDetailAdapterCallback.changeStartTimeClicked(position);
                }
            });
        }else{
            holder.sectionStartTime.setVisibility(View.GONE);
        }

        List<Destination> destinationList = tripSection.getDestinations();
        ChildTripDetailAdapter childTripDetailAdapter = new ChildTripDetailAdapter(destinationList,childTripDetailAdapterCallBack);
        holder.childRecyclerView.setAdapter(childTripDetailAdapter);

 /*       holder.btnAddTripDetail.setOnClickListener(new View.OnClickListener() {
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
        });*/
        holder.addAttraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPermission)
                {
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

        TextView sectionTextview,sectionStartTime ;
        RecyclerView childRecyclerView;
      //  Button btnAddTripDetail;
        View addAttraction;

        public ParentGroupTripDetailHolder(@NonNull View itemView) {
            super(itemView);
            sectionTextview = itemView.findViewById(R.id.sectionNameTextView);
            sectionStartTime = itemView.findViewById(R.id.sectionStartTime);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
           // btnAddTripDetail = itemView.findViewById(R.id.section_row_btnAddTripDetail);
            addAttraction = itemView.findViewById(R.id.sectionRowAddAttraction);
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
        void changeStartTimeClicked(int position);
    }



}
