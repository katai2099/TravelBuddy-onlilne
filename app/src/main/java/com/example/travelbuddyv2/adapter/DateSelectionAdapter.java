package com.example.travelbuddyv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;

import java.util.List;

public class DateSelectionAdapter extends  RecyclerView.Adapter<DateSelectionAdapter.DateSelectionHolder>{

    List<String> dates ;

    public DateSelectionAdapter(List<String> dates) {
        this.dates = dates;
    }

    @NonNull
    @Override
    public DateSelectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_row,parent,false);

        return new DateSelectionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DateSelectionHolder holder, int position) {

        String date = dates.get(position);
        holder.textView.setText(date);

    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    class DateSelectionHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public DateSelectionHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvBasicRow);
        }
    }

}
