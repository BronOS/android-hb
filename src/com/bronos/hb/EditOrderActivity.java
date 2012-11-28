package com.bronos.hb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.bronos.hb.ds.AccountsDataSource;
import com.bronos.hb.ds.CategoriesDataSource;
import com.bronos.hb.ds.OrdersDataSource;
import com.bronos.hb.model.Account;
import com.bronos.hb.model.Category;
import com.bronos.hb.model.Type;

import java.util.ArrayList;
import java.util.List;

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

        Intent intent = getIntent();
        setSelecteds(intent);
        setTypeView();
        setAccountView();
        setCategoryView();

        final EditOrderActivity context = this;
        Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View view) {
                TextView sumView = (TextView) findViewById(R.id.sum);
                TextView descriptionView = (TextView) findViewById(R.id.description);
                double sum = Double.parseDouble(sumView.getText().toString().length() == 0 ? "0" : sumView.getText().toString());
                if (selectedAccount.getId() == 0) {
                    showToast(getString(R.string.edit_order_choose_account_message));
                } else if (selectedCategory.getId() == 0) {
                    showToast(getString(R.string.edit_order_choose_category_message));
                } else if (sum == 0) {
                    showToast(getString(R.string.edit_order_choose_sum_message));
                } else {
                    datasource.create(
                            selectedCategory.getId(),
                            selectedCategory.toString(),
                            selectedAccount.getId(),
                            selectedTypeId,
                            sum,
                            descriptionView.getText().toString()
                    );

                    AccountsDataSource accountsDataSource = new AccountsDataSource(context);
                    accountsDataSource.open();
                    if (selectedTypeId == OrdersDataSource.TYPE_OUTGO) {
                        sum *= -1;
                    }
                    selectedAccount.setAmount(new Float(selectedAccount.getAmount() + sum));
                    accountsDataSource.updateAccount(selectedAccount);
                    accountsDataSource.close();

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void setSelecteds(Intent intent) {
        selectedTypeId = intent.getIntExtra("type", 0);

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

    /**
     * Adds cancel button to the dialog.
     *
     * @param builder Dialog builder.
     */
    private void addCancelButtonToMenu(AlertDialog.Builder builder)
    {
        builder.setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    /**
     * Returns types adapter.
     *
     * @return ArrayAdapter<Type>
     */
    private ArrayAdapter<Type> getTypeAdapter() {
        final List<Type> values = new ArrayList<Type>();
        Type incomeType = new Type(OrdersDataSource.TYPE_INCOME, getString(R.string.type_income));
        Type outgoType = new Type(OrdersDataSource.TYPE_OUTGO, getString(R.string.type_outgo));
        values.add(outgoType);
        values.add(incomeType);
        return new ArrayAdapter<Type>(this, android.R.layout.simple_list_item_1, values);
    }

    /**
     * Returns accounts adapter.
     *
     * @return ArrayAdapter<Type>
     */
    private ArrayAdapter<Account> getAccountAdapter() {
        final List<Account> values = new ArrayList<Account>();

        AccountsDataSource accountsDataSource = new AccountsDataSource(this);
        accountsDataSource.open();
        values.addAll(accountsDataSource.getAllAccounts());
        accountsDataSource.close();

        return new ArrayAdapter<Account>(this, android.R.layout.simple_list_item_1, values);
    }

    /**
     * Returns categories adapter.
     *
     * @return ArrayAdapter<Category>
     */
    private ArrayAdapter<Category> getCategoryAdapter() {
        final List<Category> values = new ArrayList<Category>();

        CategoriesDataSource categoriesDataSource = new CategoriesDataSource(this);
        categoriesDataSource.open();
        values.addAll(categoriesDataSource.getAllSorted());
        categoriesDataSource.close();

        return new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_1, values);
    }

    /**
     * Sets type.
     */
    private void setTypeView() {
        final EditOrderActivity context = this;

        TextView type = (TextView) findViewById(R.id.type);

        if (selectedTypeId == OrdersDataSource.TYPE_INCOME) {
            type.setText(R.string.type_income);
        } else if (selectedTypeId == OrdersDataSource.TYPE_OUTGO) {
            type.setText(R.string.type_outgo);
        }

        type.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builderTypes = new AlertDialog.Builder(context);
                builderTypes.setCancelable(false);
                builderTypes.setTitle(R.string.edit_order_choose_type);

                final ArrayAdapter<Type> typeArrayAdapter = getTypeAdapter();
                builderTypes.setSingleChoiceItems(typeArrayAdapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int item) {
                        selectedTypeId = typeArrayAdapter.getItem(item).getId();
                        setTypeView();
                        dialog.cancel();
                    }
                });
                addCancelButtonToMenu(builderTypes);
                builderTypes.show();
            }
        });
    }

    /**
     * Sets account.
     */
    private void setAccountView() {
        final EditOrderActivity context = this;

        TextView account = (TextView) findViewById(R.id.account);

        if (selectedAccount.getId() > 0) {
            account.setText(selectedAccount.toString());
        } else {
            account.setText("-");
        }

        account.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builderAccounts = new AlertDialog.Builder(context);
                builderAccounts.setCancelable(false);
                builderAccounts.setTitle(R.string.edit_order_choose_account);

                final ArrayAdapter<Account> accountArrayAdapter = getAccountAdapter();
                builderAccounts.setSingleChoiceItems(accountArrayAdapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int item) {
                        selectedAccount = accountArrayAdapter.getItem(item);
                        setAccountView();
                        dialog.cancel();
                    }
                });
                addCancelButtonToMenu(builderAccounts);
                builderAccounts.show();
            }
        });
    }

    /**
     * Sets category.
     */
    private void setCategoryView() {
        final EditOrderActivity context = this;

        TextView categoryView = (TextView) findViewById(R.id.category);

        categoryView.setText(selectedCategory.toString());

        categoryView.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builderCategories = new AlertDialog.Builder(context);
                builderCategories.setCancelable(false);
                builderCategories.setTitle(R.string.edit_order_choose_category);

                final ArrayAdapter<Category> categoryArrayAdapter = getCategoryAdapter();
                builderCategories.setSingleChoiceItems(categoryArrayAdapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int item) {
                        selectedCategory = categoryArrayAdapter.getItem(item);
                        setCategoryView();
                        dialog.cancel();
                    }
                });
                addCancelButtonToMenu(builderCategories);
                builderCategories.show();
            }
        });
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