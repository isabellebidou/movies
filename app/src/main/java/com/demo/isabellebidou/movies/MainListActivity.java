package com.demo.isabellebidou.movies;
//http://codetheory.in/android-saving-files-on-internal-and-external-storage/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static android.widget.Toast.LENGTH_LONG;


public class MainListActivity extends AppCompatActivity {

    static final String VIDEOLIST = "videolist";
    static final String UPLOADERLIST = "uploaderlist";
    private static boolean offline = false;

    public static void setOffline(boolean offline) {
        MainListActivity.offline = offline;
    }

    BroadcastReceiver connectionReceiver;
    static FileOutputStream outputStream = null;
    FileInputStream inputStream = null;
    String FILENAME = "internalStorageFile";
    private static ListView lv;
    Context context;
    private static ArrayList<Video> videos = new ArrayList<com.demo.isabellebidou.movies.Video>();
    private static CustomAdapter adapter;


    private static CacheHandler cacheHandler;

    public static CacheHandler getCacheHandler() {
        return cacheHandler;
    }

    public static CustomAdapter getAdapter() {
        return adapter;
    }

    private static ArrayList<com.demo.isabellebidou.movies.Uploader> uploaders = new ArrayList<Uploader>();

    public static ArrayList<Uploader> getUploaders() {
        return uploaders;
    }

    public static void setUploaders(ArrayList<Uploader> uploaders) {
        MainListActivity.uploaders = uploaders;
    }

