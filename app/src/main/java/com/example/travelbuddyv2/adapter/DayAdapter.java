package com.example.travelbuddyv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayHolder> {

    List<String> days;
    DayAdapterCallback dayAdapterCallback;

    public DayAdapter(List<String> days, DayAdapterCallback dayAdapterCallback) {
        this.days = days;
        this.dayAdapterCallback = dayAdapterCallback;
    }

    @NonNull
    @Override
    public DayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_row,parent,false);
        return new DayHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DayHolder holder, int position) {
        holder.tvDay.setText(days.get(position));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    class DayHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView tvDay;
        public DayHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDayRow);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            dayAdapterCallback.onListClicked(getAdapterPosition());
        }
    }

    public interface DayAdapterCallback{
        void onListClicked(int position);
    }

}
