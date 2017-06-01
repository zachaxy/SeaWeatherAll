package com.zx.seaweatherall.bean;

import android.content.Context;
import android.database.Cursor;

import com.zx.seaweatherall.Param;
import com.zx.seaweatherall.utils.ACache;
import com.zx.seaweatherall.utils.DBUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.zx.seaweatherall.utils.DBUtils.getDB;

/**
 * Created by zhangxin on 2017/5/31 0031.
 * <p>
 * Description : 封装的是整个大区
 */

public class SeaBean {

    Context mContext;
    int id;  //
    int indexInVP; //

    int currentSeaNo = 0;

    //必须要保证的是下面三个集合的长度是相等，must；
    public Locater[] locaters; //各个点的位置
    public SeaArea[] seaAreas; //各个区域的顶点
    public String[] areaNames; //各个区域的名字


    //保存的是当前地图的当天的天气；
    public WeatherBean[][] weathers;


    //保存的是台风名字和台风轨迹点；
    public LinkedHashMap<String, ArrayList<Locater>> typhoonMap = new LinkedHashMap<>();
    public HashMap<String, ArrayList<Integer>> typhoonCircle = new HashMap<>();
    public HashMap<String, String> typhoonContent = new HashMap<>();


    // TODO: 2017/5/31 0031 构造方法需要实现，后面几个变量都还没有值呢；


    public SeaBean(Context context, int id, int indexInVP) {
        mContext = context;
        this.id = id;
        this.indexInVP = indexInVP;

        init(id);
    }

    private void init(int id) {
        switch (id) {
            case Param.SHANDONG_0:
                initSHANDONG0();
                break;
            case Param.MAOMING_0:
                initMAOMING0();
                break;
            case Param.MAOMING_1:
                initMAOMING1();
                break;
            case Param.ZHOUSHAN_0:
                break;
        }
    }


    void initSHANDONG0() {
        Locater[] locators0 = {new Locater(0, 0), new Locater(39, -365), new Locater(64, -338), new Locater(109, -356),
                new Locater(113, -303), new Locater(116, -254), new Locater(178, -203), new Locater(116, -119),
                new Locater(4, -43), new Locater(97, -37), new Locater(63, 33), new Locater(-244, 39),
                new Locater(-168, 27), new Locater(-113, 19), new Locater(-39, 18), new Locater(-147, 108),
                new Locater(-20, 104), new Locater(-138, 204), new Locater(-31, 204)};

        SeaArea[] seaAreas0 = {
                new SeaArea(),
                new SeaArea(new Locater(458, 2), new Locater(473, 14), new Locater(438, 82), new Locater(399, 39),
                        2710.0),
                new SeaArea(new Locater(459, 44), new Locater(447, 66), new Locater(490, 73), new Locater(474, 38),
                        825.5),
                new SeaArea(new Locater(474, 38), new Locater(490, 73), new Locater(558, 67), new Locater(533, 8),
                        3353.5),
                new SeaArea(new Locater(490, 73), new Locater(401, 122), new Locater(579, 122), new Locater(574, 66),
                        6730.5),
                new SeaArea(new Locater(418, 122), new Locater(478, 186), new Locater(563, 158), new Locater(579,
                        122), 6458.0),
                new SeaArea(new Locater(563, 158), new Locater(323, 240), new Locater(615, 240), new Locater(672,
                        153), 16571.0),
                new SeaArea(new Locater(478, 240), new Locater(411, 321), new Locater(550, 326), new Locater(615,
                        240), 11688.0),
                new SeaArea(new Locater(411, 321), new Locater(358, 369), new Locater(412, 407), new Locater(456,
                        328), 4234.5),
                new SeaArea(new Locater(456, 328), new Locater(451, 407), new Locater(518, 407), new Locater(550,
                        326), 6421.5),
                new SeaArea(new Locater(411, 407), new Locater(411, 462), new Locater(518, 462), new Locater(518,
                        407), 5885.0),
                new SeaArea(new Locater(139, 374), new Locater(141, 504), new Locater(203, 459), new Locater(225,
                        400), 6898.0),
                new SeaArea(new Locater(224, 412), new Locater(213, 434), new Locater(249, 430), new Locater(238, 406),
                        575.0),
                new SeaArea(new Locater(249, 430), new Locater(230, 462), new Locater(314, 462), new Locater(314, 381),
                        new Locater(238, 406), 5026.0),
                new SeaArea(new Locater(314, 336), new Locater(314, 462), new Locater(412, 462), new Locater(412, 407),
                        8869.0),
                new SeaArea(new Locater(203, 459), new Locater(78, 557), new Locater(314, 557), new Locater(314, 462),
                        16836.5),
                new SeaArea(new Locater(314, 462), new Locater(314, 557), new Locater(442, 557), new Locater(447, 462),
                        12397.5),
                new SeaArea(new Locater(213, 557), new Locater(73, 753), new Locater(314, 753), new Locater(314, 557),
                        33516.0),
                new SeaArea(new Locater(314, 557), new Locater(314, 753), new Locater(520, 753), new Locater(442,
                        557), 32732.0),

        };
        String[] areaNames0 = {"中国", "渤海", "渤海海峡", "黄海北部", "黄海中部", "黄海南部",
                "东海北部", "东海南部", "台湾海峡", "台湾省以东", "巴士海峡", "北部湾", "琼州海峡",
                "南海西北部", "南海东北部", "南海中西部", "南海中东部", "南海西南部", "南海东南部",};

        locaters = locators0;
        seaAreas = seaAreas0;
        areaNames = areaNames0;

        ACache aCache = ACache.get(mContext);
        weathers = (WeatherBean[][]) aCache.getAsObject("weather" + id);


    }


