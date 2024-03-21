package com.virtual.util.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RecordValidTime {

    private final HashMap<String, Long> mRecordMap = new HashMap<>();
    private final long mDelayTime;

    public RecordValidTime(long delayTime) {
        mDelayTime = delayTime;
    }

    public void conductData(long currentTime) {
        HashSet<String> removeKeySet = new HashSet<>();
        if (mRecordMap.size() > 0) {
            for (Map.Entry<String, Long> entry : mRecordMap.entrySet()) {
                if (entry.getValue() + mDelayTime > currentTime) {
                    continue;
                }
                removeKeySet.add(entry.getKey());
            }
        }
        if (removeKeySet.size() > 0) {
            for (String key : removeKeySet) {
                mRecordMap.remove(key);
            }
        }
    }

    public boolean checkRecord(String key) {
        return mRecordMap.containsKey(key);
    }

    public void putRecord(String key, long currentTime) {
        mRecordMap.put(key, currentTime);
    }
}
