package com.demo.isabellebidou.movies;

import android.os.AsyncTask;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by isabelle on 19/09/16.
 */
public class ReadFromStorage extends AsyncTask<Void, Void, String> {

    public ReadFromStorage( FileInputStream fis){
        fileInputStream=fis;


    }

    protected FileInputStream fileInputStream;

    @Override
    protected String doInBackground(Void... arg0) {

        String temp = "";

        try {

            int c;

            while ((c = fileInputStream.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }

            Log.d("BUFFER", temp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return temp;


    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        MainListActivity.parseStringAndCreateVideosObjects(s);

    }
}
