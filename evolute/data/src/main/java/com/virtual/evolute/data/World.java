package com.virtual.evolute.data;

import java.util.HashMap;

public class World extends Body {
    public String[][] map;
    public HashMap<String, Place> placeMap;


    public World(String key, String name) {
        super(key, name);
    }
}
