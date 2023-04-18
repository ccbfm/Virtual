package com.virtual.util.persist;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        VPersistType.SP,
        VPersistType.FILE,
        VPersistType.DB
})
@Retention(RetentionPolicy.SOURCE)
public @interface VPersistType {
    int SP = 1;
    int FILE = 2;
    int DB = 4;
}
