package com.virtual.util.persist.sp;

import com.virtual.util.persist.VPersistConfig;

public final class VSp {

    public static VSpConfig.Builder get() {
        return get(VPersistConfig.DEFAULT_PERSIST);
    }

    public static VSpConfig.Builder get(String name) {
        return VSpConfig.instance().getSp(name);
    }

}
