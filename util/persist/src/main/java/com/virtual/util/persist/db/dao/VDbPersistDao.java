package com.virtual.util.persist.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.virtual.util.persist.db.model.VDbPersist;

public class VDbPersistDao extends VBaseDao {

    public VDbPersistDao(String tableName) {
        super(tableName);
    }

    @Override
    protected String createSql() {
        return "create table if not exists " + tableName() + "(key text primary key, " +
                "value text, disable integer, update_time integer)";
    }


    public String queryValue(String key) {
        try (Cursor cursor = query(new String[]{"value"}, "key = ?", new String[]{key},
                null, null, null, null);) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Throwable throwable) {
            Log.e("VDbPersistDao", "queryValue Throwable: ", throwable);
        }
        return null;
    }

    public VDbPersist query(String key) {
        try (Cursor cursor = query(new String[]{"key", "value", "disable", "update_time"},
                "key = ?", new String[]{key},
                null, null, null, null);) {
            if (cursor != null && cursor.moveToFirst()) {
                VDbPersist persist = new VDbPersist();
                persist.key = cursor.getString(0);
                persist.value = cursor.getString(1);
                persist.disable = cursor.getInt(2);
                persist.update_time = cursor.getLong(3);
                return persist;
            }
        } catch (Throwable throwable) {
            Log.e("VDbPersistDao", "query Throwable: ", throwable);
        }
        return null;
    }

    public long insertValue(String key, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("key", key);
        contentValues.put("value", value);
        contentValues.put("update_time", System.currentTimeMillis());
        return insert(null, contentValues);
    }

    public long insertValue(String key, String value, int disable) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("key", key);
        contentValues.put("value", value);
        contentValues.put("disable", disable);
        contentValues.put("update_time", System.currentTimeMillis());
        return insert(null, contentValues);
    }

    public int updateModel(VDbPersist model) {
        ContentValues values = new ContentValues();
        values.put("key", model.key);
        values.put("value", model.value);
        values.put("disable", model.disable);
        values.put("update_time", System.currentTimeMillis());
        return this.update(values, "key=?", new String[]{model.key});
    }

    public int deleteModel(String key) {
        return this.delete("key=?", new String[]{key});
    }
}
