package com.virtual.util.log;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        VLevel.D,
        VLevel.I,
        VLevel.W,
        VLevel.E,
        VLevel.NONE
})
@Retention(RetentionPolicy.SOURCE)
public @interface VLevel {

    int D = 1;

    int I = D + 1;

    int W = I + 1;

    int E = W + 1;

    int NONE = E + 1;
}
