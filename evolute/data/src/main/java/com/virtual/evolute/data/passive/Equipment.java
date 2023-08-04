package com.virtual.evolute.data.passive;

import com.virtual.evolute.data.Passive;

import java.util.HashMap;

public class Equipment extends Passive {
    public final HashMap<String, Attribute> attributeMap = new HashMap<>();

    public Equipment(String key, String name) {
        super(key, name);
    }

    public static class Attribute {
        public int value;
    }

    public @interface Name {

    }
}