    public static ArrayList<Video> getVideos() {
        return videos;
    }

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        PreferenceManager.setDefaultValues(this, R.xml.fragment_preference, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        offline = preferences.getBoolean("pref_offline", true);

//context.getResources().getIdentifier(key_name, "string", context.getPackageName()));
      //  prefs.getString(key_name,context.getResources().getString(ResId);

        cacheHandler = new CacheHandler();
        cacheHandler.setUp();


        if (savedInstanceState != null) {
            // the app was just restarted
            Log.d("MAINLISTACTIVITY", "savedInstanceState!= null");
            videos = savedInstanceState.getParcelableArrayList(VIDEOLIST);


            ArrayList<Uploader> uploadersArrayList = savedInstanceState.getParcelableArrayList(UPLOADERLIST);


            setUploaders(uploadersArrayList);
            MainListActivity.getAdapter().refreshList(videos);

        } else if (offline && isInternalStorageFilePresent()) {
            //the app is used offline and pulls the json from the internal storage after the first launch


            Log.d("MAINLISTACTIVITY", "read from internal storage");
            try {
                inputStream = openFileInput(FILENAME);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            AsyncTask<Void, Void, String> read = new ReadFromStorage(inputStream).execute();

        } else {
            //the app gets the json data from server
            Log.d("MAINLISTACTIVITY", "gets the json data from server");

            if (offline) {
                try {


                    outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


            final ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = conMan.getActiveNetworkInfo();

            boolean isConnected = networkInfo != null &&
                    networkInfo.isConnectedOrConnecting();


            Log.d("MAINLISTACTIVITY", "savedInstanceState== null AND !isInternalStorageFilePresent");


            if (isConnected) {


                AsyncTask<Void, Void, String> getJson = new GetJson().execute();
            } else {
                promptForConnection();

                connectionReceiver = new BroadcastReceiver() {

                    public void onReceive(Context context, Intent intent) {
                        if (conMan.getActiveNetworkInfo() != null &&
                                conMan.getActiveNetworkInfo().isConnectedOrConnecting()) {

                            AsyncTask<Void, Void, String> getJson = new GetJson().execute();

                        }

                        unregisterReceiver(connectionReceiver);
                    }

                };

                registerReceiver(connectionReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

            }


        }


        context = this;

        lv = (ListView) findViewById(R.id.list);

        adapter = new CustomAdapter(this, videos);

        adapter.notifyDataSetChanged();


        lv.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences: {
                Intent intent = new Intent();
                intent.setClass(this, AppSettingsActivity.class);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d("MAINLISTACTIVITY", "onResume");
        adapter.refreshList(videos);
    }


    public static void parseStringAndCreateVideosObjects(String jsonStr) {

        JSONArray jsonArrayData = null;
        JSONObject jsonObject;


        try {

            jsonObject = new JSONObject(jsonStr);
            jsonArrayData = jsonObject.getJSONArray("data");


        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            videos.clear();
            for (int i = 0; i < jsonArrayData.length(); i++) {

                JSONObject movie = jsonArrayData.getJSONObject(i);
                JSONObject video = movie.getJSONObject("video");

                try {
                    JSONObject uploaderObject = movie.getJSONObject("uploader");
                    String dateStr = video.getString("date_added").trim();
                    String durationStrUnchanged = video.getString("duration").trim();
                    String split = durationStrUnchanged.split(Pattern.quote("."))[0];
                    Integer durationInSeconds = Integer.valueOf(split);

                    Integer durationSeconds = durationInSeconds % 60;
                    Integer durationMinutes = durationInSeconds / 60;
                    String durationStr = durationMinutes + ":" + durationSeconds;

                    String titleStr = video.getString("title").trim();
                    String viewsStr = video.getString("views").trim();
                    String movieIdStr = video.getString("videoid").trim();

                    JSONObject thumbsObject = video.getJSONObject("thumbs");
                    JSONObject filesObject;

                    filesObject = video.getJSONObject("files");


                    String thumbsUrlStr = thumbsObject.getString("default").trim();
                    String fileUrlStr = filesObject.getString("sd").trim();
                    String uploaderNameStr = uploaderObject.getString("name").trim();
                    String uploaderAvatarStr = uploaderObject.getString("avatar").trim();
                    String uploaderIdStr = uploaderObject.getString("userid").trim();
                    Uploader uploader;

                    if (!checkIfAlreadyAnUplauder(Integer.valueOf(uploaderIdStr))) {
                        uploader = new Uploader(Integer.valueOf(uploaderIdStr), uploaderNameStr, uploaderAvatarStr);


                        uploaders.add(uploader);

                    } else {

                        uploader = Uploader.findVideoUploaderByStringId(uploaderIdStr);


                    }


                    videos.add(new Video(Integer.valueOf(movieIdStr), viewsStr, titleStr, thumbsUrlStr, fileUrlStr, durationStr, dateStr, uploader));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("MAINLISTACTIVITY", "wrong json data for video id" + video.get("videoid"));
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();

                    continue;
                }


            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        getAdapter().refreshList(videos);
        Log.d("MAINLISTACTIVITY", "parseStringAndCreateVideosObjects videos.size()" + videos.size() + " ");

        if (offline) {


            AsyncTask<Void, Void, Void> write = new WriteToStorage(outputStream, jsonStr).execute();
        }

    }

    private static boolean checkIfAlreadyAnUplauder(Integer id) {
        boolean result = false;
        for (int j = 0; j < uploaders.size(); j++) {

            if (uploaders.get(j).getUserid().toString().equalsIgnoreCase(id.toString())) {
                result = true;
            }
        }


        return result;
    }


    public void promptForConnection() {
        Toast.makeText(getBaseContext(),
                "Internet connection required",
                LENGTH_LONG).show();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {


        Log.d("MAINLISTACTIVITY", "onSaveInstanceState videos.size()" + videos.size() + " ");

        Log.d("MAINLISTACTIVITY", "onSaveInstanceState uploadersArray.size" + videos.size() + " ");

        outState.putParcelableArrayList(UPLOADERLIST, getUploaders());
        outState.putParcelableArrayList(VIDEOLIST, getVideos());
        super.onSaveInstanceState(outState);
    }


    public boolean isInternalStorageFilePresent() {
        String path = getFilesDir().getAbsolutePath() + "/" + FILENAME;
        File file = new File(path);
        Log.d("MAINLISTACTIVITY", "file.exists()=" + file.exists());
        return file.exists();
    }


}
