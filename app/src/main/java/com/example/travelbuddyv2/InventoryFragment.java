    package com.example.travelbuddyv2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.InventoryGridViewAdapter;
import com.example.travelbuddyv2.adapter.InventoryListViewAdapter;
import com.example.travelbuddyv2.model.Inventory;
import com.example.travelbuddyv2.model.User;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class InventoryFragment extends Fragment implements InventoryListViewAdapter.InventoryAdapterCallBack ,
            InventoryGridViewAdapter.InventoryGridViewAdapterCallBack {

    Boolean isListView = true , onCreateOptionMenuCalled = false;
    RecyclerView rcvItems;
    private static final int PICK_IMAGE = 3;
    private static final int PICK_PDF_FILE = 2;
    private MenuItem listView;
    private MenuItem gridView;
    String tripID , tripOwner;
    boolean isPersonal;
    private ProgressDialog loadingBar ;
    private final String tag = "INVENTORY_FRAGMENT";
    InventoryListViewAdapter inventoryAdapter ;
    InventoryGridViewAdapter inventoryGridViewAdapter;
    private List<Inventory> inventoryList;
    LinearLayoutManager gridLayout;
    LinearLayoutManager listLayout;
    private String currentUserUUID;
    User currentOwnerOfItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_inventory, container, false);
        setHasOptionsMenu(true);
        loadingBar = new ProgressDialog(getContext());
        listLayout = new LinearLayoutManager(getContext());
        gridLayout = new GridLayoutManager(getContext(),2);
        currentOwnerOfItem = new User();
        currentUserUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Bundle bundle = getArguments();
        if(bundle!=null){
            isPersonal = bundle.getBoolean("isPersonal");
            tripID = bundle.getString("TRIP_STRING_ID");

            if(!isPersonal){
                tripOwner = bundle.getString("TRIP_OWNER");
            }
        }
        inventoryList = new ArrayList<>();
        rcvItems = root.findViewById(R.id.rcvItems);
        rcvItems.setLayoutManager(listLayout);
        rcvItems.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
        inventoryAdapter = new InventoryListViewAdapter(inventoryList,this);
        inventoryGridViewAdapter = new InventoryGridViewAdapter(inventoryList,this);

        rcvItems.setAdapter(inventoryAdapter);

        if(isPersonal){
            fillInventoryList(currentUserUUID,tripID);
        }else{
            fillInventoryList(tripOwner,tripID);
        }

        return root;
    }




    private void openFile() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        String [] mimeTypes = {"application/pdf", "image/*"};
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent, PICK_PDF_FILE);
    }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if (requestCode == PICK_PDF_FILE
                    && resultCode == Activity.RESULT_OK) {
                // The result data contains a URI for the document or directory that
                // the user selected.
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                   String type = getContext().getContentResolver().getType(uri);
                    Toast.makeText(getContext(),type,Toast.LENGTH_SHORT).show();
                    if(isPersonal){
                        uploadToFirebaseCloudStorage(uri,currentUserUUID);
                    }else{
                        uploadToFirebaseCloudStorage(uri,tripOwner);
                    }

                }
            }

        }

        @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.inventory_menu,menu);
        listView = menu.findItem(R.id.optionListView);
        gridView = menu.findItem(R.id.optionGridView);
        listView.setVisible(false);
        onCreateOptionMenuCalled = true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.optionAddItem:
                openFile();
                return true;
            case R.id.optionListView:
                changeToListView();
                return true;
            case R.id.optionGridView:
                changeToGridView();
                return true;
            default:
               return super.onOptionsItemSelected(item);
        }

    }

        private void changeToListView() {
        rcvItems.setLayoutManager(listLayout);
        rcvItems.setAdapter(inventoryAdapter);
        gridView.setVisible(true);
        listView.setVisible(false);
        isListView = true;
        }

        private void changeToGridView(){
        rcvItems.setLayoutManager(gridLayout);
        rcvItems.setAdapter(inventoryGridViewAdapter);
        listView.setVisible(true);
        gridView.setVisible(false);
        isListView = false;
        }

        private void uploadToFirebaseCloudStorage(Uri uri,String owner){

        Cursor returnCursor = getContext()
                .getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        final String fileName = returnCursor.getString(nameIndex);


        Log.d(tag,returnCursor.getString(nameIndex));
        //Log.d(tag,String.valueOf(returnCursor.getLong(sizeIndex)));

        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);
        StorageReference UserProfileImageReference = FirebaseStorage.getInstance().getReference().child("trip_file")
                .child(owner)
                .child(tripID);

        StorageReference path = UserProfileImageReference.child(fileName);

        path.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                 //   loadingBar.dismiss();

                    final Task<Uri> firebaseUri = task.getResult().getStorage().getDownloadUrl();

                    final Inventory inventory = new Inventory();

                    firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            inventory.setFileUri(downloadUrl);
                            inventory.setFileName(fileName);
                            inventory.setOwner(currentUserUUID);
                            if(isPersonal)
                            inventory.setPermission("Private");
                            else{
                                inventory.setPermission("Shared");
                            }
                            if(isPersonal){
                                updateFirebaseDatabaseInventoryNode(inventory,currentUserUUID);
                            }else{
                                updateFirebaseDatabaseInventoryNode(inventory,tripOwner);
                            }
                        }
                    });

                }
                else{
                    loadingBar.dismiss();
                }
            }
        });


    }

    private void updateFirebaseDatabaseInventoryNode(Inventory inventory,String owner){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Inventory")
                .child(owner)
                .child(tripID);

        String key = reference.push().getKey();

        DatabaseReference endRes = reference.child(key);

        endRes.setValue(inventory).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadingBar.dismiss();
                Toast.makeText(getContext(),"Upload success",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fillInventoryList(String owner,String tripID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Inventory")
                .child(owner)
                .child(tripID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inventoryList.clear();

                for (DataSnapshot data:snapshot.getChildren()) {
                    Inventory tmp = data.getValue(Inventory.class);
                    //inventoryList.add(tmp);
                    if(! tmp.getOwner().equals(currentUserUUID) && tmp.getPermission().toUpperCase().equals("SHARED")){
                        inventoryList.add(tmp);
                    }else if(tmp.getOwner().equals(currentUserUUID)){
                        inventoryList.add(tmp);
                    }
                }
                inventoryAdapter.notifyDataSetChanged();
                inventoryGridViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

        @Override
        public void onPrivacyClicked(int position) {

            final Inventory inventory = inventoryList.get(position);


            final CharSequence[] choices = {"Private","Shared"};
            int checked = 1;

            if(inventory.getPermission().toUpperCase().equals("PRIVATE")) checked = 0 ;


            if(inventory.getOwner().equals(currentUserUUID)){

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Please Select Privacy")
               .setSingleChoiceItems(choices, checked, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                        if(which==1){
                            inventory.setPermission("Shared");
                        }
                        else{
                            inventory.setPermission("Private");
                        }
                   }
               })

                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isPersonal){
                            changeItemPermission(inventory,currentUserUUID);
                        }else{
                            changeItemPermission(inventory,tripOwner);
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }else{
               Toast.makeText(getContext(),"You cannot set Permission to another group member item",Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onDeleteClicked(int position) {
           final Inventory inventory = inventoryList.get(position);

            if(currentUserUUID.equals(inventory.getOwner())){

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("are you sure you want to delete this item?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isPersonal){
                            removeItem(currentUserUUID,inventory);
                        }else{
                            removeItem(tripOwner,inventory);
                        }
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                getCurrentItemOwnerName(inventory.getOwner(),builder,inventory);

            }
        }

        @Override
        public void onItemToEnlargeClicked(int position) {

            Inventory itemToPassToNextActivity = inventoryList.get(position);
            Intent i = new Intent(getContext(),ClickedItemActivity.class);
            i.putExtra("itemName",itemToPassToNextActivity.getFileName());
            i.putExtra("itemOwner",itemToPassToNextActivity.getOwner());
            i.putExtra("itemUri",itemToPassToNextActivity.getFileUri());
            if(isPersonal){
                i.putExtra("isPersonal",true);
                i.putExtra("tripStringID",tripID);
            }else{
                i.putExtra("isPersonal",false);
                i.putExtra("tripStringID",tripID);
                i.putExtra("tripOwner",tripOwner);
            }
            startActivity(i);
        }

        @Override
        public void onDownloadClicked(int position) {
            Inventory tmp = inventoryList.get(position);

            DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(tmp.getFileUri());
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(getContext(),Environment.DIRECTORY_DOWNLOADS,tmp.getFileName());
            downloadManager.enqueue(request);


        }

        private void getCurrentItemOwnerName(String ownerUUID, final AlertDialog.Builder builder, final Inventory inventory){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User")
                    .child(ownerUUID);

            reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    User tmp = dataSnapshot.getValue(User.class);
                    currentOwnerOfItem.setName(tmp.getName());
                    builder.setMessage("This item belongs to \"" + currentOwnerOfItem.getName() + "\" are you sure you want to delete?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(isPersonal){
                                removeItem(currentUserUUID,inventory);
                            }else{
                                removeItem(tripOwner,inventory);
                            }
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        private void changeItemPermission(final Inventory inventory, String owner){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Inventory")
                    .child(owner)
                    .child(tripID);



            reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    for(DataSnapshot item:dataSnapshot.getChildren()){
                        Inventory tmp = item.getValue(Inventory.class);

                        if(tmp.getFileName().equals(inventory.getFileName()) && tmp.getOwner().equals(inventory.getOwner())){
                            DatabaseReference toUpdate = item.getRef();
                            toUpdate.child("permission").setValue(inventory.getPermission());
                            break;
                        }

                    }
                }
            });

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
                }
            });
        }


        @Override
        public void gridOnPrivacyGClicked(int position) {
            onPrivacyClicked(position);
        }

        @Override
        public void gridOnDeleteClicked(int position) {
            onDeleteClicked(position);
        }

        @Override
        public void gridOnItemToEnlargeClicked(int position) {
            onItemToEnlargeClicked(position);
        }

        @Override
        public void gridOnDownloadClicked(int position) {
            onDownloadClicked(position);
        }

    @Override
    public void onResume() {
        super.onResume();
        if(isListView && onCreateOptionMenuCalled) {
            gridView.setVisible(true);
            listView.setVisible(false);
        }else if(onCreateOptionMenuCalled){
            gridView.setVisible(false);
            listView.setVisible(true);
        }

    }
}