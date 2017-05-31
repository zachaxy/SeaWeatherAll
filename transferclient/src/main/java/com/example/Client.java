package com.example;

import java.util.ArrayList;

public class Client {

    public static void main(String[] args) {
        func1();
    }


    static int pc = 4677;
    static int tab = 800;


    //将PC上的坐标点（海区气象显示点转换为平板上的点）
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
}
