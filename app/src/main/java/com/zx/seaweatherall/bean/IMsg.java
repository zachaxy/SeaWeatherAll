package com.zx.seaweatherall.bean;


import com.zx.seaweatherall.Param;

/**
 * Created by zhangxin on 2017/5/2 0002.
 * <p>
 * Description : 由于台风和天气不能很好的区分开，这里定义了IMsg的父类，类型默认返回的是天气，让台风去继承这个类，然后修改类型；
 */

public class IMsg {

    public String content;

    public void setContent(String content) {
        this.content = content;
    }

    //获取天气的文字描述,在右侧显示;
    public String getMsgContent() {
        return content;
    }

    //获取天气的类型是台风还是天气;
    public int getMsgType() {
        return Param.type_weather;
    }
}
