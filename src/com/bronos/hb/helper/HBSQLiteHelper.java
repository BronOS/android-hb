package com.bronos.hb.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HBSQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hb.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = 
            "CREATE TABLE accounts(" +
            "    _id INTEGER PRIMARY KEY autoincrement," +
            "    title TEXT NOT NULL," +
            "    amount REAL DEFAULT 0" +
            ");" +
            "CREATE TABLE categories(" +
            "    _id INTEGER PRIMARY KEY autoincrement," +
            "    parent_id INTEGER DEFAULT 0," +
            "    title TEXT NOT NULL" +
            ");" +
            "CREATE TABLE orders(" +
            "    _id INTEGER PRIMARY KEY autoincrement," +
            "    user_id INTEGER DEFAULT 0," +
            "    category_id INTEGER DEFAULT 0," +
            "    account_id INTEGER DEFAULT 0," +
            "    type INTEGER DEFAULT 1," +
            "    created_at INTEGER DEFAULT 0," +
            "    updated_at INTEGER DEFAULT 0," +
            "    order_sum REAL DEFAULT 0," +
            "    description TEXT NOT NULL" +
            ");";
    private static final String DATABASE_DELETE =
            "DROP TABLE IF EXISTS accounts;" +
            "DROP TABLE IF EXISTS categories;" +
            "DROP TABLE IF EXISTS orders;";
    
    public HBSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_DELETE);
        onCreate(db);
    }
}
