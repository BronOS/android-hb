package com.bronos.hb.ds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TableLayout;
import com.bronos.hb.helper.HBSQLiteHelper;
import com.bronos.hb.model.Account;
import com.bronos.hb.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesDataSource {
    // Database fields
    private SQLiteDatabase database;
    private HBSQLiteHelper dbHelper;
    private String[] allColumns = {"_id", "parent_id", "title"};
    final public static String TABLE_NAME = "categories";

    public CategoriesDataSource(Context context) {
        dbHelper = new HBSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Category create(long parent_id, String title) {
        ContentValues values = new ContentValues();
        values.put("parent_id", parent_id);
        values.put("title", title);
        long id = database.insert(TABLE_NAME, null, values);
        return get(id);
    }

    public Category update(Category category) {
        ContentValues values = new ContentValues();
        values.put("parent_id", category.getParentId());
        values.put("title", category.getTitle());
        long id = database.update(TABLE_NAME, values, "_id = " + category.getId(), null);
        return get(id);
    }

    public Category get(long id) {
        Cursor cursor = database.query(TABLE_NAME, allColumns, "_id = " + id, null, null, null, null);
        cursor.moveToFirst();
        Category newModel = cursorToModel(cursor);
        cursor.close();
        return newModel;
    }

    public void delete(Category category) {
        long id = category.getId();
        database.delete(TABLE_NAME, "_id = " + id, null);
    }

    public List<Category> getAll() {
        List<Category> list = new ArrayList<Category>();

        Cursor cursor = database.query(TABLE_NAME, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category category = cursorToModel(cursor);
            list.add(category);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return list;
    }

    private Category cursorToModel(Cursor cursor) {
        Category category = new Category();
        category.setId(cursor.getLong(0));
        category.setParentId(cursor.getLong(1));
        category.setTitle(cursor.getString(2));
        return category;
    }}
