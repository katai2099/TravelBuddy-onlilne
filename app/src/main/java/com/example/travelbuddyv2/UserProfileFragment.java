package com.example.travelbuddyv2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelbuddyv2.model.Member;
import com.example.travelbuddyv2.model.User;
import com.example.travelbuddyv2.model.tripModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class UserProfileFragment extends Fragment {

    final private String tag = "USER_PROFILE_FRAGMENT";
    TextView tvUserEmail, tvUserName , tvLogOut;
    Button  btnSave,btnCancel;
    private  BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private EditText etEditProfileName;
    CircleImageView imgUserProfileImage;
    User user;
    private static final int GalleryPick = 1;
    private ProgressDialog loadingBar ;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_user_profile, container, false);

        tvUserName = root.findViewById(R.id.tvProfileName);
        tvUserEmail = root.findViewById(R.id.tvProfileEmail);
        tvLogOut = root.findViewById(R.id.tvUserLogOut);
        imgUserProfileImage = root.findViewById(R.id.imgProfilePic);
        loadingBar = new ProgressDialog(getContext());
        user = new User();
        getCurrentUserInformation();

        imgUserProfileImage.setImageResource(R.drawable.ic_baseline_person_24);

        initBottomSheet(root);

        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

       // changeUserName(newName,v);

       imgUserProfileImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent galleryIntent = new Intent();
               galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
               galleryIntent.setType("image/*");

               startActivityForResult(galleryIntent,GalleryPick);

           }
       });

        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut(getActivity());
            }
        });
        return root;
    }

    private void initBottomSheet(View root){
       bottomSheetDialog =  new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
       bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.edit_profile_name_bottom_layout,
                        (LinearLayout)root.findViewById(R.id.bottomSheetContainer));
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        btnCancel = bottomSheetView.findViewById(R.id.btnEditProfileCancel);
        btnSave = bottomSheetView.findViewById(R.id.btnEditProfileSave);
        etEditProfileName = bottomSheetView.findViewById(R.id.etEditProfileName);

        etEditProfileName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelButtonBehavior();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonBehavior();
            }
        });
    }

    private void cancelButtonBehavior(){
        bottomSheetDialog.dismiss();
    }
    private void saveButtonBehavior(){
        String previousName = tvUserName.getText().toString();
        String newName = etEditProfileName.getText().toString();

        if(!previousName.equals(newName)){
            changeUserName(newName);
            refreshUserName();
        }

        bottomSheetDialog.dismiss();
    }

    private void refreshUserName(){
        tvUserName.setText(etEditProfileName.getText().toString());
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == Activity.RESULT_OK && data!=null){
            Log.d(tag,"first one was called");
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(getContext(),this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d(tag,"I am here");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == Activity.RESULT_OK){
                Uri resultUri = result.getUri();
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                StorageReference UserProfileImageReference = FirebaseStorage.getInstance().getReference().child("profile_image");

                final StorageReference filePath = UserProfileImageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");


                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(getContext(),"Profile Image Update successfully",Toast.LENGTH_SHORT).show();

                            final Task<Uri> firebaseUri = task.getResult().getStorage().getDownloadUrl();


                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    user.setProfile_image(downloadUrl);
                                }
                            });
                           /* String downloadUrl = task.getResult().getUploadSessionUri().toString();

                            user.setProfile_image(downloadUrl);*/

                            //update current user node
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //update current user in member node

                            final DatabaseReference memberReference = FirebaseDatabase.getInstance().getReference().child("Member");

                            //update current user in knownList node

                            final DatabaseReference knownListReference = FirebaseDatabase.getInstance().getReference().child("Known_lists");

                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        memberReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot tripOwner:snapshot.getChildren()){
                                                    for(DataSnapshot tripStringID:tripOwner.getChildren()){
                                                        for(DataSnapshot member:tripStringID.getChildren()){
                                                            Member currentMember = member.getValue(Member.class);
                                                            if(currentMember.getID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            && currentMember.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                                                currentMember.setProfileImg(user.getProfile_image());

                                                                DatabaseReference toUpdate = member.getRef();
                                                                toUpdate.setValue(currentMember);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        knownListReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot owner:snapshot.getChildren()){
                                                    if(! owner.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                                    {
                                                        for(DataSnapshot userList: owner.getChildren()){
                                                            User currentUser = userList.getValue(User.class);

                                                            if(currentUser.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            && currentUser.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                                                currentUser.setProfile_image(user.getProfile_image());

                                                                DatabaseReference toUpdate = userList.getRef();
                                                                toUpdate.setValue(currentUser);
                                                                break;

                                                            }

                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        Toast.makeText(getContext(),"Upload finished",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Picasso.get().load(user.getProfile_image()).fit().into(imgUserProfileImage);
                                    }else{
                                        loadingBar.dismiss();
                                    }
                                }
                            });

                        }
                        else{
                            String message = task.getException().toString();
                            Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });


            }

        }

    }

    private void signOut(final Activity activity){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(getString(R.string.appSharedPref),MODE_PRIVATE);
        sharedPreferences.edit().remove("token").apply();
        sharedPreferences.edit().remove("isFirstRetrievePendingNotification").apply();

        cancelAllPendingIntentOfAlarm();

        savePendingNotificationIntoFirebaseDatabase(activity);


    }

    private void getCurrentUserInformation(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User tmp = dataSnapshot.getValue(User.class);
                user.setName(tmp.getName());
                user.setEmail(tmp.getEmail());
                user.setUser_id(tmp.getUser_id());
                user.setProfile_image(tmp.getProfile_image());

                tvUserName.setText(user.getName());
                tvUserEmail.setText(user.getEmail());
                etEditProfileName.setText(user.getName());

                if(user.getProfile_image()!=null && !(TextUtils.isEmpty(user.getProfile_image()))){
                    Picasso.get().load(user.getProfile_image()).resize(200,0).into(imgUserProfileImage);
                }
            }
        });
    }

    private void changeUserName(final String newName){
        //update current user
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("name");

        //update current user in member node

        final DatabaseReference memberReference = FirebaseDatabase.getInstance().getReference().child("Member");

        //update current user in knownList node

        final DatabaseReference knownListReference = FirebaseDatabase.getInstance().getReference().child("Known_lists");

        reference.setValue(newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                memberReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot tripOwner:snapshot.getChildren()){
                            for(DataSnapshot tripStringID:tripOwner.getChildren()){
                                for(DataSnapshot member:tripStringID.getChildren()){
                                    Member currentMember = member.getValue(Member.class);
                                    if(currentMember.getID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            && currentMember.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){

                                        DatabaseReference toUpdate = member.getRef();
                                        toUpdate.child("name").setValue(newName);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                knownListReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot owner:snapshot.getChildren()){
                            if(! owner.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            {
                                for(DataSnapshot userList: owner.getChildren()){
                                    User currentUser = userList.getValue(User.class);

                                    if(currentUser.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            && currentUser.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){

                                        DatabaseReference toUpdate = userList.getRef();
                                        toUpdate.child("name").setValue(newName);
                                        break;

                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private void savePendingNotificationIntoFirebaseDatabase(final Activity activity){


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Pending_notification_backup")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        final DatabaseHelper db = new DatabaseHelper(getContext());

        List<tripModel> tripModels = db.getPendingNotificationList();

        final int size = tripModels.size()-1;

        if(!tripModels.isEmpty()){
            for(int i=0;i<tripModels.size();i++){

                final int cnt = i;

                reference.child(tripModels.get(i).getStringID()).setValue(tripModels.get(i)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(cnt == size){
                            //we clear offline database before signOut so next user wont use the same data as previous user
                            db.deleteAllPendingNotification();
                            Intent i = new Intent(getContext(), loginActivity.class);
                            startActivity(i);
                            activity.finishAffinity();
                            FirebaseAuth.getInstance().signOut();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"SOMETHING WENT WRONG",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else{
            Intent i = new Intent(getContext(), loginActivity.class);
            startActivity(i);
            activity.finishAffinity();
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void cancelAllPendingIntentOfAlarm(){
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getContext(),ReminderBroadcast.class);

        DatabaseHelper db = new DatabaseHelper(getContext());

        List<tripModel> tripModels = db.getPendingNotificationList();

        for(int i=0;i<tripModels.size();i++){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),Helper.tripStringIDToInt(tripModels.get(i).getStringID()),intent,0);
            pendingIntent.cancel();
            alarmManager.cancel(pendingIntent);
        }
    }


}