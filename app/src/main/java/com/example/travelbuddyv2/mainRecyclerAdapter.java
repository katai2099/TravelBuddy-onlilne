package com.example.travelbuddyv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.model.tripModel;

import java.util.List;

public class mainRecyclerAdapter extends RecyclerView.Adapter<mainRecyclerAdapter.ViewHolder>  {

    int tmp_id_from_child;
    List<tripSection> list;
    mainAdapterListener mainAdapterListener;
    childRecyclerAdapter.ChildAdapterListener childAdapterListener;

    public mainRecyclerAdapter(List<tripSection> list, mainAdapterListener mainAdapterListener, childRecyclerAdapter.ChildAdapterListener childAdapterListener) {
        this.list = list;
        this.mainAdapterListener = mainAdapterListener;
        this.childAdapterListener = childAdapterListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sectionTextview;
        RecyclerView childRecyclerView;
        mainAdapterListener mainAdapterListener;

        public ViewHolder(@NonNull View itemView, final mainAdapterListener mainAdapterListener) {
            super(itemView);
            sectionTextview = itemView.findViewById(R.id.sectionNameTextView);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainAdapterListener.onTitleClicked(getAdapterPosition());
                }
            });


           ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(childRecyclerView);

        }

    }
    @NonNull
    @Override
    public mainRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.section_row,parent,false);

        return new ViewHolder(view,mainAdapterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull mainRecyclerAdapter.ViewHolder holder, int position) {

        tripSection section = list.get(position);
        String date = section.getDate();
        List<tripModel> trips = section.getTripList();
        holder.sectionTextview.setText(date);
        childRecyclerAdapter childrecyclerAdapter = new childRecyclerAdapter(trips,childAdapterListener);
        holder.childRecyclerView.setAdapter(childrecyclerAdapter);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface mainAdapterListener{
        public void onTitleClicked(int position);
        public void deleteClickedTripDetail(int id);
    }

     ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

         //  mainAdapterListener.k(viewHolder.getAdapterPosition());
          //System.out.println(tmp_id_from_child);
            mainAdapterListener.deleteClickedTripDetail(tmp_id_from_child);
        }
    };



}
