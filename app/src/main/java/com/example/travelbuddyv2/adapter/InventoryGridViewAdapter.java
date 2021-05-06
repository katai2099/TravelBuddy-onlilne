package com.example.travelbuddyv2.adapter;

import android.graphics.Color;
import android.os.Build;
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

public class InventoryGridViewAdapter extends RecyclerView.Adapter<InventoryGridViewAdapter.InventoryGridViewHolder> {

    List<Inventory> inventoryList;
    InventoryGridViewAdapterCallBack inventoryGridViewAdapterCallBack;

    public InventoryGridViewAdapter(List<Inventory> inventoryList,InventoryGridViewAdapterCallBack inventoryGridViewAdapterCallBack) {
        this.inventoryList = inventoryList;
        this.inventoryGridViewAdapterCallBack = inventoryGridViewAdapterCallBack;
    }

    @NonNull
    @Override
    public InventoryGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_grid_view_row,parent,false);

        return new InventoryGridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryGridViewHolder holder, final int position) {

        Inventory currentInventory = inventoryList.get(position);

        holder.tvInventoryName.setText(currentInventory.getFileName());
        if(currentInventory.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            holder.tvInventoryName.setTextColor(Color.parseColor("#0faaae"));
        if(Helper.isPdf(currentInventory.getFileName())){
            holder.imgInventoryThumbnail.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
        }
        else{
            Picasso.get().load(currentInventory.getFileUri()).into(holder.imgInventoryThumbnail);
        }


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
                                inventoryGridViewAdapterCallBack.gridOnPrivacyGClicked(position);
                                return true;
                            case R.id.optionDelete:
                                inventoryGridViewAdapterCallBack.gridOnDeleteClicked(position);
                                return true;
                            case R.id.optionDownload:
                                inventoryGridViewAdapterCallBack.gridOnDownloadClicked(position);
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
        return inventoryList==null ? 0 : inventoryList.size();
    }

    class InventoryGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgInventoryThumbnail;
        TextView tvInventoryName;
        Button btnInventoryOption;

        public InventoryGridViewHolder(@NonNull View itemView) {
            super(itemView);
            imgInventoryThumbnail = itemView.findViewById(R.id.imgInventoryGridThumbnail);
            tvInventoryName = itemView.findViewById(R.id.tvInventoryGridName);
            btnInventoryOption = itemView.findViewById(R.id.btnInventoryGridOption);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            inventoryGridViewAdapterCallBack.gridOnItemToEnlargeClicked(getAdapterPosition());
        }
    }

    public interface InventoryGridViewAdapterCallBack{
        void gridOnPrivacyGClicked(int position);
        void gridOnDeleteClicked(int position);
        void gridOnItemToEnlargeClicked(int position);
        void gridOnDownloadClicked(int position);
    }

}
