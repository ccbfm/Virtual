package com.virtual.util.log;

public final class VLevelString {

    public static String levelString(@VLevel int level) {
        switch (level) {
            case VLevel.D:
                return "D";
            case VLevel.I:
                return "I";
            case VLevel.W:
                return "W";
            case VLevel.E:
                return "E";
            case VLevel.NONE:
                break;
        }
        return level + "";
    }
}
