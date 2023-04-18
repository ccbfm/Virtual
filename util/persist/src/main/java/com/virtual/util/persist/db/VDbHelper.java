package com.virtual.util.persist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.virtual.util.persist.db.dao.IVDao;

import java.util.Collection;

public final class VDbHelper extends SQLiteOpenHelper {

    private final Collection<IVDao> mDaoList;

    public VDbHelper(@Nullable Context context,
                     @Nullable String name,
                     @Nullable SQLiteDatabase.CursorFactory factory,
                     int version,
                     Collection<IVDao> daoList) {
        super(context, name, factory, version);
        mDaoList = daoList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (mDaoList != null) {
            for (IVDao dao : mDaoList) {
                dao.create(db);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (mDaoList != null) {
            for (IVDao dao : mDaoList) {
                dao.upgrade(db, oldVersion, newVersion);
            }
        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = super.getReadableDatabase();
        if (mDaoList != null) {
            for (IVDao dao : mDaoList) {
                dao.readableDatabase(db);
            }
        }
        return db;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();
        if (mDaoList != null) {
            for (IVDao dao : mDaoList) {
                dao.writableDatabase(db);
            }
        }
        return db;
    }
}
