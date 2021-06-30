package com.louisngatale.garagehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.louisngatale.garagehub.adapters.CustomInfoWindowsAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements
            GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationButtonClickListener,
            GoogleMap.OnMyLocationClickListener,
            OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private Button dashboard;
    private GoogleMap map;
    private Geocoder geocoder;

    private FirebaseFirestore mDb;

    private ArrayList<HashMap<String,Object>> addresses = new ArrayList<>();

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Double latitude, longitude;
    private MarkerOptions options,yourLocation,destination;
    private final String TAG = "MAPS";
    private CameraPosition cameraPosition;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    // [START maps_current_place_state_keys]
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    // [END maps_current_place_state_keys]

    // [START maps_current_place_on_create]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDb = FirebaseFirestore.getInstance();

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_main);

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

        dashboard = findViewById(R.id.dashboard);

        dashboard.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null){
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
            }else {
                Intent dashboard = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(dashboard);
            }
        });

        // Build the map.
        // [START maps_current_place_map_fragment]
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        // [END maps_current_place_map_fragment]
        // [END_EXCLUDE]

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrieve garage locations from database
    }
    // [END maps_current_place_on_create]

    private void retrieve_addresses(GoogleMap googleMap) {
        mDb.collection("companies")
            .get().addOnCompleteListener(task -> {
                task.getResult().getDocuments().forEach(doc -> {
                    addresses.add((HashMap<String, Object>) doc.get("Address"));

                    //Create new latlng object
                    LatLng latLng = new LatLng(
                            (double) ((HashMap<?, ?>) doc.get("Address")).get("Latitude"),
                            (double) ((HashMap<?, ?>) doc.get("Address")).get("Longitude"));

                    //Add latlng to new options object
                    MarkerOptions options = new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_garage_black_36))
                            .title(doc.get("company").toString())
                            .snippet(doc.get("description").toString());

                    //Add marker to map
                    googleMap.addMarker(options).setTag(doc);
                });
        });
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    // [START maps_current_place_on_map_ready]
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnInfoWindowClickListener(this);

        map.setInfoWindowAdapter(new CustomInfoWindowsAdapter(MainActivity.this));

        //
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        retrieve_addresses(googleMap);

//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     * @Author: Louis Ngatale
     */
    // [START maps_current_place_get_device_location]
    private void getDeviceLocation() {
         /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                        // Get current location and add marker
                        options = new MarkerOptions()
                                .position(new LatLng(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude()))
                                .title("Your location");
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.you));
//                                map.addMarker(options);
                    }
                } else {
                    map.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                }
            });
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    // [END maps_current_place_get_device_location]

    /**
     * Saves the state of the map when the activity is paused.
     */
    // [START maps_current_place_on_save_instance_state]
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        DocumentSnapshot tag = (DocumentSnapshot) marker.getTag();

        Intent view_garage = new Intent(MainActivity.this, ViewGarage.class);
        view_garage.putExtra("id", tag.getId());
        startActivity(view_garage);
    }
    // [END maps_current_place_on_save_instance_state]
}