package com.example;

/**
 * Created by zhangxin on 2017/5/31 0031.
 * <p>
 * Description :
 */

public class Beans {
    Bean a;
    Bean b;
    Bean c;
    Bean d;
    Bean e;
    Bean f;
    Bean g;

    public int size;

    public double area = 0;

    StringBuilder sb = new StringBuilder();

    public Beans(Bean a, Bean b, Bean c, Bean d, int size) {
        this.a = func11(a);
        this.b = func11(b);
        this.c = func11(c);
        this.d = func11(d);
        this.size = size;


    }

    public Beans(Bean a, Bean b, Bean c, Bean d, Bean e, int size) {
        this(a, b, c, d, size);
        this.e = func11(e);
    }

    public Beans(Bean a, Bean b, Bean c, Bean d, Bean e, Bean f, int size) {
        this(a, b, c, d, e, size);
        this.f = func11(f);

    }

    public Beans(Bean a, Bean b, Bean c, Bean d, Bean e, Bean f, Bean g, int size) {
        this(a, b, c, d, e, f, size);
        this.g = func11(g);

    }


    int pc = 1500;
    int tab = 800;

    Bean func11(Bean bean) {
        int x = bean.x * tab / pc;
        int y = bean.y * tab / pc;
        sb.append("new Locater(" + x + "," + y + "),");
        return new Bean(x, y);
    }
}
