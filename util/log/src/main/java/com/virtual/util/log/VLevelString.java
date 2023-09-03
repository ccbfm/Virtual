package com.virtual.util.log;

public final class VLevelString {

    public static String levelString(@VLogLevel int level) {
        switch (level) {
            case VLogLevel.D:
                return "D";
            case VLogLevel.I:
                return "I";
            case VLogLevel.W:
                return "W";
            case VLogLevel.E:
                return "E";
            case VLogLevel.NONE:
                break;
        }
        return level + "";
    }
}
