package com.bronos.hb;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.bronos.hb.ds.AccountsDataSource;
import com.bronos.hb.ds.CategoriesDataSource;
import com.bronos.hb.ds.OrdersDataSource;
import com.bronos.hb.helper.HBSQLiteHelper;
import com.bronos.hb.model.Account;
import com.bronos.hb.model.Category;
import com.bronos.hb.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Orders activity class.
 */
public class OrdersActivity extends ListActivity {
    /**
     * Data source.
     */
    private OrdersDataSource datasource;
    /**
     * Context menu edit item id.
     */
    final private static int ACCOUNTS_MENU_EDIT = 1;
    /**
     * Context menu remove item id.
     */
    final private static int ACCOUNTS_MENU_REMOVE = 2;
    /**
     * Selected type id.
     */
    private int selectedTypeId;
    /**
     * Selected account.
     */
    private Account selectedAccount;
    /**
     * Selected category.
     */
    private Category selectedCategory;

    final static private int REQUEST_CODE_EDIT = 1;
    final static private int REQUEST_CODE_ADD = 2;
    final static private int REQUEST_CODE_FILTER = 3;

    /**
     * Creates new activity.
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders);

        datasource = new OrdersDataSource(this);
        datasource.open();

        setFilters(getIntent());

        showList();

        ListView list = getListView();
        registerForContextMenu(list);

        // TODO: on delete/create/update change account amount.
    }

    private void setFilters(Intent intent) {
        selectedTypeId = intent.getIntExtra("type", -1);

        long accountId = intent.getLongExtra("account", 0);
        if (accountId > 0) {
            AccountsDataSource accountsDataSource = new AccountsDataSource(this);
            accountsDataSource.open();
            selectedAccount = accountsDataSource.getAccount(accountId);
            accountsDataSource.close();
        } else {
            selectedAccount = new Account();
            selectedAccount.setId(0);
            selectedAccount.setTitle("");
            selectedAccount.setAmount(new Float(0));
        }

        long categoryId = intent.getLongExtra("category", 0);
        if (categoryId > 0) {
            CategoriesDataSource categoriesDataSource = new CategoriesDataSource(this);
            categoriesDataSource.open();
            selectedCategory = categoriesDataSource.get(categoryId);
            categoriesDataSource.close();
        } else {
            selectedCategory = new Category();
            selectedCategory.setTitle(getString(R.string.root_category));
            selectedCategory.setLevel(0);
            selectedCategory.setParentId(0);
            selectedCategory.setId(0);
        }
    }

    private void setTitleWithFilters() {
        ArrayList<String> titleList = new ArrayList<String>();

        titleList.add(getString(R.string.orders));

        if (selectedAccount.getId() > 0) {
            titleList.add(selectedAccount.toString());
        }

        if (selectedCategory.getId() > 0) {
            titleList.add(selectedCategory.toString());
        }

        if (selectedTypeId == OrdersDataSource.TYPE_INCOME) {
            titleList.add(getString(R.string.type_income));
        }

        if (selectedTypeId == OrdersDataSource.TYPE_OUTGO) {
            titleList.add(getString(R.string.type_outgo));
        }

        setTitle(TextUtils.join("|", titleList));
    }

    /**
     * Shows list.
     *
     * @return void
     */
    private void showList() {
        List<Order> values = datasource.getAll(getFilter());
        ArrayAdapter<Order> adapter = new ArrayAdapter<Order>(this, android.R.layout.simple_list_item_1, values);
        setTitleWithFilters();
        setListAdapter(adapter);
    }

    private String getFilter() {
        ArrayList<String> filter = new ArrayList<String>();

        if (selectedTypeId > -1) {
            filter.add("type = " + selectedTypeId);
        }

        if (selectedAccount.getId() > 0) {
            filter.add("account_id = " + selectedAccount.getId());
        }

        if (selectedCategory.getId() > 0) {
            filter.add("category_id = " + selectedCategory.getId());
        }

        // TODO: implement search.

        return filter.size() > 0 ? TextUtils.join(" AND ", filter) : null;
    }

    /**
     * Show filters activity.
     */
    private void showFilters() {
        Intent intent = new Intent(this, FiltersOrdersActivity.class);
        if (selectedTypeId > -1) {
            intent.putExtra("type", selectedTypeId);
        }
        intent.putExtra("category", selectedCategory.getId());
        intent.putExtra("account", selectedAccount.getId());

        startActivityForResult(intent, REQUEST_CODE_FILTER);
    }

    /**
     * Open activity "EditOrderActivity" for creating new order.
     */
    private void addItem() {
        Intent intent = new Intent(this, EditOrderActivity.class);
        if (selectedTypeId > -1) {
            intent.putExtra("type", selectedTypeId);
        }
        intent.putExtra("category", selectedCategory.getId());
        intent.putExtra("account", selectedAccount.getId());

        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.orders_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                addItem();
                return true;
            case R.id.filter:
                showFilters();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        datasource.open();

        if (requestCode == REQUEST_CODE_FILTER) {
            setFilters(data);
        }

        showList();
    }
}