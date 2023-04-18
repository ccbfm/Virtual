package com.virtual.util.persist.db;

import com.virtual.util.persist.db.dao.IVDao;

public final class VDb {

    public static VDbConfig.Builder get(String tableName) {
        return get(VDbConfig.DEFAULT_DB_NAME, tableName);
    }

    public static VDbConfig.Builder get(String name, String tableName) {
        return VDbConfig.instance().getDb(name, tableName);
    }

    public static <T extends IVDao> T getVDao(String tableName, Class<T> clazz) {
        return getVDao(VDbConfig.DEFAULT_DB_NAME, tableName, clazz);
    }

    public static <T extends IVDao> T getVDao(String name, String tableName, Class<T> clazz) {
        return clazz.cast(get(name, tableName).getDao(tableName));
    }
}
