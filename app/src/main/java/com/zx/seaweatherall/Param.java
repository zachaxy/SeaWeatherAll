package com.zx.seaweatherall;

import com.felhr.usbserial.UsbSerialDevice;
import com.zx.seaweatherall.bean.Locater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by zhangxin on 2017/5/8 0008.
 * <p>
 * Description :
 */

public class Param {

    //得到一个具体的子类 串口设备子类这里是CP2102SerialDevice的对象.
    public static UsbSerialDevice serialPort;
    public static final int BAUD_RATE = 9600;

    public final static String PERF_PASSWORD = "fenghuoa";

    public static boolean T_SWiTCH;

    public static String CONFIGNAME = "fenghuo";
    // 有效期
    public static String mDate;

    public static String mChanel1;
    public static String mChanel2;
    public static String mChanel3;
    public static String mChanel4;
    public static String mChanel5;
    public static String mChanel6;
    public static String mChanel7;
    public static String mChanel8;
    public static String mChanel9;
    public static String mChanel10;

    // 10个信道
    public static ArrayList<String> mChannels;

    // 频偏
    public static String mOffSet;

    // 音量大小
    public static int mSounds;

    // 静噪
    public static boolean mSound;
    // 音频带宽
    public static int mSoundExtent;

    // 信噪比
    public static String mSNR;

    // 信道号
    public static String mSNN;

    // 数据传输速率
    public static String mDTR;

    public static boolean IsTyphonClear = false;

    // 用数组来存储天气类型,第一个索引手动赋值为0,类型从1开始的,我也是醉了...
    public static final int[] weatherIcon = {R.drawable.w0, R.drawable.w1,
            R.drawable.w2, R.drawable.w3, R.drawable.w4, R.drawable.w5,
            R.drawable.w6, R.drawable.w7, R.drawable.w8, R.drawable.w9,
            R.drawable.w10, R.drawable.w11, R.drawable.w12, R.drawable.w13,
            R.drawable.w14, R.drawable.w15, R.drawable.w16, R.drawable.w17,
            R.drawable.w18, R.drawable.w19, R.drawable.w20, R.drawable.w21,
            R.drawable.w22, R.drawable.w23, R.drawable.w24, R.drawable.w25,
            R.drawable.w26, R.drawable.w27, R.drawable.w28, R.drawable.w29,
            R.drawable.w30, R.drawable.w31, R.drawable.w32, R.drawable.w33,
            R.drawable.w34, R.drawable.w35, R.drawable.w36, R.drawable.w37,
            R.drawable.w38, R.drawable.w6, R.drawable.w7, R.drawable.w8,
            R.drawable.w9, R.drawable.w7, R.drawable.w8, R.drawable.w9,
            R.drawable.w8, R.drawable.w9, R.drawable.w9, R.drawable.w20,
            R.drawable.w21, R.drawable.w21,};


    public static final String[] weatherName = {"",
            "晴", "多云", "阴天", "小雨", "中雨", "大雨", "暴雨", "大暴雨",
            "特大暴雨", "阵雨", "雷阵雨", "雷阵雨伴有冰雹", "雷电", "冰雹", "冻雨", "霜冻",
            "雨夹雪", "小雪", "中雪", "大雪", "暴雪", "轻雾", "雾", "浓雾",
            "霾", "小雨-中雨", "中雨-大雨", "大雨-暴雨", "暴雨-大暴雨", "大暴雨-特大暴雨", "小雪-中雪", "中雪-大雪",
            "大雪-暴雪", "浮尘", "扬沙", "沙尘暴", "强沙尘暴", "台风",
            "小到大雨", "小到暴雨", "小到大暴雨",
            "小到特大暴雨", "中到暴雨", "中到大暴雨", "中到特大暴雨", "大到大暴雨",
            "大到特大暴雨", "暴雨到特大暴雨", "小到大雪", "小到暴雪", "中到暴雪"
    };

    public static final String[] windDirection = {"", "北风", "北东北风", "东北风", "东东北风", "东风", "东东南风", "东南风", "南东南风",
            "南风", "南西南风", "西南风", "西西南风", "西风", "西西北风", "西北风", "北西北风", "旋转风", "静风"};


    public static String param = "SDR状态回复错误";

