package com.virtual.util.common.bus;

import java.util.HashMap;

public abstract class HashMapMode<K, V> {
    private final static boolean DEBUG = false;
    private final static String TAG = "HashMapMode";

    private final HashMap<K, V> mHashMap;

    protected HashMapMode() {
        this(16);
    }

    protected HashMapMode(int initialCapacity) {
        mHashMap = new HashMap<>(initialCapacity);
    }

    protected V getAndCreateValue(K key) {
        V value = mHashMap.get(key);
        if (value == null) {
            synchronized (this) {
                value = mHashMap.get(key);
                if (value == null) {
                    value = createValue();
                    mHashMap.put(key, value);
                }
            }
        }
        return value;
    }

    protected V getValue(K key) {
        return mHashMap.get(key);
    }

    public V removeValue(K service) {
        synchronized (this) {
            return mHashMap.remove(service);
        }
    }

    public void clear() {
        synchronized (this) {
            mHashMap.clear();
        }
    }

    /**
     * 创建泛型对象
     *
     * @return V
     */
    protected abstract V createValue();
}
