package com.virtual.evolute.data;

import java.util.List;

public class Place extends Body {

    public List<Around> around;

    public Place(String key, String name) {
        super(key, name);
    }

    public static class Around extends Body {
        public float angle;

        public Around(String key, String name, float angle) {
            super(key, name);
            this.angle = angle;
        }
    }


}
