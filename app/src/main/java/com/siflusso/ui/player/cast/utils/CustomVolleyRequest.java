package com.siflusso.ui.player.cast.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;


public class CustomVolleyRequest {

    @SuppressLint("StaticFieldLeak")
    private static CustomVolleyRequest customVolleyRequest;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private RequestQueue requestQueue;
    private final ImageLoader imageLoader;


    private CustomVolleyRequest(Context context) {
        CustomVolleyRequest.context = context;
        this.requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized CustomVolleyRequest getInstance(Context context) {
        if (customVolleyRequest == null) {
            customVolleyRequest = new CustomVolleyRequest(context);
        }
        return customVolleyRequest;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}
