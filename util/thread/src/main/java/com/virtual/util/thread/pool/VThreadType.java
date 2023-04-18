package com.virtual.util.thread.pool;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        VThreadType.SINGLE,
        VThreadType.CACHED,
        VThreadType.IO,
        VThreadType.CPU
})
@Retention(RetentionPolicy.SOURCE)
public @interface VThreadType {

    int SINGLE = -1;
    int CACHED = -2;
    int IO = -4;
    int CPU = -8;
}
