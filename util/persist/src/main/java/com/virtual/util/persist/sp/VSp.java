package com.virtual.util.persist.sp;

import android.content.Context;

import androidx.annotation.NonNull;

import com.virtual.util.persist.VPersistConfig;

public final class VSp {

    public static VSpConfig.Builder get(@NonNull Context context) {
        return get(context, VPersistConfig.DEFAULT_PERSIST);
    }

    public static VSpConfig.Builder get(@NonNull Context context, String name) {
        return VSpConfig.instance().getSp(context, name);
    }

}
