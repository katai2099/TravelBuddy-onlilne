package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.GoogleMapPictureAdapter;
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
import com.google.android.libraries.places.api.model.DayOfWeek;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class MapsFragment extends Fragment {

    private boolean isWorkingButton = true;
    private boolean isHideAttractionButtonHide = true;
    private SlidingUpPanelLayout googleMapInformationLayout;
    private final String tag = "MAP_FRAGMENT";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private Location lastKnownLocation;
    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private GoogleMap map;
    private GoogleMapPictureAdapter googleMapPictureAdapter;
    private RecyclerView rcvGoogleMapPics;
    private Button btnAddTrip, btnSearchForAttraction, btnAddAttractionOnSwipeUp, btnHideAttraction;
    private List<Bitmap> bitmapList;
    private TextView locationName, locationAddress, locationRating, locationWorkingHour, locationPhoneNumber, locationWebsite;
    private RatingBar locationStarRating;
    private double currentLat, currentLng;
    private View locationButton;
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
        public void onMapReady(final GoogleMap googleMap) {
            map = googleMap;

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d(tag, "ASKING FOR PERMISSION");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                behaviorWhenLocationPermissionIsGiven(googleMap);
            }
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    googleMapInformationLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    //  btnSearchForAttraction.setVisibility(View.VISIBLE);
                    setButtonVisible();
                    if (materialSearchBar.isSearchOpened()) {
                        materialSearchBar.clearSuggestions();
                        materialSearchBar.closeSearch();
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
                    setButtonGone();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), 0);
                    }
                    materialSearchBar.closeSearch();
                    materialSearchBar.clearSuggestions();
                    final String placeName = pointOfInterest.name;
                    final String placeId = pointOfInterest.placeId;
                    final LatLng placeLatLng = pointOfInterest.latLng;
                    Toast.makeText(getActivity(), placeName, Toast.LENGTH_SHORT).show();
                    showBottomLayoutWithDetail(placeId, placeName, placeLatLng);
                }
            });
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d(tag, marker.getSnippet());
                    setButtonGone();
                    showBottomLayoutWithDetail(marker.getSnippet(), marker.getTitle(), marker.getPosition());
                    return true;
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
        googleMapInformationLayout = root.findViewById(R.id.sliding_layout);
        initPlaceInformationLayout(root);
        bottomSheetBehavior();
        bitmapList = new ArrayList<>();
        googleMapPictureAdapter = new GoogleMapPictureAdapter(bitmapList);
        rcvGoogleMapPics = googleMapInformationLayout.findViewById(R.id.googleMapPicturesRecyclerView);
        rcvGoogleMapPics.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rcvGoogleMapPics.setAdapter(googleMapPictureAdapter);
        btnAddTrip = root.findViewById(R.id.mapFragmentBtnAddTripDetail);
        btnSearchForAttraction = root.findViewById(R.id.btnSearchForAttraction);
        btnHideAttraction = root.findViewById(R.id.btnHideAttraction);
        btnAddAttractionOnSwipeUp = root.findViewById(R.id.mapFragmentBtnAddTripDetailOnSwipeUp);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        Places.initialize(getContext(), "AIzaSyA9ND3V5NWS18Gr0sIjO-e1A3hPF1uONAw");
        placesClient = Places.createClient(getContext());
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        btnSearchForAttraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationButton.performClick();
                findNearByAttraction();
                btnSearchForAttraction.setVisibility(View.GONE);
                btnHideAttraction.setVisibility(View.VISIBLE);
                isHideAttractionButtonHide = false;
            }
        });
        btnHideAttraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.clear();
                btnHideAttraction.setVisibility(View.GONE);
                btnSearchForAttraction.setVisibility(View.VISIBLE);
                isHideAttractionButtonHide = true;
            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (enabled && (googleMapInformationLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        googleMapInformationLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)) {
                    Log.d(tag, "Search typed");
                    googleMapInformationLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }
                if (enabled && (btnHideAttraction.getVisibility() == View.VISIBLE || btnSearchForAttraction.getVisibility() == View.VISIBLE)) {
                    setButtonGone();
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
//                Toast.makeText(getContext(),text.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.closeSearch();
                    materialSearchBar.clearSuggestions();
                }
            }
        });
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String countryCode = "HU";
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                try {
                    List<Address> addresses = geocoder.getFromLocation(currentLat, currentLng, 1);
                    if (addresses.size() != 0) {
                        Address obj = addresses.get(0);
                        countryCode = obj.getCountryCode();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(tag, countryCode);
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry(countryCode)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                        predictionList = findAutocompletePredictionsResponse.getAutocompletePredictions();
                        List<String> suggestionList = new ArrayList<>();
                        for (int i = 0; i < predictionList.size(); i++) {
                            AutocompletePrediction prediction = predictionList.get(i);
                            suggestionList.add(prediction.getFullText(null).toString());
                        }
                        materialSearchBar.updateLastSuggestions(suggestionList);
                        if (!materialSearchBar.isSuggestionsVisible()) {
                            materialSearchBar.showSuggestionsList();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Toast.makeText(getContext(), String.valueOf(apiException.getStatusCode()), Toast.LENGTH_SHORT).show();
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
                if (position >= predictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                materialSearchBar.clearSuggestions();
                setButtonGone();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                final String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        LatLng latLngOfPlace = place.getLatLng();
                        String placeName = place.getName();
                        if (latLngOfPlace != null && placeName != null) {
                            map.clear();
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.title(placeName);
                            markerOptions.snippet(placeId);
                            markerOptions.position(latLngOfPlace);
                            map.addMarker(markerOptions);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, 15));
                            showBottomLayoutWithDetail(placeId, placeName, latLngOfPlace);
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
    }

    /*  @Override
      public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);
          if (requestCode == 51) {
              if (resultCode == RESULT_OK) {
                  getDeviceLocation();
              }
          }
      }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(tag, "GET PERMISSION RESULT");
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(tag, "PERMISSION GRANTED");
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(tag, "ACCESS_FIND_LOCATION GRANTED");
                        getDeviceLocation();
                    }
                } else {
                    Log.d(tag, "PERMISSION DENIED");
                    behaviorWhenLocationPermissionIsNotGiven();
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

        } else {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15));
                            currentLat = lastKnownLocation.getLatitude();
                            currentLng = lastKnownLocation.getLongitude();
                        }
                    } else {
                        Toast.makeText(getContext(), "Unable to get last location", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void initPlaceInformationLayout(View googleMapInformationLayout) {
        /*   locationName , locationAddress , locationRating , locationWorkingHour , locationPhoneNumber , location;*/
        locationName = googleMapInformationLayout.findViewById(R.id.locationName);
        locationAddress = googleMapInformationLayout.findViewById(R.id.locationAddress);
        locationRating = googleMapInformationLayout.findViewById(R.id.locationRating);
        locationWorkingHour = googleMapInformationLayout.findViewById(R.id.locationWorkingHour);
        locationPhoneNumber = googleMapInformationLayout.findViewById(R.id.locationPhoneNumber);
        locationWebsite = googleMapInformationLayout.findViewById(R.id.locationWebsite);
        locationStarRating = googleMapInformationLayout.findViewById(R.id.locationStarRating);
    }

    private void bottomSheetBehavior() {
        //we hide when user first enter the map fragment
        googleMapInformationLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        googleMapInformationLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                btnAddTrip.setVisibility(View.GONE);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    btnAddTrip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showBottomLayoutWithDetail(final String placeID, final String placeName, final LatLng placeLatLng) {
        final List<Place.Field> placeInformation = Arrays.asList(Place.Field.ADDRESS, Place.Field.RATING, Place.Field.OPENING_HOURS,
                Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.WEBSITE_URI);
        final FetchPlaceRequest placeInformationRequest = FetchPlaceRequest.newInstance(placeID, placeInformation);
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
                if (place.getAddress() != null) {
                    String address = place.getAddress();
                    locationAddress.setText(address);
                }
                if (place.getRating() != null && place.getUserRatingsTotal() != null) {
                    Double rating = place.getRating();
                    int numberOfRater = place.getUserRatingsTotal();
                    locationStarRating.setRating(rating.floatValue());
                    locationRating.setText(String.format("%s %d", rating, numberOfRater));
                }
                if (place.getOpeningHours() != null) {
                    OpeningHours workingHour = place.getOpeningHours();
                    int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    today = today - 2;
                    if (today == -1) {
                        today = 6;
                    }
                    System.out.println("kataikataikatai" + today);
                    locationWorkingHour.setText(workingHour.getWeekdayText().get(today));
                }
                if (place.getPhoneNumber() != null) {
                    String phone = place.getPhoneNumber();
                    locationPhoneNumber.setText(phone);
                }
                if (place.getWebsiteUri() != null) {
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
                // Toast.makeText(getApplicationContext(),place.toString(),Toast.LENGTH_SHORT).show();
                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                if (metadata == null || metadata.isEmpty()) {
                    Toast.makeText(getContext(), "No metadata", Toast.LENGTH_SHORT).show();
                }
                if (metadata != null && metadata.size() != 0) {

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
        googleMapInformationLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        locationName.setText(placeName);
        btnAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), TripSelectionActivity.class);
                i.putExtra("googleMapPlaceName", placeName);
                i.putExtra("googleMapPlaceID", placeID);
                i.putExtra("googleMapPlaceLat", placeLatLng.latitude);
                i.putExtra("googleMapPlaceLong", placeLatLng.longitude);
                i.putExtra("googleMapAddress", locationAddress.getText().toString());
                startActivity(i);
            }
        });
        btnAddAttractionOnSwipeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), TripSelectionActivity.class);
                i.putExtra("googleMapPlaceName", placeName);
                i.putExtra("googleMapPlaceID", placeID);
                i.putExtra("googleMapPlaceLat", placeLatLng.latitude);
                i.putExtra("googleMapPlaceLong", placeLatLng.longitude);
                i.putExtra("googleMapAddress", locationAddress.getText().toString());
                startActivity(i);
            }
        });
    }
    private void findNearByAttraction() {

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + currentLat + "," + currentLng +
                "&radius=1000" +
                "&type=tourist_attraction" +
                "&key=AIzaSyA9ND3V5NWS18Gr0sIjO-e1A3hPF1uONAw";
        new PlaceTask().execute(url);
    }
    private class PlaceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }
    private String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        Log.d(tag, "download url data " + data);
        return data;
    }

    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //create json parser class
            JsonParser jsonParser = new JsonParser();
            //initialize hash map list
            List<HashMap<String, String>> mapList = null;
            //initialize json object
            try {
                JSONObject object = new JSONObject(strings[0]);
                //parse json object
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            map.clear();
            Log.d(tag, "I am here after clearing map");
            for (int i = 0; i < hashMaps.size(); i++) {
                HashMap<String, String> hashMapList = hashMaps.get(i);
                double lat = Double.parseDouble(hashMapList.get("lat"));
                double lng = Double.parseDouble(hashMapList.get("lng"));
                String name = hashMapList.get("name");
                String placeID = hashMapList.get("placeID");
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(name);
                options.snippet(placeID);
                map.addMarker(options);
                Log.d(tag, "I am here adding marker");
            }
        }
    }
    private void setButtonVisible() {
        if (btnSearchForAttraction.getVisibility() == View.GONE && isHideAttractionButtonHide && isWorkingButton) {
            btnSearchForAttraction.setVisibility(View.VISIBLE);
        } else if (btnHideAttraction.getVisibility() == View.GONE && btnSearchForAttraction.getVisibility() == View.GONE && isWorkingButton) {
            btnHideAttraction.setVisibility(View.VISIBLE);
        }
    }
    private void setButtonGone() {
        if (btnSearchForAttraction.getVisibility() == View.VISIBLE) {
            btnSearchForAttraction.setVisibility(View.GONE);
        } else if (btnHideAttraction.getVisibility() == View.VISIBLE) {
            btnHideAttraction.setVisibility(View.GONE);
        }
    }
    private void behaviorWhenLocationPermissionIsNotGiven() {
        Log.d(tag, "PERMISSION IS NOT GIVEN PROCEED WITH HIDE BUTTON");
        btnSearchForAttraction.setVisibility(View.GONE);
        isWorkingButton = false;
    }
    @SuppressLint("MissingPermission")
    private void behaviorWhenLocationPermissionIsGiven(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
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
    }
}