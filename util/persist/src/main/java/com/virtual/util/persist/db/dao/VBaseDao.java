package com.virtual.util.persist.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据类型 ：NULL、INTEGER、REAL、TEXT、BLOB
 * INTEGER –整数，对应Java 的byte、short、int 和long。
 * REAL – 小数，对应Java 的float 和 double。
 * TEXT – 字串，对应Java 的String。
 * BLOB - byte[]
 */
public abstract class VBaseDao implements IVDao {

    protected String mTableName;
    protected SQLiteDatabase mWDb;
    protected SQLiteDatabase mRDb;

    public VBaseDao(String tableName) {
        mTableName = tableName;
    }

    @Override
    public String tableName() {
        return mTableName;
    }

    @Override
    public void create(SQLiteDatabase db) {
        db.execSQL(createSql());
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void readableDatabase(SQLiteDatabase db) {
        //Log.d("VBaseDao", "readableDatabase db=" +db);
        mRDb = db;
    }

    @Override
    public void writableDatabase(SQLiteDatabase db) {
        //Log.d("VBaseDao", "writableDatabase db=" +db);
        mWDb = db;
    }

    @Override
    public Cursor query(String[] columns, String selection,
                        String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit) {
        return mRDb.query(tableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    @Override
    public long insert(String nullColumnHack, ContentValues values) {
        return mWDb.insertWithOnConflict(tableName(), nullColumnHack, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public int update(ContentValues values, String whereClause, String[] whereArgs) {
        return mWDb.update(tableName(), values, whereClause, whereArgs);
    }

    @Override
    public int delete(String whereClause, String[] whereArgs) {
        return mWDb.delete(tableName(), whereClause, whereArgs);
    }

    protected abstract String createSql();

}
