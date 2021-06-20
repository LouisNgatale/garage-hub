package com.louisngatale.garagehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditLocationActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private static final String KEY_CAMERA_POSITION = "camera_position",
            KEY_LOCATION = "location";
    private Button save_location;
    private GoogleMap map;
    private Geocoder geocoder;

    // A default location and default zoom to use when location permission is not grantend
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Double latitude, longitude;
    private MarkerOptions options, yourLocation, destination;
    private final String TAG = "MAPS";
    private CameraPosition cameraPosition;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    private HashMap<String,String> address;

    Marker position;

    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        geocoder = new Geocoder(this, Locale.getDefault());
        save_location = findViewById(R.id.save_location);

        // [START_EXCLUDE silent]
        // [START maps_current_place_on_create_save_instance_state]
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // [END maps_current_place_on_create_save_instance_state]
        // [END_EXCLUDE]

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.edit_location_map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }


    // Disable the location update button
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    // TODO: Get the clicked location address and update the marker position
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    // TODO: Get the new location and update the variable accordingly
    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    // When the map is ready, update the user interface
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Your Garage"));

        addMarker(googleMap);


        updateLocationUI();

        getDeviceLocation();

        // Add click listener to update the current location when the user clicks on the map
        googleMap.setOnMapClickListener(latLng -> {

            latitude = latLng.latitude;
            longitude = latLng.longitude;

            position.setPosition(latLng);
            position.setTitle("Your Garage");

        });
    }

    @SuppressLint("ObsoleteSdkInt")
    private void addMarker(GoogleMap googleMap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //  Get location
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null){
                                    // Get latitude and longitude
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();

                                    //Create new latlng object
                                    LatLng latLng = new LatLng(latitude,longitude);

                                    //Add latlng to new options object
                                    MarkerOptions options = new MarkerOptions()
                                            .position(latLng)
                                            .title("Your Garage");

                                    //Add marker to map
                                    if(position == null){
                                        position = googleMap.addMarker(options);
                                    }
                                    else {
                                        position.setPosition(latLng);
                                    }

                                    // Set the initial camera position
                                    googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng , 14.0f) );

                                    /*// Decode the address
                                    try {
                                        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                        address.put("Address",addresses.get(0).getAddressLine(0)); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                        address.put("State",addresses.get(0).getAdminArea());
                                        address.put("Country",addresses.get(0).getCountryName());
                                        address.put("Postal Code",addresses.get(0).getPostalCode());
                                        address.put("Known Name",addresses.get(0).getFeatureName());

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }*/
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d(TAG, "onFailure: " + e);
                                Log.d(TAG, "onFailure: Failed to get current location" );

                                Toast.makeText(EditLocationActivity.this, "Couldn't get current location", Toast.LENGTH_SHORT).show();
                            }
                        });

            }else{
                Toast.makeText(this, "Request denied", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Else check", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocationUI() {
        if (map == null) return;
        try {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    // This function gets the current location of the user and updates the map accordingly
    public void getDeviceLocation() {
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()){
                    lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null){
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                        options = new MarkerOptions()
                                .position(new LatLng(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude()))
                                .title("Your Location");
                    }else {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });
        }catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage(),e);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_location:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Latitude",latitude);
                returnIntent.putExtra("Longitude",longitude);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                break;
            default:
                break;
        }
    }
}