    // 定义ack的初始状态为-2,若为-1:无响应; 若为0:nack, 若为1:ack,每次用完后都应该置为-2值.
    public static int ack = -1;
    public static volatile int SDRAck = -1;
    public static volatile int SoundAck = -1;
    public static volatile int SoundsAck = -1;
    public static volatile int ChannelAck = -1;
    public static volatile int unLinkAck = -1;

    public static volatile int unlinkCount = 0;

    //默认自动拆链时间是60分钟
    public static volatile int unlinkTime = 60;

    public static boolean totalFlag = true;

    //PC端图片的大小;
    public final static double ORIGINAL_IMAGE_SIZE = 4677.0;

    //移动端图片的大小;
    public final static double ACTUAL_IMAGE_SIZE = 800.0;


    public static int my_area = 0;
    public static int my_group = 0;
    public static int my_id = 0;
    public static int my_authority = 0;

    //由于天气和台风合并在一起发， 这里为了区分是天气还台风，做了一个标示；
    public static final int type_weather = 1001;
    public static final int type_typhooon = 1002;


    //山东远海区
    public static final int SHANDONG_FAR_SEA_AREA_COUNT = 18;
    public static final String[] SHANDONG_AREA_NAME = {
            "中国",
            "渤海",
            "渤海海峡",
            "黄海北部",
            "黄海中部",
            "黄海南部",
            "东海北部",
            "东海南部",
            "台湾海峡",
            "台湾省以东",
            "巴士海峡",
            "北部湾",
            "琼州海峡",
            "南海西北部",
            "南海东北部",
            "南海中西部",
            "南海中东部",
            "南海西南部",
            "南海东南部",
    };


    //茂名
    public static final int MAOMING_FAR_AREA_COUNT = 18;
    public static final int MAOMING_NEAR_AREA_COUNT = 10;
    public static final int MAOMING_FISH_AREA_COUNT = 23;

    public static final String[] MAOMING_FAR_AREA_NAME = {
            "中国",
            "台湾海峡",
            "汕头附近海面",
            "汕尾附近海面",
            "珠江口外海面",
            "珠江口内海面",
            "川山群岛附近海面",
            "湛江附近海面",
            "琼州海峡",
            "北部湾",
            "海南岛西南部",
            "西沙",
            "东沙",
            "中沙",
            "巴士海峡",
            "南沙",
            "华列拉",
            "头顿",
            "曾母暗沙",
    };

    public static final String[] MAOMING_NEAR_AREA_NAME = {
            "中国",
            "南澳",
            "草屿",
            "托泞列岛",
            "万山",
            "上川",
            "下川",
            "海陵",
            "放鸡岛",
            "东海",
            "涠洲",
    };


    //舟山大渔区，小渔区
    public static final int ZHOUSHAN_BIG_FISH_AREA_COUNT = 13;
    public static final int ZHOUSHAN_SMALL_FISH_AREA_COUNT = 203;

    //哪个单位用,1为山东、2为茂名、3为舟山。
    public static final int SHANDONG = 1;
    public static final int MAOMING = 2;
    public static final int ZHOUSHAN = 3;


    //哪个单位用,1为山东、2为茂名、3为舟山。
    public static final int SHANDONG_0 = 0;
    public static final int MAOMING_0 = 1;
    public static final int MAOMING_1 = 2;
    public static final int ZHOUSHAN_0 = 3;

    //通过蓝牙模块获取固定气象局的全限，从1开始；
    //作用是在接受消息时，先检查权限，不是的话，直接不解析，返回null；索引值下标为：1,2,3；分别对应三个地区（SHANDONG。。）
    public static boolean[] AUTHORITY = new boolean[4];
    public static ArrayList<Integer> MAP_PIC = new ArrayList<>();
    //使用场景，如果接受到某一地点的气象，需要切换到对应地图，索引为地区（SHANDONG_0。。。），值为ViewPager中的position；
    public static HashMap<Integer, Integer> map2position = new HashMap<>();


    //ViewPager中展示的是哪个地图,注意这里显示的不是VP的index，而是SHANDONG_0，表示位置的，值的复制是在解析消息的时候赋值的；
    public static int CURRENT_POSITION = -1;

    //当前区域的海区号码；
    public static int AREA_NO;


    public static LinkedHashMap<Integer, ArrayList<Locater>> typhoonMap = new LinkedHashMap<Integer, ArrayList<Locater>>();

}
