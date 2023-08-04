package com.virtual.evolute.control.produce;

import com.virtual.evolute.data.Place;

public class PlaceProduce implements IProduce<Place> {

    protected PlaceParam mPlaceParam;

    public PlaceProduce(PlaceParam placeParam) {
        mPlaceParam = placeParam;
    }

    @Override
    public Place produce() {
        return null;
    }

    public static class PlaceParam {

    }
}
