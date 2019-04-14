package com.example.guusmobileapp;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {

    public final static String PREF_SOUND = "pref_soundOn";
    public final static boolean PREF_SOUND_DEFAULT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    public static boolean soundCheck(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_SOUND,PREF_SOUND_DEFAULT);
    }

}
