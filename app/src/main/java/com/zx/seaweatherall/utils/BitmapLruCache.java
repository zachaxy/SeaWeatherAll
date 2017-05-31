package com.zx.seaweatherall.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by zhangxin on 2017/5/29 0029.
 * <p>
 * Description :
 */

public class BitmapLruCache extends LruCache<String, Bitmap> {
    static String TAG = "aaa";
    static Context mContext;

    private volatile static BitmapLruCache cache;

    public static BitmapLruCache getCache() {
        Log.e(TAG, "getCache: ");
        if (cache == null) {
            synchronized (BitmapLruCache.class) {
                if (cache == null) {
                    cache = new BitmapLruCache(mContext);
                }
            }
        }
        return cache;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    private BitmapLruCache(Context context) {
        super((int) (Runtime.getRuntime().maxMemory() / 1024 / 10));
        Log.e(TAG, "BitmapLruCache: ");
    }

    @Override
    protected Bitmap create(String key) {
        Log.e(TAG, "create: ");
//        return BitmapFactory.decodeResource(mContext.getResources(), key);
        return super.create(key);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        Log.e(TAG, "sizeOf: " + key);
        return value.getByteCount() / 1024;
    }


    public Bitmap get(int id1, int id2, int id3, int id4) {
        StringBuilder sb = new StringBuilder();
        sb.append(id1).append("#").append(id2).append("#").append(id3).append("#").append(id4);
        String key = sb.toString();
        Bitmap res = get(key);
        if (res != null) {
            return res;
        }

        Bitmap b1 = SingleBitmapLruCache.getCache().get(id1);
        Bitmap b2 = SingleBitmapLruCache.getCache().get(id2);
        Bitmap b3 = SingleBitmapLruCache.getCache().get(id3);
        Bitmap b4 = SingleBitmapLruCache.getCache().get(id4);
        Log.e(TAG, "get: b1的原始地址" + b1);
        Bitmap[] bitmaps = {b1, b2, b3, b4};

        res = mergeBitmap(bitmaps);

        put(sb.toString(), res);
        return res;
    }


    public Bitmap mergeBitmap(Bitmap[] bitmaps) {
        int height = 0;
        for (int i = 0; i < bitmaps.length; i++) {
            height = Math.max(height, bitmaps[i].getHeight());
        }

//        height = height / 2;

        Log.d("###1", "mergeBitmap: " + height);
        int width = 0;

        for (int i = 0; i < bitmaps.length; i++) {
            /*if (bitmap.getHeight() != height) {
                bitmap = Bitmap.createScaledBitmap(bitmap,
                        (int) (bitmap.getWidth() * 1f / bitmap.getHeight() * height), height, false);
            }*/
            bitmaps[i] = scaleBitmap(bitmaps[i], 0.5f);
            Log.d("aaa", "" + bitmaps[i].getWidth());
            Log.d("aaa", "" + bitmaps[i].getHeight());
            width += bitmaps[i].getWidth();
        }
        height /= 2;

        Log.d(TAG, "mergeBitmap width: " + width);
        Log.d(TAG, "mergeBitmap height: " + height);
        // 定义输出的bitmap
        Bitmap res = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        //要获取到一个bitmap，那么就需要一个绑定的canvas，用canvas画的内容全在bitmap上，画好之后在返回该bitmap；
        Canvas canvas = new Canvas(res);

        int left = 0;
        for (Bitmap bitmap : bitmaps) {
            canvas.drawBitmap(bitmap, left, 0, null);
            Log.d(TAG, "mergeBitmap left: " + left);
            left += bitmap.getWidth();
        }
        return res;
    }

    private Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        Log.e(TAG, "scaleBitmap: 新bitmap的地址：" + newBM);
//        origin.recycle();
        return newBM;
    }

    /***
     * @param leftBitmap
     * @param rightBitmap
     * @param isBaseMax   是否取二者的最大高度最为拼接后的高度
     * @return
     */
    public Bitmap mergeBitmap_LR(Bitmap leftBitmap, Bitmap rightBitmap, boolean isBaseMax) {

        if (leftBitmap == null || leftBitmap.isRecycled()
                || rightBitmap == null || rightBitmap.isRecycled()) {
            return null;
        }
        int height = 0; // 拼接后的高度，按照参数取大或取小
        if (isBaseMax) {
            height = leftBitmap.getHeight() > rightBitmap.getHeight() ? leftBitmap.getHeight() : rightBitmap
                    .getHeight();
        } else {
            height = leftBitmap.getHeight() < rightBitmap.getHeight() ? leftBitmap.getHeight() : rightBitmap
                    .getHeight();
        }

        // 缩放之后的bitmap
        Bitmap tempBitmapL = leftBitmap;
        Bitmap tempBitmapR = rightBitmap;


        //进行缩放，将两个bitmap的高设置为相同的；
        if (leftBitmap.getHeight() != height) {
            tempBitmapL = Bitmap.createScaledBitmap(leftBitmap, (int) (leftBitmap.getWidth() * 1f / leftBitmap
                    .getHeight() * height), height, false);
        } else if (rightBitmap.getHeight() != height) {
            tempBitmapR = Bitmap.createScaledBitmap(rightBitmap, (int) (rightBitmap.getWidth() * 1f / rightBitmap
                    .getHeight() * height), height, false);
        }

        // 拼接后的宽度
        int width = tempBitmapL.getWidth() + tempBitmapR.getWidth();

        // 定义输出的bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        //要获取到一个bitmap，那么就需要一个绑定的canvas，用canvas画的内容全在bitmap上，画好之后在返回该bitmap；
        Canvas canvas = new Canvas(bitmap);

        // 缩放后两个bitmap需要绘制的参数
        Rect leftRect = new Rect(0, 0, tempBitmapL.getWidth(), tempBitmapL.getHeight());
        Rect rightRect = new Rect(0, 0, tempBitmapR.getWidth(), tempBitmapR.getHeight());

        // 右边图需要绘制的位置，往右边偏移左边图的宽度，高度是相同的
        Rect rightRectT = new Rect(tempBitmapL.getWidth(), 0, width, height);

        canvas.drawBitmap(tempBitmapL, leftRect, leftRect, null);
        canvas.drawBitmap(tempBitmapR, rightRect, rightRectT, null);
        return bitmap;
    }
}
