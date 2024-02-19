package com.virtual.util.common.bus;

import androidx.lifecycle.MutableLiveData;

public final class LiveDataBus extends HashMapMode<String, MutableLiveData<?>> {

    private LiveDataBus() {
        super();
    }

    @SuppressWarnings("unchecked")
    public <T> MutableLiveData<T> with(String key) {
        return (MutableLiveData<T>) getAndCreateValue(key);
    }

    private static class SingleTonHolder {
        private final static LiveDataBus INSTANCE = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return SingleTonHolder.INSTANCE;
    }


    @Override
    protected MutableLiveData<?> createValue() {
        return new MutableLiveData<>();
    }

}