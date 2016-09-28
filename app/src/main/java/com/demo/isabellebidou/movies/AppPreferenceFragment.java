package com.demo.isabellebidou.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_LONG;


public class AppPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{



    public AppPreferenceFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {



        if (key.equals("pref_offline")) {

            boolean offline = sharedPreferences.getBoolean("pref_offline",true);

            String result= String.valueOf(offline);
            /*Toast.makeText(getActivity().getBaseContext(),
                    result,
                    LENGTH_LONG).show();*/
            MainListActivity.setOffline(offline);

        //    Log.d("AppPreferenceFragment", "onSharedPreferenceChanged: "+result);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