    void initMAOMING0() {
        Locater[] locators0 = {
                new Locater(0, 0),
                new Locater(120,-357),
                new Locater(53,-309),
                new Locater(-6,-298),
                new Locater(-71,-273),
                new Locater(-91,-334),
                new Locater(-118,-257),
                new Locater(-172,-248),
                new Locater(-208,-217),
                new Locater(-304,-220),
                new Locater(-240,-121),
                new Locater(-144,-157),
                new Locater(39,-218),
                new Locater(39,-99),
                new Locater(234,-230),
                new Locater(5,47),
                new Locater(-200,5),
                new Locater(-255,114),
                new Locater(-130,222),
        };

        SeaArea[] seaAreas0 = {
                new SeaArea(),
                new SeaArea(new Locater(442,56),new Locater(505,2),new Locater(588,2),new Locater(561,145),11951.0),
                new SeaArea(new Locater(407,84),new Locater(442,56),new Locater(520,145),new Locater(452,150),5006.0),
                new SeaArea(new Locater(349,92),new Locater(407,84),new Locater(452,150),new Locater(379,160),4726.0),
                new SeaArea(new Locater(293,123),new Locater(309,80),new Locater(349,92),new Locater(359,113),1699.0),
                new SeaArea(new Locater(293,123),new Locater(359,113),new Locater(379,160),new Locater(317,166),3056.0),
                new SeaArea(new Locater(245,128),new Locater(293,123),new Locater(317,166),new Locater(271,178),2398.0),
                new SeaArea(new Locater(204,174),new Locater(216,191),new Locater(271,178),new Locater(245,128),new Locater(184,148),3161.5),
                new SeaArea(new Locater(165,190),new Locater(178,180),new Locater(204,174),new Locater(216,191),518.5),
                new SeaArea(new Locater(74,293),new Locater(2,184),new Locater(174,88),new Locater(178,180),new Locater(165,190),new Locater(149,246),19778.5),
                new SeaArea(new Locater(74,293),new Locater(149,246),new Locater(189,238),new Locater(195,377),new Locater(148,377),10771.5),
                new SeaArea(new Locater(189,238),new Locater(216,191),new Locater(317,166),new Locater(321,402),new Locater(250,401),new Locater(249,377),new Locater(195,377),27443.5),
                new SeaArea(new Locater(317,166),new Locater(520,145),new Locater(520,263),new Locater(318,259),21368.0),
                new SeaArea(new Locater(318,259),new Locater(551,264),new Locater(538,405),new Locater(321,402),31970.0),
                new SeaArea(new Locater(520,145),new Locater(643,147),new Locater(673,263),new Locater(520,263),16131.0),
                new SeaArea(new Locater(250,401),new Locater(538,405),new Locater(545,484),new Locater(435,583),new Locater(211,582),new Locater(252,536),53634.5),
                new SeaArea(new Locater(148,377),new Locater(249,377),new Locater(252,536),new Locater(141,482),14046.0),
                new SeaArea(new Locater(141,482),new Locater(252,536),new Locater(211,582),new Locater(3,581),14025.0),
                new SeaArea(new Locater(149,584),new Locater(435,583),new Locater(435,650),new Locater(154,771),36157.0),
        };

        String[] areaNames0 = {
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

        locaters = locators0;
         seaAreas = seaAreas0;
        areaNames = areaNames0;

        ACache aCache = ACache.get(mContext);
        weathers = (WeatherBean[][]) aCache.getAsObject("weather" + id);
    }

    //这个区域比较特别，因为不需要区域显示，而是图标全显示，手指点击图标弹出具体信息；
    void initMAOMING1() {
        Locater[] locators0 = {
                new Locater(0, 0),
                new Locater(350,-278),
                new Locater(284,-194),
                new Locater(162,-130),
                new Locater(29,-148),
                new Locater(-4,-46),
                new Locater(-84,-122),
                new Locater(-111,-29),
                new Locater(-215,-93),
                new Locater(-286,-21),
                new Locater(-376,25),
        };

        String[] areaNames0 = {
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

        locaters = locators0;
//        seaAreas = seaAreas0;
        areaNames = areaNames0;

        ACache aCache = ACache.get(mContext);
        weathers = (WeatherBean[][]) aCache.getAsObject("weather" + id);
    }


}
