package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.GoogleMapPictureAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String tag = "MAP_ACTIVITY";

   // GoogleMapPhotoAdapter googleMapPhotoAdapter;
    List<Bitmap> bitmapList;
   // ViewPager viewPagerGoogleMapPhoto;
    private GoogleMap mMap;
    View info;
    PlacesClient placesClient;
    GoogleMapPictureAdapter googleMapPictureAdapter;
    RecyclerView rcvGoogleMapPictures;
    String dateFromTripDetail,tripStringID, tripOwner;
    Button btnAddTripToDatabase;
    boolean isCurrentUserAMember;
    int ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            tripStringID = bundle.getString("tripStringID");
            dateFromTripDetail = bundle.getString("dateOfTrip");
            tripOwner = bundle.getString("tripOwner");

            Log.d(tag,tripOwner==null?"null":tripOwner);
            Log.d(tag,"date of Trip " + dateFromTripDetail);
            Log.d(tag,"trip String ID " + tripStringID);
        }

        isCurrentUserAMember = tripOwner != null;


        btnAddTripToDatabase = findViewById(R.id.mapActivityBtnAddTripDetail);
        bitmapList = new ArrayList<>();
        rcvGoogleMapPictures = findViewById(R.id.googleMapPicturesRecyclerView);

        rcvGoogleMapPictures.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
     //   rcvGoogleMapPictures.setPadding(10,10,10,10);
        googleMapPictureAdapter = new GoogleMapPictureAdapter(bitmapList);
        rcvGoogleMapPictures.setAdapter(googleMapPictureAdapter);

        info = findViewById(R.id.googleMapInformationLayout);
        info.setVisibility(View.GONE);
       // viewPagerGoogleMapPhoto = findViewById(R.id.googleMapPicturesViewPager);


        Places.initialize(this,"AIzaSyA9ND3V5NWS18Gr0sIjO-e1A3hPF1uONAw");

        placesClient = Places.createClient(this);

        if(!isCurrentUserAMember){
            getDateLatestTripDetailIDForOwner();
        }else{
            getDateLatestTripDetailIDForMember();
        }


       /* CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("katai");*/


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));


        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                final String placeId = pointOfInterest.placeId;
                final String placeName = pointOfInterest.name;
                final LatLng placeLatLng = pointOfInterest.latLng;

                //clear list to display new image every time user click on POI
                bitmapList.clear();

                // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
                final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);

                // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
                final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);

                placesClient.fetchPlace(placeRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        final Place place = fetchPlaceResponse.getPlace();
                        // Toast.makeText(getApplicationContext(),place.toString(),Toast.LENGTH_SHORT).show();

                        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                        if (metadata == null || metadata.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "No metadata", Toast.LENGTH_SHORT).show();
                        }

                        //   final PhotoMetadata photoMetadata = metadata.get(0);

                        // Get the attribution text.
                        //  final String attributions = photoMetadata.getAttributions();

                        if (metadata != null && !(metadata.isEmpty())){

                            for (int i = 0; i < metadata.size(); i++) {
                                if (i == 5)
                                    break;
                                final PhotoMetadata photoMetadata = metadata.get(i);
                                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                        .setMaxWidth(300)
                                        .setMaxHeight(300)
                                        .build();

                                placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                                    @Override
                                    public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                                        //Toast.makeText(getBaseContext(),"FETCH PHOTO COMPLETED",Toast.LENGTH_SHORT).show();
                                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                        bitmapList.add(bitmap);
                                        googleMapPictureAdapter.notifyDataSetChanged();
                                        //      googleMapPhotoAdapter = new GoogleMapPhotoAdapter(getApplicationContext(), bitmapList);
                                        //    viewPagerGoogleMapPhoto.setAdapter(googleMapPhotoAdapter);
                                        //    viewPagerGoogleMapPhoto.setPadding(10,10,10,0);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if (e instanceof ApiException) {
                                            final ApiException apiException = (ApiException) e;
                                            Log.e(tag, "Place not found: " + e.getMessage());
                                            final int statusCode = apiException.getStatusCode();
                                            Log.e(tag, String.valueOf(statusCode));

                                        }
                                    }
                                });


                            }
                    }

                    }
                });



                info.setVisibility(View.VISIBLE);
                TextView tv = info.findViewById(R.id.tvPlaceName);
                tv.setText(placeName);

                btnAddTripToDatabase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!isCurrentUserAMember){
                            addAttractionToDatabaseForOwner(placeId,placeName,placeLatLng);
                        }else{
                            addAttractionToDatabaseForMember(placeId,placeName,placeLatLng);
                        }

                    }
                });

            }
        });

    }

    private void getDateLatestTripDetailIDForOwner(){

        DatabaseReference currentDateTripDetailNode = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripStringID)
                .child(dateFromTripDetail);

        currentDateTripDetailNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Integer> idTmp = new ArrayList<>();
                for(DataSnapshot tripDetailID:snapshot.getChildren())
                {
                    idTmp.add(Helper.tripDetailStringIDToInt(tripDetailID.getKey()));
                }
                if(!idTmp.isEmpty()){
                    Collections.sort(idTmp);
                    int res = idTmp.get(idTmp.size()-1);
                    ID = res+1;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getDateLatestTripDetailIDForMember(){

        DatabaseReference currentDateTripDetailNode = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(tripOwner)
                .child(tripStringID)
                .child(dateFromTripDetail);

        currentDateTripDetailNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Integer> idTmp = new ArrayList<>();
                for(DataSnapshot tripDetailID:snapshot.getChildren())
                {
                    idTmp.add(Helper.tripDetailStringIDToInt(tripDetailID.getKey()));
                }
                if(!idTmp.isEmpty()){
                    Collections.sort(idTmp);
                    int res = idTmp.get(idTmp.size()-1);
                    ID = res+1;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void killActivity(){
        finish();
    }

    private void addAttractionToDatabaseForOwner(String placeId,String placeName,LatLng placeLatLng){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripStringID)
                .child(dateFromTripDetail)
                .child("td"+ID);

        Destination destination = new Destination();
        destination.setPlaceId(placeId);
        destination.setName(placeName);
        destination.setLongtitude(placeLatLng.longitude);
        destination.setLatitude(placeLatLng.latitude);

        reference.setValue(destination).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                killActivity();
            }
        });
    }

    private void addAttractionToDatabaseForMember(String placeId,String placeName,LatLng placeLatLng){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(tripOwner)
                .child(tripStringID)
                .child(dateFromTripDetail)
                .child("td"+ID);

        Destination destination = new Destination();
        destination.setPlaceId(placeId);
        destination.setName(placeName);
        destination.setLongtitude(placeLatLng.longitude);
        destination.setLatitude(placeLatLng.latitude);

        reference.setValue(destination).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                killActivity();
            }
        });

    }

}