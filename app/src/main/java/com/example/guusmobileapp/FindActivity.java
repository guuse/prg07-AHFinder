package com.example.guusmobileapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;

public class FindActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback ;

    public final static String LOG_TAG = "AH FINDER: ";
    public final static String REQUEST_URI = "http://guusekkelenkamp.nl/prg09/index.json";
    public final static int REQUEST_LAST_LOCATION_PERMISSION = 1;
    public final static int REQUEST_LOCATION_UPDATES_PERMISSION = 2;

    public double LocationLat;
    public double LocationLong;
    public double AhLong;
    public double AhLat;
    public Button mapBtn;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        mapBtn = (Button) findViewById(R.id.map);
        mapBtn.setVisibility(View.GONE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.background);

        if (SettingsActivity.soundCheck(this)) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.stop();
        }

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
        intent.putExtra("AhLong", AhLong);
        intent.putExtra("AhLat", AhLat);
        startActivity(intent);
    }

    public void findAh(View view) {
        RequestQueue requestQueue;

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache, network);

        requestQueue.start();

        JsonObjectRequest volleyRequest = createRequest();

        requestQueue.add(volleyRequest);
    }

    private JsonObjectRequest createRequest() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, REQUEST_URI, (String)null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "Response: " + response.toString());
                        updateAfterRequest(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "Volley request failed: " + error.getMessage());
                    }
                });

        return jsonObjectRequest;
    }

    public void updateAfterRequest(JSONObject locationJson) {
        try {
            AhLat = locationJson.getDouble("latitude");
            AhLong = locationJson.getDouble("longitude");
        } catch (JSONException e) {
            Log.e(LOG_TAG, "unexpected JSON exception", e);
        }
        mapBtn.setVisibility(View.VISIBLE);
        Log.d(LOG_TAG, "AH found lat: " +  AhLat);
        Log.d(LOG_TAG, "AH found long: " +  AhLong);
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.background);

        if (SettingsActivity.soundCheck(this)) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onPause(){
        fusedLocationClient.removeLocationUpdates(locationCallback);
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void settings(View view)
    {
        Intent intent = new Intent(FindActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
