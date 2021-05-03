package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.GoogleMapPictureAdapter;
import com.example.travelbuddyv2.googleMapAPICall.PlaceTask;
import com.example.travelbuddyv2.model.Destination;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AttractionDetailActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String tag = "MAP_ACTIVITY";
    List<Bitmap> bitmapList;
    private SlidingUpPanelLayout googleMapInformationLayout;
    private MaterialSearchBar materialSearchBar;
    private Button btnAddTrip, btnSearchForAttraction, btnAddAttractionOnSwipeUp, btnHideAttraction;
    private TextView locationName, locationAddress, locationRating, locationWorkingHour, locationPhoneNumber, locationWebsite;
    private ImageView locationSinglePic;
    private RatingBar locationStarRating;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private View mapView;
    private List<AutocompletePrediction> predictionList;
    private double currentLat,currentLng;
    private GoogleMap mMap;
    PlacesClient placesClient;
    GoogleMapPictureAdapter googleMapPictureAdapter;
    RecyclerView rcvGoogleMapPictures;
    View locationButton;
    SupportMapFragment mapFragment;
    private String placeIDFromPreviousActivity , placeNameFromPreviousActivity;
    private double placeLatFromPreviousActivity, placeLngFromPreviousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapView = mapFragment.getView();
            mapFragment.getMapAsync(this);
        }
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            placeIDFromPreviousActivity = bundle.getString("PLACEID");
            placeNameFromPreviousActivity = bundle.getString("PLACENAME");
            placeLatFromPreviousActivity = bundle.getDouble("PLACELAT");
            placeLngFromPreviousActivity = bundle.getDouble("PLACELNG");
        }
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setEnabled(false);
        googleMapInformationLayout = findViewById(R.id.sliding_layout);
        initPlaceInformationLayout();
        bottomSheetBehavior();
        btnAddTrip = findViewById(R.id.mapFragmentBtnAddTripDetail);
        btnSearchForAttraction = findViewById(R.id.btnSearchForAttraction);
        btnHideAttraction = findViewById(R.id.btnHideAttraction);
        btnAddAttractionOnSwipeUp = findViewById(R.id.mapFragmentBtnAddTripDetailOnSwipeUp);
        btnAddAttractionOnSwipeUp.setVisibility(View.GONE);
        btnAddTrip.setVisibility(View.GONE);
        btnSearchForAttraction.setVisibility(View.GONE);
        bitmapList = new ArrayList<>();
        rcvGoogleMapPictures = findViewById(R.id.googleMapPicturesRecyclerView);
        rcvGoogleMapPictures.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        googleMapPictureAdapter = new GoogleMapPictureAdapter(bitmapList);
        rcvGoogleMapPictures.setAdapter(googleMapPictureAdapter);
        Places.initialize(this, "AIzaSyA9ND3V5NWS18Gr0sIjO-e1A3hPF1uONAw");
        placesClient = Places.createClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        LatLng attractionLatLng = new LatLng(placeLatFromPreviousActivity,placeLngFromPreviousActivity);
        showBottomLayoutWithDetail(placeIDFromPreviousActivity,placeNameFromPreviousActivity,attractionLatLng);

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
        mapView = mapFragment.getView();
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(AttractionDetailActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            behaviorWhenLocationPermissionIsGiven(googleMap);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(tag, "GET PERMISSION RESULT");
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(tag, "PERMISSION GRANTED");
                    if (ContextCompat.checkSelfPermission(AttractionDetailActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(tag, "ACCESS_FIND_LOCATION GRANTED");
                        getDeviceLocation();
                        behaviorWhenLocationPermissionIsGiven(mMap);
                    }
                } else {
                    Log.d(tag, "PERMISSION DENIED");
                    behaviorWhenLocationPermissionIsNotGiven();
                }
        }
    }

    public void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(AttractionDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AttractionDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(AttractionDetailActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }else{
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Log.d(tag,"GET DEVICE LOCATION SUCCESSFUL");
                        lastKnownLocation = task.getResult();
                        if(lastKnownLocation!=null){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()),15));
                            currentLat = lastKnownLocation.getLatitude();
                            currentLng = lastKnownLocation.getLongitude();
                        }
                    } else {
                        Toast.makeText(AttractionDetailActivity.this, "Unable to get last location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void behaviorWhenLocationPermissionIsNotGiven() {
        Log.d(tag, "PERMISSION IS NOT GIVEN PROCEED WITH HIDE BUTTON");
        btnSearchForAttraction.setVisibility(View.GONE);
    }

    @SuppressLint("MissingPermission")
    private void behaviorWhenLocationPermissionIsGiven(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        Log.d(tag, String.valueOf(googleMap.isMyLocationEnabled()));
        if(mapView==null) {
            Log.d(tag,"Map view null") ;
        }else{
            Log.d(tag,"Map view is not null");
        }
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 80);
            Log.d(tag,"Tryna locate get current location button");
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(AttractionDetailActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(tag,"TRY TO GET DEVICE LOCATION");
                getDeviceLocation();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(AttractionDetailActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void initPlaceInformationLayout(){
        /*   locationName , locationAddress , locationRating , locationWorkingHour , locationPhoneNumber , location;*/
        locationName = findViewById(R.id.locationName);
        locationAddress = findViewById(R.id.locationAddress);
        locationRating = findViewById(R.id.locationRating);
        locationWorkingHour = findViewById(R.id.locationWorkingHour);
        locationPhoneNumber = findViewById(R.id.locationPhoneNumber);
        locationWebsite = findViewById(R.id.locationWebsite);
        locationStarRating = findViewById(R.id.locationStarRating);
        locationSinglePic = findViewById(R.id.locationSinglePic);
    }

    private void bottomSheetBehavior(){

        //we hide when user first enter the map fragment
        googleMapInformationLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        googleMapInformationLayout.setEnabled(false);

    }

    private void showBottomLayoutWithDetail(final String placeID, final String placeName, final LatLng placeLatLng){
        final List<Place.Field> placeInformation = Arrays.asList(Place.Field.ADDRESS,Place.Field.RATING,Place.Field.OPENING_HOURS,
                Place.Field.PHONE_NUMBER,Place.Field.RATING, Place.Field.WEBSITE_URI);
        final FetchPlaceRequest placeInformationRequest = FetchPlaceRequest.newInstance(placeID,placeInformation);
        placesClient.fetchPlace(placeInformationRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                final Place place = fetchPlaceResponse.getPlace();
                locationAddress.setText("N/A");
                locationRating.setText("N/A");
                locationWorkingHour.setText("N/A");
                locationPhoneNumber.setText("N/A");
                locationStarRating.setNumStars(0);
                locationWebsite.setText("N/A");
                if(place.getAddress()!=null){
                    String address = place.getAddress();
                    locationAddress.setText(address);
                }
                if(place.getRating()!=null && place.getUserRatingsTotal()!=null){
                    Double rating = place.getRating();
                    int numberOfRater = place.getUserRatingsTotal();
                    locationStarRating.setRating(rating.floatValue());
                    locationRating.setText(String.format("%s %d", rating, numberOfRater));
                }
                if(place.getOpeningHours()!=null){
                    OpeningHours workingHour = place.getOpeningHours();
                    int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    today= today-2;
                    if(today == -1){
                        today = 6;
                    }
                    System.out.println("kataikataikatai" + today);
                    locationWorkingHour.setText(workingHour.getWeekdayText().get(today));
                }
                if(place.getPhoneNumber()!=null){
                    String phone = place.getPhoneNumber();
                    locationPhoneNumber.setText(phone);
                }
                if(place.getWebsiteUri()!=null){
                    Uri website = place.getWebsiteUri();
                    locationWebsite.setText(website.toString());

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                locationAddress.setText("N/A");
                locationRating.setText("N/A");
                locationWorkingHour.setText("N/A");
                locationPhoneNumber.setText("N/A");
                locationStarRating.setNumStars(0);
                locationWebsite.setText("N/A");
            }
        });

        //clear list to display new image every time user click on POI
        bitmapList.clear();
        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);
        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeID, fields);
        placesClient.fetchPlace(placeRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                final Place place = fetchPlaceResponse.getPlace();
                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                if (metadata!=null && metadata.size() != 0) {
                    for (int i = 0; i < metadata.size(); i++) {
                        if (i == 5)
                            break;
                        final PhotoMetadata photoMetadata = metadata.get(i);
                        final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                .setMaxWidth(852)
                                .setMaxHeight(480)
                                .build();
                        final int cnt = i;
                        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                            @Override
                            public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                if(cnt==0){
                                    locationSinglePic.setImageBitmap(bitmap);
                                }else{
                                    bitmapList.add(bitmap);
                                    googleMapPictureAdapter.notifyDataSetChanged();
                                }
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
                }else{
                    locationSinglePic.setImageResource(R.drawable.ic_baseline_photo_24);
                }
            }
        });

        googleMapInformationLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        locationName.setText(placeName);
    }


}