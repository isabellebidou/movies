package com.demo.isabellebidou.movies;

/**
 * Created by isabelle on 14/09/16.
 */

import android.annotation.TargetApi;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.regex.Pattern;


/**
 * Created by isabelle on 17/09/16.
 */
public class GetJson extends AsyncTask<Void, Void, String> {


    public GetJson() {
        super();
    }

    public GetJson(FileOutputStream storageFile) {
        internalStorageFile = storageFile;
    }

    private static FileOutputStream internalStorageFile;


    private String urlstr = "http://demo.clipbucket.com/api_public/getVideos/";
    private URL url;



    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(Void... arg0) {


        // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        // Making a request to url and getting response
        String jsonStr = null;


        try {
            url = new URL(urlstr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            jsonStr = sh.getDataMovies(url);
            Log.d("GETJSON","jsonStr: "+jsonStr);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonStr;


    }



    @Override
    protected void onPostExecute(String str) {
        super.onPostExecute(str);

        if (str.length()>=0) {


            MainListActivity.parseStringAndCreateVideosObjects(str);
        }


    }

}

