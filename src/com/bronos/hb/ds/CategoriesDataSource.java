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
    private String[] allColumns = {HBSQLiteHelper.TABLE_ID, "parent_id", "level", "title"};

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
        long level = 0;
        if (parent_id > 0) {
            Category parentCategory = get(parent_id);
            level = parentCategory.getLevel() + 1;
        }

        ContentValues values = new ContentValues();
        values.put("parent_id", parent_id);
        values.put("level", level);
        values.put("title", title);
        long id = database.insert(HBSQLiteHelper.TABLE_CATEGORIES, null, values);
        return get(id);
    }

    public Category update(Category category) {
        ContentValues values = new ContentValues();
        values.put("parent_id", category.getParentId());
        values.put("level", category.getLevel());
        values.put("title", category.getTitle());
        long id = database.update(HBSQLiteHelper.TABLE_CATEGORIES, values, HBSQLiteHelper.TABLE_ID + " = " + category.getId(), null);
        return get(id);
    }

    public Category get(long id) {
        Cursor cursor = database.query(HBSQLiteHelper.TABLE_CATEGORIES, allColumns, HBSQLiteHelper.TABLE_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Category newModel = cursorToModel(cursor);
        cursor.close();
        return newModel;
    }

    public List<Category> getChildren(long parentId) {
        List<Category> list = new ArrayList<Category>();

        Cursor cursor = database.query(HBSQLiteHelper.TABLE_CATEGORIES, allColumns, "parent_id = " + parentId, null, null, null, null);

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

    public void delete(Category category) {
        long id = category.getId();

        for (Category child: getChildren(id)) {
            delete(child);
        }

        // TODO: remove orders.

        database.delete(HBSQLiteHelper.TABLE_CATEGORIES, HBSQLiteHelper.TABLE_ID + " = " + id, null);
    }

    public List<Category> getAll() {
        List<Category> list = new ArrayList<Category>();

        Cursor cursor = database.query(HBSQLiteHelper.TABLE_CATEGORIES, allColumns, null, null, null, null, null);

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

    public List<Category> getAllSorted() {
        return sortListByParent(0, getAll());
    }

    private List<Category> sortListByParent(long parentId, List<Category> list) {
        List<Category> retList = new ArrayList<Category>();

        for (Category category: list) {
            if (category.getParentId() == parentId) {
                retList.add(category);
                retList.addAll(sortListByParent(category.getId(), list));
            }
        }

        return retList;
    }

    private Category cursorToModel(Cursor cursor) {
        Category category = new Category();
        category.setId(cursor.getLong(0));
        category.setParentId(cursor.getLong(1));
        category.setLevel(cursor.getLong(2));
        category.setTitle(cursor.getString(3));
        return category;
    }}
