package com.virtual.util.common;

import java.util.Random;

public class RandomUtils {

    private static Random sRandom;

    private static void checkRandom() {
        if (sRandom == null) {
            sRandom = new Random();
        }
    }

    public static int randomInt(int bound) {
        if (bound < 0) {
            return 0;
        }
        checkRandom();
        return sRandom.nextInt(bound);
    }

    public static int randomInt(int start, int bound) {
        return randomInt(start, bound, false);
    }

    public static int randomInt(int start, int bound, boolean flag) {
        if (bound < 0) {
            return 0;
        }
        checkRandom();
        if (flag) {
            int f = sRandom.nextInt(2);
            return f == 0 ? start + sRandom.nextInt(bound) : start - sRandom.nextInt(bound);
        }
        return start + sRandom.nextInt(bound);
    }

    public static int randomRange(int start, int end) {
        int s, bound;
        if (start > end) {
            s = end;
            bound = start - end;
        } else {
            s = start;
            bound = end - start;
        }
        if (bound <= 0) {
            return s;
        } else {
            bound = bound + 1;
        }
        checkRandom();
        return s + sRandom.nextInt(bound);
    }


}
