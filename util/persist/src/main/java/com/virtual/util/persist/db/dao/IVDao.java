package com.virtual.util.persist.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public interface IVDao {

    String tableName();

    void create(SQLiteDatabase db);

    void upgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    void readableDatabase(SQLiteDatabase db);

    void writableDatabase(SQLiteDatabase db);

    Cursor query(String[] columns, String selection,
                 String[] selectionArgs, String groupBy, String having,
                 String orderBy, String limit);

    long insert(String nullColumnHack, ContentValues values);

    int update(ContentValues values, String whereClause, String[] whereArgs);

    int delete(String whereClause, String[] whereArgs);
}
