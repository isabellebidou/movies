package com.demo.isabellebidou.movies;

import android.os.AsyncTask;

import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by isabelle on 19/09/16.
 */
public class WriteToStorage extends AsyncTask<Void, Void, Void> {

    public WriteToStorage(FileOutputStream storageFile,String str) {

        internalStorageFile = storageFile;
        string = str;

    }

    private static FileOutputStream internalStorageFile;
    private static String string;
    @Override
    protected Void doInBackground(Void... arg0) {


        try {

            internalStorageFile.write(string.getBytes());
            internalStorageFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
