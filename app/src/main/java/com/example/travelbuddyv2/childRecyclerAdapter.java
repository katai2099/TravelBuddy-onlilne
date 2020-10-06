package com.example.travelbuddyv2;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLOutput;
import java.util.List;

public class childRecyclerAdapter extends RecyclerView.Adapter<childRecyclerAdapter.ViewHolder> {

    List<tripModel> list;
    ChildAdapterListener childAdapterListener;

    public childRecyclerAdapter(List<tripModel> list,ChildAdapterListener childAdapterListener) {
        this.list = list;
        this.childAdapterListener = childAdapterListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemTextView;
        ChildAdapterListener childAdapterListener;

        public ViewHolder(@NonNull View itemView,final ChildAdapterListener childAdapterListener) {
            super(itemView);
            itemTextView =itemView.findViewById(R.id.itemTextView);
            this.childAdapterListener = childAdapterListener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //System.out.println(getAdapterPosition());
                //  childAdapterListener.onItemClicked(list.get(getAdapterPosition()).getId());
                    childAdapterListener.onItemClickedToEdit(list.get(getAdapterPosition()).getId());
                }
            });
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    childAdapterListener.onItemClicked(list.get(getAdapterPosition()).getId());
                   // System.out.println(list.get(getAdapterPosition()).getId());
                    return false;
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

        return new ViewHolder(view,childAdapterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemTextView.setText(list.get(position).getStartTime()+" ---> "+list.get(position).getEndTime() + "\t\t" + list.get(position).getDestination());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ChildAdapterListener{
        void onItemClicked(int position);
        void onItemClickedToEdit(int position);
    }


}
