package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelbuddyv2.model.Inventory;
import com.example.travelbuddyv2.networkManager.NetworkObserver;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ClickedItemActivity extends AppCompatActivity {

    Inventory itemInfoFromPreviousActivity;
    ImageView imgItemImage;
    TextView tvItemName;
    String tripID , tripOwner;
    boolean isPersonal;
    String currentUserUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        itemInfoFromPreviousActivity = new Inventory();
        currentUserUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        imgItemImage = findViewById(R.id.imgEnlargeImage);
        tvItemName = findViewById(R.id.tvEnlargeImageName);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            itemInfoFromPreviousActivity.setOwner(bundle.getString("itemOwner"));
            itemInfoFromPreviousActivity.setFileName(bundle.getString("itemName"));
            itemInfoFromPreviousActivity.setFileUri(bundle.getString("itemUri"));
            isPersonal = bundle.getBoolean("isPersonal");
            if(!isPersonal){
                tripOwner = bundle.getString("tripOwner");
            }
            tripID = bundle.getString("tripStringID");
        }
        String title = itemInfoFromPreviousActivity.getFileName().substring(0,itemInfoFromPreviousActivity.getFileName().length()-4);
        setTitle(title);
        tvItemName.setText(itemInfoFromPreviousActivity.getFileName());
        if(Helper.isPdf(itemInfoFromPreviousActivity.getFileName())){
            imgItemImage.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
        }else{
            Picasso.get().load(itemInfoFromPreviousActivity.getFileUri()).fit().into(imgItemImage);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.enlarge_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.optionEnlargeImageDelete:
                deleteFile();
                return true;
            case R.id.optionEnlargeImageDownload:
                downloadFile();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void deleteFile(){
        if(NetworkObserver.isNetworkConnected) {
            if (itemInfoFromPreviousActivity.getOwner().equals(currentUserUUID)) {
                removeItem(currentUserUUID, itemInfoFromPreviousActivity);
                removeItemFromCloudStorage(currentUserUUID,itemInfoFromPreviousActivity);
            } else {
                Toast.makeText(ClickedItemActivity.this,"This file does not belong to you",Toast.LENGTH_SHORT).show();
            }
        }else{
            Helper.showSnackBar(imgItemImage,getString(R.string.noInternet));
        }
    }

    private void downloadFile(){
        if(NetworkObserver.isNetworkConnected) {
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(itemInfoFromPreviousActivity.getFileUri());
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(ClickedItemActivity.this, Environment.DIRECTORY_DOWNLOADS, itemInfoFromPreviousActivity.getFileName());
            downloadManager.enqueue(request);
        }else{
            Helper.showSnackBar(imgItemImage,getString(R.string.noInternet));
        }
    }

    private void removeItemFromCloudStorage(String owner,Inventory inventory){
        StorageReference itemLocationReference = FirebaseStorage.getInstance().getReference().child("trip_file")
                .child(owner)
                .child(tripID)
                .child(inventory.getFileName());
        itemLocationReference.delete();
    }

    private void removeItem(String owner, final Inventory inventory){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Inventory")
                .child(owner)
                .child(tripID);
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Inventory tmp = data.getValue(Inventory.class);
                    if(tmp.getFileName().equals(inventory.getFileName())){
                        DatabaseReference toDelete = data.getRef();
                        toDelete.removeValue();
                        break;
                    }
                }
                finish();
            }
        });
    }




}