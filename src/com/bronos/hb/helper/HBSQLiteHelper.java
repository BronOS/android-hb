package com.bronos.hb.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HBSQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hb.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_ID = "_id";

    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_CATEGORIES = "categories";

    private static final String DATABASE_CREATE_ACCOUNTS =
            "CREATE TABLE " + TABLE_ACCOUNTS + "(" +
            "    " + TABLE_ID + " INTEGER PRIMARY KEY autoincrement," +
            "    title TEXT NOT NULL," +
            "    amount REAL DEFAULT 0" +
            ");";
    private static final String DATABASE_CREATE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES + "(" +
            "    " + TABLE_ID + " INTEGER PRIMARY KEY autoincrement," +
            "    parent_id INTEGER DEFAULT 0," +
            "    level INTEGER DEFAULT 0," +
            "    title TEXT NOT NULL" +
            ");";
    private static final String DATABASE_CREATE_ORDERS =
            "CREATE TABLE " + TABLE_ORDERS + "(" +
            "    " + TABLE_ID + " INTEGER PRIMARY KEY autoincrement," +
            "    user_id INTEGER DEFAULT 0," +
            "    category_id INTEGER DEFAULT 0," +
            "    account_id INTEGER DEFAULT 0," +
            "    type INTEGER DEFAULT 1," +
            "    created_at INTEGER DEFAULT 0," +
            "    updated_at INTEGER DEFAULT 0," +
            "    order_sum REAL DEFAULT 0," +
            "    description TEXT NOT NULL" +
            ");";
    private static final String DATABASE_DELETE_ACCOUNTS =
            "DROP TABLE IF EXISTS " + TABLE_ACCOUNTS + ";";
    private static final String DATABASE_DELETE_CATEGORIES =
            "DROP TABLE IF EXISTS " + TABLE_CATEGORIES + ";";
    private static final String DATABASE_DELETE_ORDERS =
            "DROP TABLE IF EXISTS " + TABLE_ORDERS + ";";

    public HBSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_ACCOUNTS);
        database.execSQL(DATABASE_CREATE_CATEGORIES);
        database.execSQL(DATABASE_CREATE_ORDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_DELETE_ACCOUNTS);
        db.execSQL(DATABASE_DELETE_CATEGORIES);
        db.execSQL(DATABASE_DELETE_ORDERS);
        onCreate(db);
    }
}
