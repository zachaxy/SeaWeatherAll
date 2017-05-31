package com.example;

import static java.lang.Float.floatToIntBits;

/**
 * Created by zhangxin on 2017/5/31 0031.
 * <p>
 * Description :
 */

public class Client2 {
    public static void main(String[] args) {
        float f1 = 120.123f;
        int i = Float.floatToIntBits(f1);

        System.out.println(i);
        byte[] bs = intToByteArray(i);

        i = byteArrayToInt(bs);
        System.out.println(i);

        f1 = Float.intBitsToFloat(i);
        System.out.println(f1);
    }

    /**
     * int到byte[]
     *
     * @param i
     * @return
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * byte[]转int
     *
     * @param bytes
     * @return
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }
}
