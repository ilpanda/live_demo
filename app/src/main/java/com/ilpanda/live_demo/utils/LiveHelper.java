package com.ilpanda.live_demo.utils;

public class LiveHelper {

    public static String getShowCount(int count) {
        String res = "";
        if (count < 10000) {
            res = String.valueOf(count);
        } else {
            int a = count / 10000;

            if (count - a * 10000 == 0) {
                res = a + ".0w";
            } else {
                int b = (count - a * 10000) / 1000;
                if (b == 0) {
                    res = a + ".0w";
                } else {
                    res = a + "." + b + "w";
                }
            }
        }
        return res;
    }
}
