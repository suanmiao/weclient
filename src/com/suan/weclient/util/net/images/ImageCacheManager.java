package com.suan.weclient.util.net.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

/**
 * Implementation of volley's ImageCache interface. This manager tracks the
 * application image loader and cache.
 *
 * @author Trey Robinson
 */
public class ImageCacheManager {

    private static ImageCacheManager mInstance;

    public static final String CACHE_USER_PROFILE = "userProfile";

    public static final String CACHE_MESSAGE_LIST_PROFILE = "messageListProfile";
    public static final String CACHE_CHAT_LIST_PROFILE = "chatListProfile";
    public static final String CACHE_MESSAGE_CONTENT = "messageContent";

    /**
     * Image cache used for local image storage
     */
    private DiskLruImageCache mDiskCache;

    private BitmapLruCache mBitmapLruCache;

    /**
     * @return instance of the cache manager
     */
    public static ImageCacheManager getInstance() {
        if (mInstance == null)
            mInstance = new ImageCacheManager();

        return mInstance;
    }

    /**
     * Initializer for the manager. Must be called prior to use.
     *
     * @param context        application context
     * @param uniqueName     name for the cache location
     * @param cacheSize      max size for the cache
     * @param compressFormat file type compression format.
     * @param quality
     */
    public void init(Context context, String uniqueName, int cacheSize,
                     CompressFormat compressFormat, int quality) {
        mDiskCache = new DiskLruImageCache(context, uniqueName, cacheSize,
                compressFormat, quality);
        mBitmapLruCache = new BitmapLruCache(cacheSize);
    }

    public Bitmap getBitmap(String key) {
        Bitmap result = null;
        try {
            Bitmap getBitmap = mBitmapLruCache.getBitmap(createKey(key));
            if (getBitmap != null) {
                return getBitmap;
            }
        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }
        try {

            Bitmap getBitmap = mDiskCache.getBitmap(createKey(key));
            if (getBitmap != null) {

                try {
                    mBitmapLruCache.put(createKey(key), getBitmap);
                } catch (NullPointerException e) {
                    throw new IllegalStateException("Disk Cache Not initialized");
                }

                return getBitmap;
            }
        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }

        return result;
    }

    public void putBitmap(String key, Bitmap bitmap, boolean storeToDisk) {

        try {
            mBitmapLruCache.put(createKey(key), bitmap);
        } catch (NullPointerException e) {
            Log.e("fuck disk",""+mBitmapLruCache+"|"+bitmap);

            throw new IllegalStateException("Disk Cache Not initialized");
        }
        if (storeToDisk) {
            try {
                mDiskCache.put(createKey(key), bitmap);
            } catch (NullPointerException e) {
                throw new IllegalStateException("Disk Cache Not initialized");
            }
        }

    }


    /**
     * Creates a unique cache key based on a url value
     *
     * @param url url to be used in key creation
     * @return cache key value
     */
    private String createKey(String url) {
        return String.valueOf(url.hashCode());
    }

}
