package com.virtual.util.persist.db;

import android.content.Context;
import android.text.TextUtils;

import com.virtual.util.persist.db.dao.IVDao;
import com.virtual.util.persist.db.dao.VDbPersistDao;

import java.util.HashMap;
import java.util.Map;

public class VDbConfig {
    public static final String DEFAULT_DB_NAME = "default_db.db";

    private VDbConfig() {
    }

    private static final class Singleton {
        private static final VDbConfig INSTANCE = new VDbConfig();
    }

    public static VDbConfig instance() {
        return Singleton.INSTANCE;
    }


    private final HashMap<String, Builder> mDbMap = new HashMap<>();


    public Builder getDb(Context context, String name) {
        return getDb(context, name, "");
    }

    public Builder getDb(Context context, String name, String tableName) {
        Builder builder = mDbMap.get(name);
        if (builder == null) {
            if (DEFAULT_DB_NAME.equals(name)) {
                if (TextUtils.isEmpty(tableName)) {
                    throw new NullPointerException("Db tableName is null.");
                }
                return createBuilder(context)
                        .setName(name)
                        .setVersion(1)
                        .addIVDao(new VDbPersistDao(tableName))
                        .build();
            }
        }
        return builder;
    }

    private void putDb(Builder builder) {
        mDbMap.put(builder.name, builder);
    }

    public Builder createBuilder(Context context) {
        return new Builder(this, context);
    }


    public static class Builder {
        private final VDbConfig dbConfig;
        private String name;
        private int version;
        private final Map<String, IVDao> daoMap = new HashMap<>();
        private VDbHelper dbHelper;
        private final Context context;

        Builder(VDbConfig dbConfig, Context context) {
            this.dbConfig = dbConfig;
            this.context = context;
        }

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public int getVersion() {
            return version;
        }

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder addIVDao(IVDao dao) {
            this.daoMap.put(dao.tableName(), dao);
            return this;
        }

        private VDbHelper getDbHelper() {
            return this.dbHelper;
        }

        public IVDao getDao(String tableName) {
            return this.daoMap.get(tableName);
        }

        public Builder build() {
            this.dbHelper = new VDbHelper(this.context,
                    name, null, version, this.daoMap.values());
            this.dbHelper.getReadableDatabase();
            this.dbHelper.getWritableDatabase();
            dbConfig.putDb(this);
            return this;
        }

        public void deleteAll() {
            for (IVDao dao : this.daoMap.values()) {
                dao.delete(null, null);
            }
        }

    }
}
