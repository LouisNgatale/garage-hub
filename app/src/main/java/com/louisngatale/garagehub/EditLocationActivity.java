package com.louisngatale.garagehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class EditLocationActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private static final String KEY_CAMERA_POSITION = "camera_position",
            KEY_LOCATION = "location";
    private Button Save;
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

    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        geocoder = new Geocoder(this, Locale.getDefault());

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
                .findFragmentById(R.id.map);
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
                .title("Your Location"));

        updateLocationUI();

        getDeviceLocation();
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
}