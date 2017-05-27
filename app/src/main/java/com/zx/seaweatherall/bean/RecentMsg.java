package com.zx.seaweatherall.bean;


import com.zx.seaweatherall.utils.BytesUtil;

import java.io.Serializable;

//定义信息的属性:图像,内容,时间
//用来存储侧边连列表；
public class RecentMsg implements Serializable {
    public boolean isRead;
    public String mMsgTime; //时间戳
    public int mMsgImg;     //图标代表的id
    public String mMsgContent;  //展示的消息内容；

    public RecentMsg(int mMsgImg, String mMsgContent, String mMsgTime) {
        this.mMsgImg = mMsgImg;
        this.mMsgContent = mMsgContent;
        this.mMsgTime = mMsgTime;
    }

    public String showMsg() {
        return "接收时间: " + BytesUtil.formatTime(mMsgTime.toCharArray()) + mMsgContent + "\n";
    }
}
