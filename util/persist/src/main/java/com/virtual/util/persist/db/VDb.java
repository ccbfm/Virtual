package com.virtual.util.persist.db;

import android.content.Context;

import com.virtual.util.persist.db.dao.IVDao;

public final class VDb {

    public static VDbConfig.Builder get(Context context, String tableName) {
        return get(context, VDbConfig.DEFAULT_DB_NAME, tableName);
    }

    public static VDbConfig.Builder get(Context context, String name, String tableName) {
        return VDbConfig.instance().getDb(context, name, tableName);
    }

    public static <T extends IVDao> T getVDao(Context context, String tableName, Class<T> clazz) {
        return getVDao(context, VDbConfig.DEFAULT_DB_NAME, tableName, clazz);
    }

    public static <T extends IVDao> T getVDao(Context context, String name, String tableName, Class<T> clazz) {
        return clazz.cast(get(context, name, tableName).getDao(tableName));
    }
}
