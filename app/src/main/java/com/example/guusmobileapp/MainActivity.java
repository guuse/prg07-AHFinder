package com.example.guusmobileapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.background);

        if (SettingsActivity.soundCheck(this)) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.stop();
        }
    }

    public void findStore(View view)
    {
        Intent intent = new Intent(MainActivity.this, FindActivity.class);
        startActivity(intent);
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

    public void settings(View view)
    {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
