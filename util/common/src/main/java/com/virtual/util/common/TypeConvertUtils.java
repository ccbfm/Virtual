package com.virtual.util.common;

import java.util.LinkedList;
import java.util.List;

public class TypeConvertUtils {

    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new LinkedList<T>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

}
