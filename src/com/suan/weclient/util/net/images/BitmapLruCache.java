package com.suan.weclient.util.net.images;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCache<String, Bitmap>  {
    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }


    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }


    public Bitmap getBitmap(String url) {
        return get(url);
    }


    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}