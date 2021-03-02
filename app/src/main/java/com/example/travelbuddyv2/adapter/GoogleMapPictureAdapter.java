package com.example.travelbuddyv2.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.R;

import java.util.List;

public class GoogleMapPictureAdapter extends RecyclerView.Adapter<GoogleMapPictureAdapter.GoogleMapPictureHolder> {


    List<Bitmap> bitmapList;

    public GoogleMapPictureAdapter(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    @NonNull
    @Override
    public GoogleMapPictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.google_map_photo,parent,false);

        return new GoogleMapPictureHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull GoogleMapPictureHolder holder, int position) {

        holder.imageView.setImageBitmap(bitmapList.get(position));

    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    class GoogleMapPictureHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public GoogleMapPictureHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.googleImage);
        }
    }

}
