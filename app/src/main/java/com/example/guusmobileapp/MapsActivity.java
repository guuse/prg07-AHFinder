package com.example.guusmobileapp;

import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

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

    int titleposition = 0;
    String[] stores =  {

    };

    public double LocationLat;
    public double LocationLong;
    public double AhLat;
    public double AhLong;

    MediaPlayer mediaPlayer;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.background);

        if (SettingsActivity.soundCheck(this)) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.stop();
        }

        Intent intent = getIntent();
        LocationLat = intent.getDoubleExtra("LocationLat", 0);
        LocationLong = intent.getDoubleExtra("LocationLong", 0);
        AhLat = intent.getDoubleExtra("AhLat", 0);
        AhLong = intent.getDoubleExtra("AhLong", 0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng current = new LatLng(LocationLat, LocationLong);
        LatLng AH = new LatLng(AhLat,AhLong);
        mMap.setMinZoomPreference(14.5f);
        mMap.setMaxZoomPreference(25.0f);
        mMap.addMarker(new MarkerOptions().position(current).title("Home"));
        mMap.addMarker(new MarkerOptions().position(AH).title("Albert Heijn"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.background);

        if (SettingsActivity.soundCheck(this)) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
