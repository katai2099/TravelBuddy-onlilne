package com.example.travelbuddyv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class childRecyclerAdapter extends RecyclerView.Adapter<childRecyclerAdapter.ViewHolder> {

    List<tripModel> list;

    public childRecyclerAdapter(List<tripModel> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView =itemView.findViewById(R.id.itemTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(getAdapterPosition());
                }
            });

        }



    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
       // View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mytripview,parent,false);
        View view = layoutInflater.inflate(R.layout.item_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemTextView.setText(list.get(position).getStartTime()+" ---> "+list.get(position).getEndTime() + "\t\t" + list.get(position).getDestination());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
