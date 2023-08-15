package com.virtual.util.persist;

import android.content.Context;
import android.text.TextUtils;

import com.virtual.util.persist.db.VDb;
import com.virtual.util.persist.db.dao.VDbPersistDao;
import com.virtual.util.persist.file.VFile;
import com.virtual.util.persist.file.VFileIO;
import com.virtual.util.persist.sp.VSp;

public final class VPersist {

    public static VPersistConfig.Builder getBuilder(Context context, String persistName) {
        return VPersistConfig.instance().getBuilder(context, persistName);
    }

    public static void setValue(Context context, String key, String value) {
        setValue(VPersistConfig.instance().getBuilder(context), key, value);
    }

    public static void setValue(Context context, String persistName, String key, String value) {
        setValue(getBuilder(context, persistName), key, value);
    }

    public static void setValue(VPersistConfig.Builder builder, String key, String value) {
        if (builder.isPersistBySp()) {
            VSp.get(builder.getContext(), builder.getPersistName()).putString(key, value);
        }
        if (builder.isPersistByDb()) {
            VDb.getVDao(builder.getContext(), builder.getPersistName(), VDbPersistDao.class)
                    .insertValue(key, value);
        }
        if (builder.isPersistByFile()) {
            VFileIO.writeFileFromString(VFile.getFileByDirAndName(builder.getPersistDir(), key), value, false);
        }
    }

    public static String getValue(Context context, String key) {
        return getValue(VPersistConfig.instance().getBuilder(context), key);
    }

    public static String getValue(Context context, String persistName, String key) {
        return getValue(getBuilder(context, persistName), key);
    }

    public static String getValue(VPersistConfig.Builder builder, String key) {
        String value = null;
        if (builder.isPersistBySp()) {
            value = VSp.get(builder.getContext(), builder.getPersistName()).getString(key);
        }
        if (!TextUtils.isEmpty(value)) {
            return value;
        }
        if (builder.isPersistByDb()) {
            value = VDb.getVDao(builder.getContext(), builder.getPersistName(), VDbPersistDao.class)
                    .queryValue(key);
        }
        if (!TextUtils.isEmpty(value)) {
            return value;
        }
        if (builder.isPersistByFile()) {
            value = VFileIO.readFileToString(VFile.getFileByDirAndName(builder.getPersistDir(), key));
        }
        return value == null ? "" : value;
    }
}
