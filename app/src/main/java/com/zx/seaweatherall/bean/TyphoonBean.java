package com.zx.seaweatherall.bean;

import com.zx.seaweatherall.Param;

import java.util.ArrayList;

/**
 * Created by zhangxin on 2017/4/19 0019.
 * <p>
 * Description :台风的bean对象;
 */

public class TyphoonBean extends IMsg {
    public String timeStamp; //发送时间；
    int typhoonNo;  //台风代号
    String typhoonName;//台风名称;
    double typhoonX;//当前台风坐标x;
    double typhoonY;//当前台风坐标y;
    String typhoonTime;//台风登陆时间;
    String windPower;
    String windDirect;
    public ArrayList<Integer> typhoonCircleList; //台风风圈,用来在地图上显示

    public TyphoonBean(String timeStamp,int typhoonNo, String typhoonName, double typhoonX, double typhoonY,
                       String typhoonTime,
                       ArrayList<Integer> typhoonCircleList, String typhoonContent) {
        this.timeStamp = timeStamp;
        this.typhoonNo = typhoonNo;
        this.typhoonName = typhoonName;
        this.typhoonX = typhoonX;
        this.typhoonY = typhoonY;
        this.typhoonTime = typhoonTime;
        this.typhoonCircleList = typhoonCircleList;
        this.content = typhoonContent;
    }

    //NOTE：临时覆盖的这个方法，正式版本去除掉，只是为了格式化显示消息信息；
    /*@Override
    public String getMsgContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("台风编号:").append(typhoonNo).append(",台风名称:").append(typhoonName)
                .append(",台风坐标:(").append(typhoonX).append(",").append(typhoonY).append(")")
        .append("登陆时间:").append(typhoonTime)
        .append("");
        return sb.toString();
    }*/

    @Override
    public int getMsgType() {
        return Param.type_typhooon;
    }
}
