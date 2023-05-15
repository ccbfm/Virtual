package com.virtual.evolute.control.produce.world;

import com.virtual.evolute.control.produce.WorldProduce;
import com.virtual.evolute.data.Place;
import com.virtual.util.math.MathRandom;

import java.util.Random;

public class SmallWorldProduce extends WorldProduce {


    public SmallWorldProduce(WorldParam worldParam) {
        super(worldParam);

    }

    @Override
    protected String key() {
        return "small_world_" + System.nanoTime() + MathRandom.get().nextInt(99);
    }

    @Override
    protected String name() {
        return "";
    }

    @Override
    protected Place createPlace() {
        Random random = MathRandom.get("create_place");
        Place place = new Place("place_" + System.nanoTime() + "_" + random.nextInt(99), "");
        return place;
    }

}
