package com.demo.isabellebidou.movies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;


import android.view.View.OnClickListener;
import android.widget.Toast;


import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by isabelle on 15/09/16.
 * inspired by https://www.caveofprogramming.com/guest-posts/custom-listview-with-imageview-and-textview-in-android.html
 * http://stackoverflow.com/questions/13672700/how-to-refresh-custom-listview-using-baseadapter-in-android
 * http://stackoverflow.com/questions/14087495/android-implementing-viewholder
 */

public class CustomAdapter extends BaseAdapter {


    public final static String VIDEO = "video";


    private ArrayList<Video> videos = new ArrayList<Video>();

    Context context;

    private static LayoutInflater inflater = null;


    public CustomAdapter(MainListActivity mainActivity, ArrayList<Video> initialmovieslist) {
        // TODO Auto-generated constructor stub

        context = mainActivity;
        videos = initialmovieslist;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (videos == null)
            return 10;
        else
            return videos.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView titleTextView;
        ImageView thumbsImageView;
        ImageView avatarImageView;
        TextView timeTextView;
        TextView viewsTextView;
        TextView uploaderNameTextView;
        TextView durationTextView;


    }

    @Override
    public View getView(final int position, View rView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder;


        View rowView = inflater.inflate(R.layout.customlistitemlayout, parent, false);
        holder = new Holder();
        rowView.setTag(holder);
        final  Video video  = videos.get(position);

// uncomment to recycle views
       /* if (rowView == null) {
            rowView = inflater.inflate(R.layout.customlistitemlayout, parent, false);
            holder = new Holder();
            rowView.setTag(holder);

        } else {
            holder = (Holder) rowView.getTag();
        }*/





        holder.titleTextView = (TextView) rowView.findViewById(R.id.videotitle);
        holder.timeTextView = (TextView) rowView.findViewById(R.id.time);
        holder.viewsTextView = (TextView) rowView.findViewById(R.id.views);
        holder.uploaderNameTextView = (TextView) rowView.findViewById(R.id.uploadername);
        holder.thumbsImageView = (ImageView) rowView.findViewById(R.id.imageView);
        holder.avatarImageView = (ImageView) rowView.findViewById(R.id.avatar);
        holder.durationTextView = (TextView) rowView.findViewById(R.id.duration);

        if (videos == null) {
            holder.titleTextView.setText(R.string.loading);

            holder.thumbsImageView.setImageResource(R.drawable.loading);


        } else {


           // final  Video video  = videos.get(position);
            holder.titleTextView.setText(video.getTitle());

            Date date;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String newDateString = "";
            try {

                date = df.parse(video.getDate());
                long now = System.currentTimeMillis();
                newDateString = DateUtils.getRelativeTimeSpanString(date.getTime(), now, DateUtils.DAY_IN_MILLIS).toString();


            } catch (java.text.ParseException ex) {
                ex.printStackTrace();
            }


            holder.timeTextView.setText(newDateString);
            holder.viewsTextView.setText(context.getResources().getString(R.string.views) + video.getViews());

            Uploader videoUploader = Uploader.findVideoUploaderByStringId(video.getUploaderId());
            //holder.durationTextView.setText(context.getResources().getString(R.string.duration)+context.getResources().getString(R.string.space)+durationMinutes+":"+durationSeconds);
            holder.durationTextView.setText(video.getDuration());
            holder.uploaderNameTextView.setText(videoUploader.getName());
            String[] thumbsIdanduUrl = {video.getMovieId().toString(), video.getThumb()};
            String[] avatarIdandUrl = {videoUploader.getUserid().toString(), videoUploader.getAvatar()};
            Bitmap thumbsBitmap = MainListActivity.getCacheHandler().getBitmapFromMemCache(video.getMovieId().toString());
            if (thumbsBitmap != null) {
                holder.thumbsImageView.setImageBitmap(thumbsBitmap);
            } else {
                new DownloadImageTask(holder.thumbsImageView).execute(thumbsIdanduUrl);
            }

            Bitmap avatarBitmap = MainListActivity.getCacheHandler().getBitmapFromMemCache(videoUploader.getUserid().toString());
            if (avatarBitmap != null) {
                holder.avatarImageView.setImageBitmap(avatarBitmap);
            } else {
                new DownloadImageTask(holder.avatarImageView).execute(avatarIdandUrl);
            }

        }


        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Toast.makeText(context, "You selected "+video.getTitle(), Toast.LENGTH_SHORT).show();

                final ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = conMan.getActiveNetworkInfo();

                boolean isConnected = networkInfo != null &&
                        networkInfo.isConnectedOrConnecting();

                if(isConnected){
                    Intent intent = new Intent(context, VideoActivity.class);

                    try {
                        intent.putExtra(VIDEO, video.getFile());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    context.startActivity(intent);

                }else{

                    Toast.makeText(context,
                            "Internet connection required",
                            LENGTH_LONG).show();
                }



            }
        });

        return rowView;
    }


    public void refreshList(ArrayList<Video> newVideos) {


        videos = newVideos;
        notifyDataSetChanged();
        Log.d("CUSTOMADAPTOR", "refreshList size:" + videos.size());
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... bitmapIdandUrl) {
            String urldisplay = bitmapIdandUrl[1];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            MainListActivity.getCacheHandler().addBitmapToMemoryCache(bitmapIdandUrl[0], mIcon11);
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
