package com.zx.seaweatherall.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by zhangxin on 2016/3/22.
 * 跑马灯
 */
public class MarqueenTextView extends TextView {

    public MarqueenTextView(Context context) {
        super(context);
    }

    public MarqueenTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueenTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused(){
        return true;
    }
}
