package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.GoogleMapPictureAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MapsFragment extends Fragment {


    private final String tag = "MAP_FRAGMENT";

    List <String> tmp;

    List <Integer> tmpInt;

    int tripDetailID = 0;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private PlacesClient placesClient;

    private List<AutocompletePrediction> predictionList;

    private Location lastKnownLocation;

    private LocationCallback locationCallback;

    private MaterialSearchBar materialSearchBar;

    private View mapView;

    private GoogleMap map;

    private View googleMapInformationLayout;

    private GoogleMapPictureAdapter googleMapPictureAdapter;

    private RecyclerView rcvGoogleMapPics;

    private Button btnAddTrip;

    private List<Bitmap> bitmapList;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */


        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                //   requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION,);

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }

            googleMap.setMyLocationEnabled(true);


            if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {



                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 40, 180);
            }

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

            task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    getDeviceLocation();
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        try {
                            resolvable.startResolutionForResult(getActivity(), 51);
                        } catch (IntentSender.SendIntentException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });

            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (materialSearchBar.isSuggestionsVisible()) {
                        materialSearchBar.clearSuggestions();
                    }
                    if (materialSearchBar.isSearchOpened()) {
                        materialSearchBar.closeSearch();
                    }
                    return false;
                }
            });

            googleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
                @Override
                public void onPoiClick(PointOfInterest pointOfInterest) {
                    final String placeName = pointOfInterest.name;
                    final String placeId = pointOfInterest.placeId;
                    final LatLng placeLatLng = pointOfInterest.latLng;
                    Toast.makeText(getActivity(), placeName, Toast.LENGTH_SHORT).show();

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
                                Toast.makeText(getContext(), "No metadata", Toast.LENGTH_SHORT).show();
                            }
                            if (metadata!=null && metadata.size() != 0) {

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
                                            Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                            bitmapList.add(bitmap);
                                            googleMapPictureAdapter.notifyDataSetChanged();
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


                    googleMapInformationLayout.setVisibility(View.VISIBLE);
                    TextView tv = googleMapInformationLayout.findViewById(R.id.tvPlaceName);
                    tv.setText(placeName);

                    btnAddTrip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getContext(),TripSelectionActivity.class);
                            i.putExtra("googleMapPlaceName",placeName);
                            i.putExtra("googleMapPlaceID",placeId);
                            i.putExtra("googleMapPlaceLat",placeLatLng.latitude);
                            i.putExtra("googleMapPlaceLong",placeLatLng.longitude);
                            startActivity(i);
                        }
                    });


                }
            });

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_maps, container, false);

        materialSearchBar = root.findViewById(R.id.searchBar);

//        getCurrentTripDetailIdFromFirebaseDatabase();

        googleMapInformationLayout = root.findViewById(R.id.googleMapInformationLayout);
        googleMapInformationLayout.setVisibility(View.GONE);
        bitmapList = new ArrayList<>();
        googleMapPictureAdapter = new GoogleMapPictureAdapter(bitmapList);
        rcvGoogleMapPics = googleMapInformationLayout.findViewById(R.id.googleMapPicturesRecyclerView);
        rcvGoogleMapPics.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        rcvGoogleMapPics.setAdapter(googleMapPictureAdapter);

        btnAddTrip = googleMapInformationLayout.findViewById(R.id.mapFragmentBtnAddTripDetail);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        Places.initialize(getContext(),"AIzaSyA9ND3V5NWS18Gr0sIjO-e1A3hPF1uONAw");

        placesClient = Places.createClient(getContext());

        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();




        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Toast.makeText(getContext(),text.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.closeSearch();
                }

            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("HU")
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {

                           predictionList = findAutocompletePredictionsResponse.getAutocompletePredictions();
                        List<String> suggestionList = new ArrayList<>();
                        for(int i=0;i<predictionList.size();i++){
                            AutocompletePrediction prediction = predictionList.get(i);
                            suggestionList.add(prediction.getFullText(null).toString());
                        }
                        materialSearchBar.updateLastSuggestions(suggestionList);
                        if(!materialSearchBar.isSuggestionsVisible()){
                            materialSearchBar.showSuggestionsList();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof ApiException){
                            ApiException apiException = (ApiException) e;
                            Toast.makeText(getContext(),String.valueOf(apiException.getStatusCode()),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if(position >= predictionList.size()){
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                },1000);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                if(imm!=null){
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);
                }



                final String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();

                        LatLng latLngOfPlace = place.getLatLng();
                        if (latLngOfPlace != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, 15));
                        }
                        map.addMarker(new MarkerOptions().position(latLngOfPlace).title("Marker"));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode = apiException.getStatusCode();
                          //  Log.i("mytag", "place not found: " + e.getMessage());
                         //   Log.i("mytag", "status code: " + statusCode);
                        }
                    }
                });

            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });



        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
            mapView = mapFragment.getView();
        }

        //Toast.makeText(getContext(),"This is map",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }

    }

    public void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    lastKnownLocation = task.getResult();
                    if(lastKnownLocation!=null){
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()),15));
                    }
                } else {
                    Toast.makeText(getContext(), "Unable to get last location", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void tripDetailIDIncrement(){
        tripDetailID++;
    }

    private void getCurrentTripDetailIdFromFirebaseDatabase(){

        tmp = new ArrayList<>();
        tmpInt = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("t1");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                 //   tmp.add(data.getKey());
                    tmpInt.add(StringToInt(data.getKey()));
                    Log.d(tag,"KEY FROM FIREBASE " + data.getKey());
                }
              //  Collections.sort(tmp);
                Collections.sort(tmpInt);
              /*  for(String k:tmp){
                    Log.d(tag,"as String " +  k);
                }*/
               /* for(Integer i:tmpInt){
                    Log.d(tag,"as Integer " + i );
                }*/
                if(tmpInt.size()!=0){
                    int size = tmpInt.get(tmpInt.size()-1);
                  Log.d(tag,"latest tripDetail ID " + size);

                    tripDetailID = size+1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private int StringToInt(String ID){

        StringBuilder tmp = new StringBuilder();

        for(int i=2;i<ID.length();i++){
            tmp.append(ID.charAt(i));
        }

        return Integer.parseInt(tmp.toString());

    }





}