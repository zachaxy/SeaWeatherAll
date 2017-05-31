package com.example;

import java.util.ArrayList;

public class Client {
    int[] datas;

    public static void main(String[] args) {
        func1();
        System.out.println("##########");
        func2();
        System.out.println("##########");
        func3();
    }


    void init() {
        int[] tmp = {1, 2, 3, 4, 5};
        datas = tmp;
    }

    private static void func() {
        for (int i = 1; i <= 45; i++) {
            System.out.println("R.drawable.a" + i + ",");
        }
    }


    static int pc = 1500;
    static int tab = 800;


    //将PC上的坐标点（海区气象显示点转换为平板上的点）,f1不动了，这个数据是：4677的
    static void func1() {
        ArrayList<Bean> list = new ArrayList<>();
        list.add(new Bean(2570, 200));
        list.add(new Bean(2718, 362));
        list.add(new Bean(2976, 256));
        list.add(new Bean(3002, 565));
        list.add(new Bean(3020, 850));
        list.add(new Bean(3380, 1150));
        list.add(new Bean(3020, 1640));
        list.add(new Bean(2365, 2087));
        list.add(new Bean(2910, 2120));
        list.add(new Bean(2710, 2535));
        list.add(new Bean(910, 2570));
        list.add(new Bean(1356, 2500));
        list.add(new Bean(1676, 2455));
        list.add(new Bean(2105, 2444));
        list.add(new Bean(1475, 2970));
        list.add(new Bean(2220, 2950));
        list.add(new Bean(1530, 3535));
        list.add(new Bean(2155, 3535));


        //要打印一个0 0 的位置，因为区域号是从1开始的，而不是从0开始的。。。
        System.out.println("new Locater(0, 0),");
        for (Bean bean : list) {
            int x = func11(bean.x);
            int y = func11(bean.y);
            System.out.println("new Locator(" + x + "," + y + "),");
        }
    }

    static int func11(int x) {
        return x * tab / pc - tab / 2;
    }

    static void func2() {
        ArrayList<Bean> list = new ArrayList<>();
        list.add(new Bean(976, 81));
        list.add(new Bean(851, 172));
        list.add(new Bean(740, 193));
        list.add(new Bean(618, 239));
        list.add(new Bean(581, 124));
        list.add(new Bean(530, 269));
        list.add(new Bean(429, 286));
        list.add(new Bean(360, 344));
        list.add(new Bean(180, 338));
        list.add(new Bean(300, 524));
        list.add(new Bean(480, 457));
        list.add(new Bean(824, 342));
        list.add(new Bean(824, 566));
        list.add(new Bean(1189, 320));
        list.add(new Bean(760, 839));
        list.add(new Bean(375, 760));
        list.add(new Bean(273, 965));
        list.add(new Bean(507, 1167));


        //要打印一个0 0 的位置，因为区域号是从1开始的，而不是从0开始的。。。
        System.out.println("new Locater(0, 0),");
        for (Bean bean : list) {
            int x = func11(bean.x);
            int y = func11(bean.y);
            System.out.println("new Locater(" + x + "," + y + "),");
        }
    }

    static void func3() {
        ArrayList<Bean> list = new ArrayList<>();

        list.add(new Bean(1408, 230));
        list.add(new Bean(1284, 388));
        list.add(new Bean(1055, 508));
        list.add(new Bean(806, 474));
        list.add(new Bean(743, 665));
        list.add(new Bean(594, 522));
        list.add(new Bean(542, 697));
        list.add(new Bean(348, 576));
        list.add(new Bean(214, 712));
        list.add(new Bean(45, 798));

        //要打印一个0 0 的位置，因为区域号是从1开始的，而不是从0开始的。。。
        System.out.println("new Locater(0, 0),");
        for (Bean bean : list) {
            int x = func11(bean.x);
            int y = func11(bean.y);
            System.out.println("new Locater(" + x + "," + y + "),");
        }
    }
}
