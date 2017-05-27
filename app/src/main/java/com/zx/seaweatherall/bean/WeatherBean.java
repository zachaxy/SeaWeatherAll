package com.zx.seaweatherall.bean;

import java.io.Serializable;

/**
 * Created by zhangxin on 2017/4/19 0019.
 * <p>
 * Description :
 */

public class WeatherBean extends IMsg implements Serializable {
    public int weatherType1;
    public int weatherType2;
    /*public String windDirct;
    public String windPower;
    public String gustWind;*/

    public String desc;  //只包含 风向 风力 阵风 的转向; 现在加上了后面的能见度和浪高；
//    public String visibility;
    //    public String waveHeight;
    public int earlyWarning;    //预警信息；

    @Override
    public String getMsgContent() {
        return desc;
    }
}
