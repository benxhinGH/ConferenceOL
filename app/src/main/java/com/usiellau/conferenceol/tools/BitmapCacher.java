package com.usiellau.conferenceol.tools;


import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by UsielLau on 2018/4/22 0022 17:40.
 */
public class BitmapCacher {
    private LruCache<String,Bitmap> lruCache;
    private BitmapCacher(){
        int maxMemory=(int)(Runtime.getRuntime().maxMemory()/1024);
        int cacheSize=maxMemory/8;
        lruCache=new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight()/1024;
            }
        };
    }
    public static BitmapCacher getInstance(){
        return InstanceHolder.instance;
    }

    private static class InstanceHolder{
        private static final BitmapCacher instance=new BitmapCacher();
    }
    public void put(String key,Bitmap value){
        lruCache.put(key, value);
    }

    public Bitmap get(String key){
        return lruCache.get(key);
    }
}
