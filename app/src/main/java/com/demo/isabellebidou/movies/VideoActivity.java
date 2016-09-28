package com.demo.isabellebidou.movies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    VideoView videoView;
    String videoFile;

    static final String VIDEOURL = "videourl";
    static final String VIDEOPOSITION = "videoposition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoView = (VideoView) findViewById(R.id.videoView);

        if(savedInstanceState!= null) {
            videoFile= savedInstanceState.getString(VIDEOURL);
            videoView.setVideoPath(videoFile);
            videoView.seekTo(savedInstanceState.getInt(VIDEOPOSITION));

        }else {

            Intent intent = getIntent();
            videoFile = intent.getStringExtra(CustomAdapter.VIDEO);


            try {
                videoView.setVideoPath(videoFile);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        videoView.start();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {


        outState.putString(VIDEOURL,videoFile);
        outState.putInt(VIDEOPOSITION,videoView.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }
}
