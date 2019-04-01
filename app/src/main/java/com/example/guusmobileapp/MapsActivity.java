package com.example.guusmobileapp;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    final private double[] latlist = {
            51.912650
    };
    final private double[] longlist = {
            4.458550
    };

    int titleposition = 0;
    String[] stores =  {

    };

    public double LocationLat;
    public double LocationLong;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        LocationLat = intent.getDoubleExtra("LocationLat", 0);
        LocationLong = intent.getDoubleExtra("LocationLong", 0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng current = new LatLng(LocationLat, LocationLong);
        LatLng AH = new LatLng(latlist[titleposition],longlist[titleposition]);
        mMap.setMinZoomPreference(14.5f);
        mMap.setMaxZoomPreference(25.0f);
        mMap.addMarker(new MarkerOptions().position(current).title("Home"));
        mMap.addMarker(new MarkerOptions().position(AH).title("Albert Heijn"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
    }
}