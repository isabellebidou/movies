package com.demo.isabellebidou.movies;

/**
 * Created by isabelle on 14/09/16.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**

 * http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
 */
public class ServiceHandler {


    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;



    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */

    public String getDataMovies(URL url) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 100000;

        try {
            //URL url = new URL(url);
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(200000 /* milliseconds */);
            conn.setConnectTimeout(300000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            //   Log.d("debug", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readStream(is);
            //   Log.d("content",contentAsString );
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private static String readStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {

            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }



}