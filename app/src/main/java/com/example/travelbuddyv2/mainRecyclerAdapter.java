package com.example.travelbuddyv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class mainRecyclerAdapter extends RecyclerView.Adapter<mainRecyclerAdapter.ViewHolder> {

    List<tripSection> list;

    public mainRecyclerAdapter(List<tripSection> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sectionTextview;
        RecyclerView childRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTextview = itemView.findViewById(R.id.sectionNameTextView);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
        }
    }
    @NonNull
    @Override
    public mainRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.section_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mainRecyclerAdapter.ViewHolder holder, int position) {

        tripSection section = list.get(position);
        String date = section.getDate();
        List<tripModel> trips = section.getTripList();

        holder.sectionTextview.setText(date);

        childRecyclerAdapter childrecyclerAdapter = new childRecyclerAdapter(trips);

        holder.childRecyclerView.setAdapter(childrecyclerAdapter);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
