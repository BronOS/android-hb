package com.bronos.hb.ds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.bronos.hb.helper.HBSQLiteHelper;
import com.bronos.hb.model.Account;
import com.bronos.hb.model.Category;
import com.bronos.hb.model.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrdersDataSource {
    // Database fields
    private SQLiteDatabase database;
    private HBSQLiteHelper dbHelper;
    private String[] allColumns = {
            HBSQLiteHelper.TABLE_ID,
            "user_id",
            "category_id",
            "category_title",
            "account_id",
            "type",
            "created_at",
            "updated_at",
            "order_sum",
            "description"
    };
    final static public int TYPE_INCOME = 1;
    final static public int TYPE_OUTGO = 0;

    public OrdersDataSource(Context context) {
        dbHelper = new HBSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Order create(long categoryId, String categoryTitle, long accountId, int type, double sum, String description) {
        long utc = Calendar.getInstance().getTimeInMillis();
        ContentValues values = new ContentValues();
        values.put("category_id", categoryId);
        values.put("category_title", categoryTitle);
        values.put("account_id", accountId);
        values.put("type", type);
        values.put("created_at", utc);
        values.put("updated_at", utc);
        values.put("order_sum", sum);
        values.put("description", description);
        long id = database.insert(HBSQLiteHelper.TABLE_ORDERS, null, values);
        return get(id);
    }

    public Order update(Order order) {
        long utc = Calendar.getInstance().getTimeInMillis();
        ContentValues values = new ContentValues();
        values.put("category_id", order.getCategoryId());
        values.put("category_title", order.getCategoryTitle());
        values.put("account_id", order.getAccountId());
        values.put("updated_at", utc);
        values.put("order_sum", order.getOrderSum());
        values.put("description", order.getDescription());
        long id = database.update(HBSQLiteHelper.TABLE_ORDERS, values, HBSQLiteHelper.TABLE_ID + " = " + order.getId(), null);
        return get(id);
    }

    public Order get(long id) {
        Cursor cursor = database.query(HBSQLiteHelper.TABLE_ORDERS, allColumns, HBSQLiteHelper.TABLE_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Order newModel = cursorToModel(cursor);
        cursor.close();
        return newModel;
    }

    public void delete(Order order) {
        long id = order.getId();
        database.delete(HBSQLiteHelper.TABLE_ORDERS, HBSQLiteHelper.TABLE_ID + " = " + id, null);
    }

    public int getCount(String filter) {
        if (filter != null) {
            filter = " WHERE " + filter;
        } else {
            filter = "";
        }

        Cursor mCount= database.rawQuery("select count(*) from " + HBSQLiteHelper.TABLE_ORDERS + filter, null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();

        return count;
    }

    public List<Order> getAll() {
        return getAll(null);
    }

    public List<Order> getAll(String filter) {
        return getAll(filter, null);
    }

    public List<Order> getAll(String filter, String limit) {
        List<Order> list = new ArrayList<Order>();

        Cursor cursor = database.query(HBSQLiteHelper.TABLE_ORDERS, allColumns, filter, null, null, null, "created_at DESC", limit);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Order order = cursorToModel(cursor);
            list.add(order);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return list;
    }

    private Order cursorToModel(Cursor cursor) {
        Order order = new Order();
        order.setId(cursor.getLong(0));
        order.setUserId(cursor.getLong(1));
        order.setCategoryId(cursor.getLong(2));
        order.setCategoryTitle(cursor.getString(3));
        order.setAccountId(cursor.getLong(4));
        order.setType(cursor.getInt(5));
        order.setCreatedAt(cursor.getLong(6));
        order.setUpdatedAt(cursor.getLong(7));
        order.setOrderSum(cursor.getDouble(8));
        order.setDescription(cursor.getString(9));
        return order;
    }}
