package com.virtual.evolute.control.produce;

import com.virtual.evolute.data.Place;
import com.virtual.evolute.data.World;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class WorldProduce implements IProduce<World> {

    protected WorldParam mWorldParam;

    public WorldProduce(WorldParam worldParam) {
        mWorldParam = worldParam;
    }

    protected String[][] map(HashMap<String, Place> placeMap) {
        WorldParam worldParam = mWorldParam;
        String[][] map = new String[worldParam.countY][worldParam.countX];
        for (int i = 0; i < worldParam.countY; i++) {
            for (int j = 0; j < worldParam.countX; j++) {
                Place place = createPlace();
                map[i][j] = place.key;
                placeMap.put(place.key, place);

                place.around = new LinkedList<>();

                int preY = i - 1;
                if (preY >= 0) {
                    float angleA = 0f, angleB = 180f;
                    for (int k = j - 1, end = j + 1; k <= end; k++) {
                        if (k >= 0) {
                            String preStr = map[preY][j];
                            Place prePlace = placeMap.get(preStr);
                            angleA += 45f;
                            angleB += 45f;
                            if (prePlace != null) {
                                prePlace.around.add(new Place.Around(place.key, place.name, angleA));
                                place.around.add(new Place.Around(prePlace.key, prePlace.name, angleB));
                            }
                        }
                    }
                }
                int preX = j - 1;
                if (preX >= 0) {
                    String preStr = map[i][preX];
                    Place prePlace = placeMap.get(preStr);
                    if (prePlace != null) {
                        prePlace.around.add(new Place.Around(place.key, place.name, 0f));
                        place.around.add(new Place.Around(prePlace.key, prePlace.name, 180f));
                    }
                }
            }
        }
        return map;
    }

    protected abstract String key();

    protected abstract String name();

    protected abstract Place createPlace();

    @Override
    public World produce() {
        World world = new World(key(), "");
        world.placeMap = new HashMap<>();
        world.map = map(world.placeMap);
        return world;
    }

    public static class WorldParam {
        public int countX;
        public int countY;
    }

}
