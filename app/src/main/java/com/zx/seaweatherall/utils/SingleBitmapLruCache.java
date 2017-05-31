package com.zx.seaweatherall.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by zhangxin on 2017/5/29 0029.
 * <p>
 * Description : 保存的是单个的icon
 */

public class SingleBitmapLruCache extends LruCache<Integer, Bitmap> {
    static String TAG = "aaa";
    static Context mContext;

    private volatile static SingleBitmapLruCache cache;

    public static SingleBitmapLruCache getCache() {
        Log.e(TAG, "getCache: ");
        if (cache == null) {
            synchronized (SingleBitmapLruCache.class) {
                if (cache == null) {
                    cache = new SingleBitmapLruCache(mContext);
                }
            }
        }
        return cache;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    private SingleBitmapLruCache(Context context) {
        super((int) (Runtime.getRuntime().maxMemory() / 1024 / 10));
    }

    @Override
    protected Bitmap create(Integer key) {
        return BitmapFactory.decodeResource(mContext.getResources(), key);
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        return value.getByteCount() / 1024;
    }


}
