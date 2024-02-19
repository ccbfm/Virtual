package com.virtual.util.network;

import java.util.HashMap;

public final class VSimpleMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 8370380184504464150L;

    public static VSimpleMap create() {
        return new VSimpleMap();
    }

    public VSimpleMap putKV(String key, Object value) {
        this.put(key, value);
        return this;
    }
}
