package com.example.travelbuddyv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.Helper;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Destination;

import java.util.List;

public class DateSelectionAdapter extends  RecyclerView.Adapter<DateSelectionAdapter.DateSelectionHolder>{

    List<String> dates ;
    DateSelectionAdapterCallBack dateSelectionAdapterCallBack;

    public DateSelectionAdapter(List<String> dates, DateSelectionAdapterCallBack dateSelectionAdapterCallBack) {
        this.dates = dates;
        this.dateSelectionAdapterCallBack = dateSelectionAdapterCallBack;
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
        String toShowDate = Helper.changeDateFormatSuitableForTripDetailScreen(date);
        String positionString = String.valueOf(position+1);
        holder.textView.setText("Day " + positionString + ":   " + toShowDate);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    class DateSelectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;

        public DateSelectionHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvBasicRow);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            dateSelectionAdapterCallBack.onListClicked(getAdapterPosition());
        }
    }

    public interface DateSelectionAdapterCallBack{
        void onListClicked(int position);
    }

}
