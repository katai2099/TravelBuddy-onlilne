package com.example.travelbuddyv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class myTripAdapter extends RecyclerView.Adapter<myTripAdapter.myViewHolder> {

    Context context;
    List<tripModel> list;
    OnListListener onListListener;

    public myTripAdapter(Context context,List<tripModel>list,OnListListener onListListener)
    {
        this.context = context;
        this.list = list;
        this.onListListener = onListListener;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView textView,textView2;
        OnListListener onListListener;

        public myViewHolder(@NonNull View itemView, final OnListListener onListListener) {
            super(itemView);
            this.onListListener = onListListener;

            textView = (TextView) itemView.findViewById(R.id.tripNameListAdapter);
            textView2 = (TextView) itemView.findViewById(R.id.tripDateListAdapter);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //System.out.println(getAdapterPosition());
                    onListListener.onListClick(getAdapterPosition());
                }
            });
        }
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mytripview,parent,false);
        return new myViewHolder(view,onListListener);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getTripName());
        holder.textView2.setText(list.get(position).getStartDate()+" ----> "+list.get(position).getEndDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    

    public interface OnListListener{
        void onListClick(int position);
    }

}
