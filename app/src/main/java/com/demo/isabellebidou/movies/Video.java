package com.demo.isabellebidou.movies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.Date;

/**
 * Created by isabelle on 14/09/16.
 */
public class Video  implements Parcelable {


    private Integer movieId;
    private String title;
    private String duration;
    private String date;
    private String thumb;
    private String file;
    private String views;
    private String uploaderId;

    public Video() {

        super();
    }


    public Video(Integer movieId,String views, String title, String thumb, String file, String duration, String date, Uploader uploader) {
        this.movieId = movieId;
        this.views = views;
        this.title = title;
        this.thumb = thumb;
        this.file = file;
        this.duration = duration;
        this.date = date;
        this.uploaderId = uploader.getUserid().toString();
    }
    public Video(Parcel in){
        String[] data = new String[6];

        in.readStringArray(data);
        this.movieId = Integer.valueOf(data[0]);
        this.views = data[1];
        this.title = data[2];
        this.thumb = data[3];
        this.file = data[4];
        this.duration=data[5];
        this.date= data[6];
        this.uploaderId=data[7];
    }


    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public String getFile() {
        return file;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public String getThumb() {
        return thumb;
    }

    public String getTitle() {
        return title;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public String getViews() {
        return views;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.movieId.toString() ,
        this.views ,
        this.title ,
        this.thumb ,
        this.file ,
        this.duration ,
        this.date ,
        this.uploaderId.toString() ,
        });

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
