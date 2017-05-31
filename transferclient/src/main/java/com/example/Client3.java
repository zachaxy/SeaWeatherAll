package com.example;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by zhangxin on 2017/5/31 0031.
 * <p>
 * Description : 用来配置茂名0的区域；
 * 首先把1500的数据转成800的，然后再算面积；
 */

public class Client3 {
    static ArrayList<Beans> lists1 = new ArrayList();
    static ArrayList<Beans> lists2 = new ArrayList();

    public static void main(String[] args) {
        f1();
        f2();

    }

    static  boolean debug = false;

    static void f1() {
        lists1.add(new Beans(new Bean(830, 106), new Bean(948, 4), new Bean(1104, 5), new Bean(1053, 273), 4));
        lists1.add(new Beans(new Bean(764, 159), new Bean(830, 106), new Bean(976, 272), new Bean(849, 283), 4));
        lists1.add(new Beans(new Bean(655, 173), new Bean(764, 159), new Bean(849, 283), new Bean(711, 300), 4));
        lists1.add(new Beans(new Bean(550, 231), new Bean(581, 151), new Bean(655, 173), new Bean(674, 213), 4));
        lists1.add(new Beans(new Bean(550, 231), new Bean(674, 213), new Bean(711, 300), new Bean(595, 312), 4));
        lists1.add(new Beans(new Bean(460, 240), new Bean(550, 231), new Bean(595, 312), new Bean(509, 334), 4));
        lists1.add(new Beans(new Bean(383, 327), new Bean(405, 359), new Bean(509, 334), new Bean(460, 240), new Bean(346, 279), 5));
        lists1.add(new Beans(new Bean(311, 358), new Bean(335, 338), new Bean(383, 327), new Bean(405, 359), 4));
        lists1.add(new Beans(new Bean(140, 551), new Bean(5, 346), new Bean(327, 166), new Bean(335, 338), new Bean(311, 358), new Bean(281, 463), 6));
        lists1.add(new Beans(new Bean(140, 551), new Bean(281, 463), new Bean(355, 448), new Bean(367, 707), new Bean(278, 708), 5));
        lists1.add(new Beans(new Bean(355, 448), new Bean(405, 359), new Bean(595, 312), new Bean(602, 754), new Bean(470, 753), new Bean(467, 707), new Bean(367, 707), 7));
        lists1.add(new Beans(new Bean(595, 312), new Bean(976, 272), new Bean(976, 494), new Bean(598, 486), 4));
        lists1.add(new Beans(new Bean(598, 486), new Bean(1034, 495), new Bean(1009, 760), new Bean(602, 754), 4));
        lists1.add(new Beans(new Bean(976, 272), new Bean(1207, 276), new Bean(1263, 494), new Bean(976, 494), 4));
        lists1.add(new Beans(new Bean(470, 753), new Bean(1009, 760), new Bean(1023, 909), new Bean(816, 1094), new Bean(397, 1093), new Bean(473, 1006), 6));
        lists1.add(new Beans(new Bean(278, 708), new Bean(467, 707), new Bean(473, 1006), new Bean(265, 904), 4));
        lists1.add(new Beans(new Bean(265, 904), new Bean(473, 1006), new Bean(397, 1093), new Bean(6, 1090), 4));
        lists1.add(new Beans(new Bean(281, 1096), new Bean(816, 1094), new Bean(817, 1219), new Bean(289, 1447), 4));


    }

    static void f2() {
        System.out.println("new SeaArea(),");
        for (Beans beans : lists1) {
            int size = beans.size;
            double area = area(size, beans);
            System.out.println("new SeaArea("+beans.sb.toString()+area+"),");
        }
    }


    /***
     * 查看p是否在区域中;
     * <p>
     * 遍历18个海区;
     * 当前坐标p,并不是相对中心点的,而是相对于移动端原始view的大小;
     *
     * @return
     */
    public static boolean pInQuadrangle(Beans seaArea, Bean p) {
        double dTriangle = -1;
        if (seaArea.size == 4) {
            dTriangle = triangleArea(seaArea.a, seaArea.b, p)
                    + triangleArea(seaArea.b, seaArea.c, p)
                    + triangleArea(seaArea.c, seaArea.d, p)
                    + triangleArea(seaArea.d, seaArea.a, p);
        } else if (seaArea.size == 5) {
            dTriangle = triangleArea(seaArea.a, seaArea.b, p)
                    + triangleArea(seaArea.b, seaArea.c, p)
                    + triangleArea(seaArea.c, seaArea.d, p)
                    + triangleArea(seaArea.d, seaArea.e, p)
                    + triangleArea(seaArea.e, seaArea.a, p);
        }
        return dTriangle == seaArea.area;
    }


    static double area(int size, Beans beans) {
        switch (size) {
            case 4:
                return triangleArea(beans.a, beans.b, beans.c, beans.d);
            case 5:
                return triangleArea(beans.a, beans.b, beans.c, beans.d, beans.e);
            case 6:
                return triangleArea(beans.a, beans.b, beans.c, beans.d, beans.e, beans.f);
            case 7:
                return triangleArea(beans.a, beans.b, beans.c, beans.d, beans.e, beans.f, beans.g);
            default:
                System.out.println("!!!!!!!!!!!!");
                return 0;
        }
    }

    // 返回三个点组成三角形的面积,既然面积这么算最多也就是个double,那么直接传入整数吧;
    //全部转为整数,所得的面积最多是xxx.5,可以用==精确比对,缺点是:存在各个海区的边界值误差
    private static double triangleArea(Bean a, Bean b, Bean c) {
        double result = Math.abs((a.x * b.y + b.x * c.y + c.x * a.y - b.x * a.y
                - c.x * b.y - a.x * c.y) / 2.0);
        return result;
    }

    //计算四边形面积
    private static double triangleArea(Bean a, Bean b, Bean c, Bean d) {
        return triangleArea(a, b, c) + triangleArea(c, d, a);
    }

    //计算五边形面积
    private static double triangleArea(Bean a, Bean b, Bean c, Bean d, Bean e) {
        return triangleArea(a, b, c) + triangleArea(a, c, d) + triangleArea(a, d, e);
    }

    //计算6边形面积
    private static double triangleArea(Bean a, Bean b, Bean c, Bean d, Bean e, Bean f) {
        if (debug){
            System.out.println("666");
        }
        return triangleArea(a, b, c) + triangleArea(a, c, d) + triangleArea(a, d, e) + triangleArea(a, e, f);
    }

    //计算7边形面积
    private static double triangleArea(Bean a, Bean b, Bean c, Bean d, Bean e, Bean f, Bean g) {
        if (debug){
            System.out.println("777");
        }
        return triangleArea(a, b, c) + triangleArea(a, c, d) + triangleArea(a, d, e) + triangleArea(a, e, f) +
                triangleArea(a, f, g);
    }

}
