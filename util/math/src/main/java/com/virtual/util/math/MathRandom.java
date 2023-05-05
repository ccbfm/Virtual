package com.virtual.util.math;

import java.util.HashMap;
import java.util.Random;

public class MathRandom {

    public static Random random() {
        return new Random();
    }

    private static final HashMap<String, Random> RANDOM_MAP = new HashMap<>();

    public static Random get() {
        return get("default");
    }

    public static Random get(String key) {
        Random random = RANDOM_MAP.get(key);
        if (random == null) {
            random = random();
            RANDOM_MAP.put(key, random);
        }
        return random;
    }
}
