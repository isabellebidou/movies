package com.demo.isabellebidou.movies;

/**
 * Created by isabelle on 17/09/16.
 * inspired by: https://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
 */

import android.graphics.Bitmap;
import android.util.LruCache;

public class CacheHandler {


    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 8;
    private LruCache<String, Bitmap> mMemoryCache;


    public LruCache<String, Bitmap> setUp() {
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        return mMemoryCache;

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


}
