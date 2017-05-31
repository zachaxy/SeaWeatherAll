package com.zx.seaweatherall.bean;

import com.zx.seaweatherall.Param;

import java.util.ArrayList;

/**
 * Created by zhangxin on 2017/5/31 0031.
 * <p>
 * Description : 封装的是整个大区
 */

public class SeaBean {
    int id;  //
    int indexInVP; //

    int currentSeaNo = 0;

    //必须要保证的是下面三个集合的长度是相等，must；
    Locater[] locaters; //各个点的位置
    SeaArea[] seaAreas; //各个区域的顶点
    String[] areaNames; //各个区域的名字

    ArrayList<Locater> tyPhoonList;
    ArrayList<Locater> typhoonCircle;
    String typhoonContent;

    // TODO: 2017/5/31 0031 构造方法需要实现，后面几个变量都还没有值呢；


    public SeaBean(int id, int indexInVP) {
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
                break;
            case Param.MAOMING_1:
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
    }
}
