package com.zx.seaweatherall.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.zx.seaweatherall.Param;
import com.zx.seaweatherall.R;
import com.zx.seaweatherall.bean.Locater;
import com.zx.seaweatherall.bean.Locator2;
import com.zx.seaweatherall.bean.SeaArea;
import com.zx.seaweatherall.bean.SeaBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/***
 * 辅助工具类
 *
 * @author zhangxin
 */

public class Tools {

    /***
     * 坐标点转换函数,将像素位置转为中心点相对位置
     * 这个4677.0是zhangxiaoxi电脑上图片的坐标,发来的坐标也是相对这个的大小,所以将这个弄到Params中
     *
     * @param l 收到的像素位置
     * @return 中心点相对位置  y/2725.0*444.0-222.0-->x*727/4677-363
     */
 /*   public static Locater transferLocate(Locater l) {
        Locater l2 = new Locater();
        *//*l2.x = (int) (l.x * 727 / 4677.0 - 363.0);
        l2.y = (int) (l.y * 727 / 4677.0 - 363.0);*//*
        l2.x = (int) (l.x * Param.ACTUAL_IMAGE_SIZE / Param.ORIGINAL_IMAGE_SIZE - Param.ACTUAL_IMAGE_SIZE / 2);
        l2.y = (int) (l.y * Param.ACTUAL_IMAGE_SIZE / Param.ORIGINAL_IMAGE_SIZE - Param.ACTUAL_IMAGE_SIZE / 2);
        return l2;
    }



    */
    //针对gps和点击选区域;
    public static final double SHANDONG0_ORIGINAL_X = 98.7379;
    public static final double SHANDONG0_ORIGINAL_Y = 41.23107;
    public static final double SHANDONG0_ORIGINAL_INTERVAL = 38.9768;

    public static final double MAOMING0_ORIGINAL_X = 98.7379;
    public static final double MAOMING0_ORIGINAL_Y = 41.23107;
    public static final double MAOMING0_ORIGINAL_INTERVAL = 38.9768;

    //茂名第二个图好像不用那啥吧~
    public static final double MAOMING1_ORIGINAL_X = 98.7379;
    public static final double MAOMING1_ORIGINAL_Y = 41.23107;
    public static final double MAOMING1_ORIGINAL_INTERVAL = 38.9768;
    /***
     * 目前采用的是这个版本
     *
     * @param locator2 获取到的gps经纬度封装;
     * @return 相对坐标;
     *//*
    public static Locator2 getLoationInView(Locator2 locator2) {
        double xi = (locator2.x - ORIGINAL_X) * Param.ACTUAL_IMAGE_SIZE / ORIGINAL_INTERVAL - Param.ACTUAL_IMAGE_SIZE
         / 2;
        double yi = (ORIGINAL_Y - locator2.y) * Param.ACTUAL_IMAGE_SIZE / ORIGINAL_INTERVAL - Param.ACTUAL_IMAGE_SIZE
         / 2;

        return new Locator2(xi, yi);
    }


    *//***
     * 目前采用的是这个版本
     *
     * @param locator2 获取到的gps经纬度封装;
     * @return 相对坐标, 不是针对中心点的啊;
     *//*
    public static Locator2 getLoationInView2(Locator2 locator2) {
        double xi = (locator2.x - ORIGINAL_X) * Param.ACTUAL_IMAGE_SIZE / ORIGINAL_INTERVAL;
        double yi = (ORIGINAL_Y - locator2.y) * Param.ACTUAL_IMAGE_SIZE / ORIGINAL_INTERVAL;
        return new Locator2(xi, yi);
    }*/


    /***
     * 针对单个点的坐标
     *
     * @param l
     * @return
     */
    public static int transferLocate(int l) {
        return (int) (l * Param.ACTUAL_IMAGE_SIZE / Param.ORIGINAL_IMAGE_SIZE - Param.ACTUAL_IMAGE_SIZE / 2);
    }

    /*//是上一个方法的逆运算;相对中心点坐标转未缩放view坐标
    public static int transferLocate2Origin(float x) {
        return (int) ((x + Param.ACTUAL_IMAGE_SIZE / 2) * Param.ORIGINAL_IMAGE_SIZE / Param.ACTUAL_IMAGE_SIZE);
    }*/

    public static String transferReplace(String src, String oldChar,
                                         String newChar) {
        StringBuilder src0 = new StringBuilder();
        src0.append(src);
        return transferReplace(src0, oldChar, newChar);
    }

