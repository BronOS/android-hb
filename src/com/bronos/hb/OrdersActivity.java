package com.bronos.hb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.bronos.hb.adapter.OrdersAdapter;
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

    private int paging_row = 0;
    private int paging_offset = 30;

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
            titleList.add(selectedCategory.toString(false));
        }

        if (selectedTypeId == OrdersDataSource.TYPE_INCOME) {
            titleList.add(getString(R.string.type_income));
        }

        if (selectedTypeId == OrdersDataSource.TYPE_OUTGO) {
            titleList.add(getString(R.string.type_outgo));
        }

        setTitle(TextUtils.join(" > ", titleList));
    }

    /**
     * Shows list.
     *
     * @return void
     */
    private void showList() {
        String filter = getFilter();
        setPagination(filter);
        ArrayList<Order> values = datasource.getAll(filter, paging_row + "," + paging_offset);
        OrdersAdapter adapter = new OrdersAdapter(this, android.R.layout.simple_list_item_1, values);
        setTitleWithFilters();
        setListAdapter(adapter);
    }

    private void setPagination(String filter) {
        Button firstBtn = (Button)findViewById(R.id.first);
        Button prevBtn = (Button)findViewById(R.id.prev);
        Button nextBtn = (Button)findViewById(R.id.next);
        Button lastBtn = (Button)findViewById(R.id.last);

        if (paging_row > 0) {
            firstBtn.setEnabled(true);
            setPagingButton(firstBtn, 0);
            prevBtn.setEnabled(true);
            int row = paging_row - paging_offset;
            setPagingButton(prevBtn, row < 0 ? 0 : row);
        } else {
            firstBtn.setEnabled(false);
            prevBtn.setEnabled(false);
        }

        int count = datasource.getCount(filter);
        if (count > (paging_row + paging_offset)) {
            nextBtn.setEnabled(true);
            setPagingButton(nextBtn, paging_row + paging_offset);
            lastBtn.setEnabled(true);
            setPagingButton(lastBtn, count / paging_offset * paging_offset);
        } else {
            nextBtn.setEnabled(false);
            lastBtn.setEnabled(false);
        }
//
//        RelativeLayout rl = (RelativeLayout)findViewById(R.id.pagination);
//        if (!firstBtn.isEnabled() && !prevBtn.isEnabled() && !nextBtn.isEnabled() && !lastBtn.isEnabled()) {
//            rl.setVisibility(RelativeLayout.INVISIBLE);
//        } else {
//            rl.setVisibility(RelativeLayout.VISIBLE);
//        }
    }

    private void setPagingButton(Button button, final int row) {
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                paging_row = row;
                showList();
            }
        });
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

    /**
     * Open activity "EditOrderActivity" for editing order.
     */
    private void editItem(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Order order = (Order) getListAdapter().getItem(info.position);
        editItem(order);
    }

    private void editItem(Order order) {
        Intent intent = new Intent(this, EditOrderActivity.class);
        intent.putExtra("order", order.getId());
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    /**
     * Adds cancel button to the dialog.
     *
     * @param builder
     */
    private void addCancelButtonToMenu(AlertDialog.Builder builder) {
        builder.setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    /**
     * Remove order.
     */
    private void removeItem(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Order order = (Order) getListAdapter().getItem(info.position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.remove_order);
        builder.setMessage(R.string.remove_order_description);

        builder.setPositiveButton(this.getString(R.string.remove), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AccountsDataSource accountsDataSource = new AccountsDataSource(getApplicationContext());
                accountsDataSource.open();
                double sum = order.getOrderSum();
                Account account = accountsDataSource.getAccount(order.getAccountId());
                if (order.getType() == OrdersDataSource.TYPE_INCOME) {
                    sum *= -1;
                }
                account.setAmount(new Float(account.getAmount() + sum));
                accountsDataSource.updateAccount(account);
                if (selectedAccount.getId() == account.getId()) {
                    selectedAccount = account;
                }
                accountsDataSource.close();

                datasource.delete(order);
                showList();
            }
        });

        addCancelButtonToMenu(builder);

        builder.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.manage_account));
        menu.add(0, ACCOUNTS_MENU_EDIT, ACCOUNTS_MENU_EDIT, R.string.edit);
        menu.add(0, ACCOUNTS_MENU_REMOVE, ACCOUNTS_MENU_REMOVE, R.string.remove);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ACCOUNTS_MENU_EDIT:
                editItem(item);
                return true;
            case ACCOUNTS_MENU_REMOVE:
                removeItem(item);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
            if (data != null) {
                setFilters(data);
            }
        } else if (selectedAccount.getId() > 0) {
            AccountsDataSource accountsDataSource = new AccountsDataSource(this);
            accountsDataSource.open();
            selectedAccount = accountsDataSource.getAccount(selectedAccount.getId());
            accountsDataSource.close();
        }

        showList();
    }

    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
        final Order order = (Order) getListAdapter().getItem(position);
        editItem(order);
    }
}