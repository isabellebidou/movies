package com.demo.isabellebidou.movies;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

/**
 * Created by isabelle on 14/09/16.
 */
public class Uploader implements Parcelable{
    private Integer userid;
    private String name;
    private String avatar;

    public Uploader(Integer userid,String name, String avatar ) {
        this.name = name;
        this.avatar = avatar;
        this.userid = userid;
    }

    public Uploader(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        this.userid= Integer.valueOf(data[0]);
        this.name = data[1];
        this.avatar = data[2];

    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public Integer getUserid() {
        return userid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.userid.toString() ,
                this.name ,
                this.avatar

        });

    }

    public static Uploader findVideoUploaderByStringId(String uploaderId){

        Uploader result= null;
        for (int i = 0; i<MainListActivity.getUploaders().size();i++){

            if (MainListActivity.getUploaders().get(i).getUserid().toString().equalsIgnoreCase(uploaderId)){

                result=MainListActivity.getUploaders().get(i);
            }
        }
        return result;
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Uploader createFromParcel(Parcel in) {
            return new Uploader(in);
        }

        public Uploader[] newArray(int size) {
            return new Uploader[size];
        }
    };
}
