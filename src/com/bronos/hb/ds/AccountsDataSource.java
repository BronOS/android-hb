package com.bronos.hb.ds;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.bronos.hb.helper.HBSQLiteHelper;
import com.bronos.hb.model.Account;

public class AccountsDataSource {
    // Database fields
    private SQLiteDatabase database;
    private HBSQLiteHelper dbHelper;
    private String[] allColumns = {HBSQLiteHelper.TABLE_ID, "title", "amount"};

    public AccountsDataSource(Context context) {
        dbHelper = new HBSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Account createAccount(String title, Float amount) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("amount", amount);
        long id = database.insert(HBSQLiteHelper.TABLE_ACCOUNTS, null, values);
        return getAccount(id);
    }

    public Account updateAccount(Account account) {
        ContentValues values = new ContentValues();
        values.put("title", account.getTitle());
        values.put("amount", account.getAmount());
        long id = database.update(HBSQLiteHelper.TABLE_ACCOUNTS, values, HBSQLiteHelper.TABLE_ID + " = " + account.getId(), null);
        return getAccount(id);
    }

    public Account getAccount(long id) {
        Cursor cursor = database.query(HBSQLiteHelper.TABLE_ACCOUNTS, allColumns, HBSQLiteHelper.TABLE_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Account newAccount = cursorToAccount(cursor);
        cursor.close();
        return newAccount;
    }

    public void deleteAccount(Account account) {
        long id = account.getId();
        // TODO: delete all orders in this account.
        database.delete(HBSQLiteHelper.TABLE_ACCOUNTS, HBSQLiteHelper.TABLE_ID + " = " + id, null);
    }

    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<Account>();

        Cursor cursor = database.query(HBSQLiteHelper.TABLE_ACCOUNTS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Account account = cursorToAccount(cursor);
            accounts.add(account);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return accounts;
    }

    private Account cursorToAccount(Cursor cursor) {
        Account account = new Account();
        account.setId(cursor.getLong(0));
        account.setTitle(cursor.getString(1));
        account.setAmount(cursor.getFloat(2));
        return account;
    }}