    public static String transferReplace(StringBuilder src, String oldChar,
                                         String newChar) {
        int index = src.indexOf(oldChar);
        while (index >= 0) {
            if (index % 2 == 0) {
                src = src.replace(index, index + 4, newChar);
            }
            index = src.indexOf(oldChar, index + 2);
        }
        return src.toString();
    }

    public static int findSilpBagHead(String s) {
        int begin = s.indexOf("c0");
        while (begin > 0 && begin % 2 != 0) {
            begin = s.indexOf("c0", begin + 2);
        }
        if (begin % 2 == 0 && s.length() - begin >= 2) {
            if (s.charAt(begin + 2) == 'c' && s.charAt(begin + 3) == '0') {
                begin = begin + 2;
            }
        }
        return begin;
    }

    public static int findSilpBagTail(int begin, String s) {
        int tail = s.indexOf("c0", begin + 2);
        while (tail > 0 && tail % 2 != 0) {
            tail = s.indexOf("c0", tail + 2);
        }
        return tail;
    }

    public static String str2time(String str) {

        SimpleDateFormat formatter1 = new SimpleDateFormat("yy-HH-dd HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyHHddHHmmss");
        try {
            str = formatter1.format(formatter2.parse(str));
        } catch (ParseException e) {
            Log.e("timeparse_error", str);
            e.printStackTrace();
        }
        return str;
    }

/*    //针对gps和点击选区域;
    public static final double ORIGINAL_X = 98.7379;
    public static final double ORIGINAL_Y = 41.23107;
    public static final double ORIGINAL_INTERVAL = 38.9768;

    public static final List<SeaArea> list = new ArrayList<>();


    //这样gps位置也可以调用这个方法,实现在图片中的
    public static Locater getLoationInView(double x, double y) {
        int xi = (int) Math.round((x - ORIGINAL_X) / ORIGINAL_INTERVAL);
        int yi = (int) Math.round((ORIGINAL_Y - y) / ORIGINAL_INTERVAL);

        return new Locater(xi, yi);
    }


    //GPS单个点
    public static Locater getLoationInView(int x, int y) {
        int xi = (int) Math.round((x - ORIGINAL_X) / ORIGINAL_INTERVAL);
        int yi = (int) Math.round((ORIGINAL_Y - y) / ORIGINAL_INTERVAL);

        return new Locater(xi, yi);
    }


    public static boolean pInQuadrangle(Locater a, Locater b, Locater c, Locater d, Locater p) {
        double dTriangle = triangleArea(a, b, p) + triangleArea(b, c, p)
                + triangleArea(c, d, p) + triangleArea(d, a, p);
        double dQuadrangle = triangleArea(a, b, c) + triangleArea(c, d, a);
        return dTriangle == dQuadrangle;
    }


    */


    //TODO:返回所在哪个区域
/*    public static int whichArea(Locator2 locator2) {
        Locater lo = new Locater((int) (locator2.x + 0.5), (int) (locator2.y + 0.5));
        for (int i = 0; i < Param.seaAreas2.length; i++) {
            if (Tools.pInQuadrangle(Param.seaAreas2[i], lo)) {
                return i;
            }
        }
        return 0;
    }*/

    public static void initMapPic(Context context) {
        int index = 0;
        if ((Param.my_authority & 0x01) > 0) {
            Param.AUTHORITY[Param.SHANDONG] = true;
            Param.MAP_PIC.add(R.drawable.p1);
            Param.map2position.put(Param.SHANDONG_0, index);
            Param.map2SeaBean.put(Param.SHANDONG_0, new SeaBean(context, Param.SHANDONG_0, index));
            index++;
        }

        if ((Param.my_authority & 0x02) > 0) {
            Param.AUTHORITY[Param.MAOMING] = true;
            Param.MAP_PIC.add(R.drawable.p2);
            Param.MAP_PIC.add(R.drawable.p3);
            Param.map2position.put(Param.MAOMING_0, index);
            Param.map2SeaBean.put(Param.SHANDONG_0, new SeaBean(context, Param.SHANDONG_0, index));
            index++;
            Param.map2position.put(Param.MAOMING_1, index);
            Param.map2SeaBean.put(Param.SHANDONG_0, new SeaBean(context, Param.SHANDONG_0, index));
            index++;
        }

/*        if ((Param.my_authority & 0x04) > 0) {
            Param.AUTHORITY[Param.ZHOUSHAN] = true;
            Param.MAP_PIC.add(R.drawable.p4);
            // TODO: 2017/5/26 0026  还要特别注意一下四个小图

            Param.map2position.put(Param.ZHOUSHAN_0, index);
            index++;
        }*/
    }
}
