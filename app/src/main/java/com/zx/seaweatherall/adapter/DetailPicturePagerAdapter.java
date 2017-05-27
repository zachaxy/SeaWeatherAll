package com.zx.seaweatherall.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.zx.seaweatherall.widget.ZoomImageView;

import java.util.ArrayList;

/**
 * Created by zhangxin on 2017/5/26 0026.
 * <p>
 * Description : MapFragment中使用到的展示图片的ViewPager
 */

public class DetailPicturePagerAdapter extends PagerAdapter {

    private static final String TAG = "###";
    ArrayList<Integer> imageuri;
    Context mContext;
    private int currentPos = -1;
    private ZoomImageView mCurrentZoomImageView;

    public DetailPicturePagerAdapter(Context context, ArrayList<Integer> imageuri) {
        this.mContext = context;
        this.imageuri = imageuri;
    }

    @Override
    public int getCount() {
        return imageuri.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "setPrimaryItem: " + position);

        if (position != currentPos) {
            currentPos = position;
            if (mCurrentZoomImageView != null) {
                mCurrentZoomImageView.resetSize();
            }
            mCurrentZoomImageView = (ZoomImageView) object;
        }
    }

    public ZoomImageView getCurrentItem() {
        return mCurrentZoomImageView;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem: " + position);


        View view = new ZoomImageView(container.getContext());
        ViewPager.LayoutParams params = new ViewPager.LayoutParams();

        view.setLayoutParams(params);
        container.addView(view);

        setupPage(view, position);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "destroyItem: " + position);
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private void setupPage(View view, int index) {
        Glide.with(mContext).load(imageuri.get(index)).crossFade(700).into((ZoomImageView) view);
    }
}