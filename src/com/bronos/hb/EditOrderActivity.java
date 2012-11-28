package com.bronos.hb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.bronos.hb.ds.AccountsDataSource;
import com.bronos.hb.ds.CategoriesDataSource;
import com.bronos.hb.ds.OrdersDataSource;
import com.bronos.hb.model.Account;
import com.bronos.hb.model.Category;

public class EditOrderActivity extends Activity {
    /**
     * Data source.
     */
    private OrdersDataSource datasource;
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

    /**
     * Create new activity.
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_order);

        datasource  = new OrdersDataSource(this);
        datasource.open();

        // TODO: check if everything selected before save.

        Intent intent = getIntent();
        setType();
        setAccount();
        setCategory();
    }

    private void setSelecteds(Intent intent) {
        selectedTypeId = intent.getIntExtra("type", 0);
        long accountId = intent.getIntExtra("account", 0);
        // Possible that any account doen't created yet.
        if (accountId > 0) {
            AccountsDataSource accountsDataSource = new AccountsDataSource(this);
            accountsDataSource.open();
            selectedAccount = accountsDataSource.getAccount(accountId);
            accountsDataSource.close();

        } else {
            selectedAccount = new Account();
            selectedAccount.setId(0);
            selectedAccount.setTitle("");
        }
        long categoryId = intent.getIntExtra("category", 0);
        // Possible that any category doen't created yet.
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

    /**
     * Sets type.
     */
    private void setType() {
        TextView type = (TextView) findViewById(R.id.type);
        String text = getString(R.string.type) + ":\n";
        if (selectedTypeId == OrdersDataSource.TYPE_INCOME) {
            text += "\n" + getString(R.string.type_income);
        } else if (selectedTypeId == OrdersDataSource.TYPE_OUTGO) {
            text += "\n" + getString(R.string.type_outgo);
        }
        type.setText(text);
    }

    /**
     * Sets account.
     */
    private void setAccount() {
        TextView account = (TextView) findViewById(R.id.account);
        if (selectedAccount.getId() > 0) {
            account.setText(getString(R.string.accounts) + ":\n" + selectedAccount.toString());
        }
    }

    /**
     * Sets category.
     */
    private void setCategory() {
        TextView category = (TextView) findViewById(R.id.category);
        category.setText(getString(R.string.categories) + ":\n" + selectedCategory.toString());
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
}