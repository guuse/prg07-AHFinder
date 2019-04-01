package com.example.guusmobileapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class FindActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback ;

    public final static String LOG_TAG = "AH FINDER: ";
    public final static int REQUEST_LAST_LOCATION_PERMISSION = 1;
    public final static int REQUEST_LOCATION_UPDATES_PERMISSION = 2;

    public double LocationLat;
    public double LocationLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(LOG_TAG, "Locatie onbekend");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d(LOG_TAG, "Locatie bekend");
                    Log.d(LOG_TAG, "Locatie:" + location.getLatitude()+","+location.getLongitude());
                    LocationLat = location.getLatitude();
                    LocationLong = location.getLongitude();
                }
            }

            ;
        };
        createLocationRequest();
        getLastKnownLocation();
    }

    public void openMap(View view) {
        Intent intent = new Intent(FindActivity.this, MapsActivity.class);
        intent.putExtra("LocationLat", LocationLat);
        intent.putExtra("LocationLong", LocationLong);
        startActivity(intent);
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LAST_LOCATION_PERMISSION);
            Log.d(LOG_TAG, "No location permission.");
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d(LOG_TAG, "Laatste locatie bekend.");
                        } else {
                            Log.d(LOG_TAG, "Laatste locatie niet bekend.");
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LAST_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "permissie granted");
                    getLastKnownLocation();
                } else {
                    Log.d(LOG_TAG, "Not granted");
                }
                break;
            case REQUEST_LOCATION_UPDATES_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "permissie granted (updates)");
                    startLocationUpdates();
                } else {
                    Log.d(LOG_TAG, "Not granted (updates)");
                }
                break;
        }
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_UPDATES_PERMISSION);

            Log.d(LOG_TAG, "Geen locatie permissie (update)");
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */);
    }

    @Override
    protected void onResume(){
        startLocationUpdates();
        super.onResume();
    }

    @Override
    protected void onPause(){
        fusedLocationClient.removeLocationUpdates(locationCallback);
        super.onPause();
    }
}
