package com.example.travelbuddyv2.adapter;


import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.Helper;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Inventory;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InventoryListViewAdapter extends RecyclerView.Adapter <InventoryListViewAdapter.InventoryHolder> {

    private int cnt = 0;
    private final String tag = "inventoryAdapter";
    List<Inventory> inventoryList;
    InventoryAdapterCallBack inventoryAdapterCallBack;

    public InventoryListViewAdapter(List<Inventory> inventoryList, InventoryAdapterCallBack inventoryAdapterCallBack) {
        this.inventoryList = inventoryList;
        this.inventoryAdapterCallBack = inventoryAdapterCallBack;
    }


    @NonNull
    @Override
    public InventoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.inventory_row,parent,false);

        return new InventoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryHolder holder, final int position) {

        Inventory currentInventory = inventoryList.get(position);

        holder.tvInventoryName.setText(currentInventory.getFileName());
        if(currentInventory.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.tvInventoryName.setTextColor(Color.parseColor("#0faaae"));
            Log.d(tag, currentInventory.getFileName() +" Same item owner " + currentInventory.getOwner() + " current user " + FirebaseAuth.getInstance().getCurrentUser().getUid());
        }else{
            holder.tvInventoryName.setTextColor(Color.parseColor("#000000"));
        }
        if(Helper.isPdf(currentInventory.getFileName())){
            holder.imgInventoryThumbnail.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
        }
        else{
            Picasso.get().load(currentInventory.getFileUri()).into(holder.imgInventoryThumbnail);
        }
        holder.tvItemPermission.setText(currentInventory.getPermission());


        holder.btnInventoryOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(),holder.btnInventoryOption);
                popupMenu.inflate(R.menu.inventory_options_menu);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.optionChangePrivacy:
                                inventoryAdapterCallBack.onPrivacyClicked(position);
                                return true;
                            case R.id.optionDelete:
                                inventoryAdapterCallBack.onDeleteClicked(position);
                                return true;
                            case R.id.optionDownload:
                                inventoryAdapterCallBack.onDownloadClicked(position);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });



    }

    @Override
    public int getItemCount() {
        return inventoryList == null ? 0 : inventoryList.size();
    }

    class InventoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView tvInventoryName , tvItemPermission;
        final Button btnInventoryOption;
        final ImageView imgInventoryThumbnail;

        public InventoryHolder(@NonNull View itemView) {
            super(itemView);

            tvInventoryName = itemView.findViewById(R.id.tvInventoryName);
            tvItemPermission = itemView.findViewById(R.id.tvItemPermission);
            btnInventoryOption = itemView.findViewById(R.id.btnInventoryOption);
            imgInventoryThumbnail = itemView.findViewById(R.id.imgInventoryThumbnail);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            inventoryAdapterCallBack.onItemToEnlargeClicked(getAdapterPosition());

        }


    }

    public interface InventoryAdapterCallBack{
        void onPrivacyClicked(int position);
        void onDeleteClicked(int position);
        void onItemToEnlargeClicked(int position);
        void onDownloadClicked(int position);
    }

}
