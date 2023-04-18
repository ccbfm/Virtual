package com.virtual.util.persist;

import android.text.TextUtils;

import com.virtual.util.persist.file.VFilePath;

import java.util.HashMap;

public class VPersistConfig {
    public static final String DEFAULT_PERSIST = "default_persist";

    private VPersistConfig() {
    }

    private static final class Singleton {
        private static final VPersistConfig INSTANCE = new VPersistConfig();
    }

    public static VPersistConfig instance() {
        return Singleton.INSTANCE;
    }

    private final HashMap<String, Builder> mBuilderMap = new HashMap<>();

    private void putBuilder(Builder builder) {
        mBuilderMap.put(builder.persistName, builder);
    }

    public Builder getBuilder() {
        return getBuilder(DEFAULT_PERSIST);
    }

    public Builder getBuilder(String persistName) {
        if (TextUtils.isEmpty(persistName)) {
            persistName = DEFAULT_PERSIST;
        }
        Builder builder = mBuilderMap.get(persistName);
        if (builder == null) {
            builder = createBuilder().setPersistName(persistName).build();
        }
        return builder;
    }

    public Builder createBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private final VPersistConfig persistConfig;
        private String persistName;
        private int persistType = VPersistType.SP;
        private String persistDir;

        public Builder(VPersistConfig persistConfig) {
            this.persistConfig = persistConfig;
        }

        public Builder setPersistName(String persistName) {
            this.persistName = persistName;
            return this;
        }

        public Builder setPersistType(int persistType) {
            this.persistType = persistType;
            return this;
        }

        public Builder setPersistDir(String persistDir) {
            this.persistDir = persistDir;
            return this;
        }

        public String getPersistName() {
            return this.persistName;
        }

        public int getPersistType() {
            return this.persistType;
        }

        public String getPersistDir() {
            if (this.persistDir == null) {
                this.persistDir = VFilePath.getExternalFilesDir(this.persistName).getAbsolutePath();
            }
            return this.persistDir;
        }

        public Builder build() {
            if (TextUtils.isEmpty(this.persistName)) {
                this.persistName = DEFAULT_PERSIST;
            }
            this.persistConfig.putBuilder(this);
            return this;
        }

        public boolean isPersistBySp() {
            return checkPersistType(VPersistType.SP);
        }

        public boolean isPersistByFile() {
            return checkPersistType(VPersistType.FILE);
        }

        public boolean isPersistByDb() {
            return checkPersistType(VPersistType.DB);
        }

        public boolean checkPersistType(int checkType) {
            return (this.persistType & checkType) == checkType;
        }
    }
}
