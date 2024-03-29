package com.bronos.hb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.bronos.hb.adapter.AccountsAdapter;
import com.bronos.hb.adapter.CategoriesAdapter;
import com.bronos.hb.adapter.TypeAdapter;
import com.bronos.hb.ds.AccountsDataSource;
import com.bronos.hb.ds.CategoriesDataSource;
import com.bronos.hb.ds.OrdersDataSource;
import com.bronos.hb.model.Account;
import com.bronos.hb.model.Category;
import com.bronos.hb.model.Type;

import java.util.ArrayList;
import java.util.List;

public class FiltersOrdersActivity extends Activity {
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
     * Creates activity.
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters_orders);

        Intent intent = getIntent();
        setSelecteds(intent);

        setTypeView();
        setCategoryView();
        setAccountView();

        final FiltersOrdersActivity context = this;
        Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("type", selectedTypeId);
                intent.putExtra("category", selectedCategory.getId());
                intent.putExtra("account", selectedAccount.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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
        final ArrayList<Type> values = new ArrayList<Type>();
        Type noneType = new Type(-1, "-");
        Type incomeType = new Type(OrdersDataSource.TYPE_INCOME, getString(R.string.type_income));
        Type outgoType = new Type(OrdersDataSource.TYPE_OUTGO, getString(R.string.type_outgo));
        values.add(noneType);
        values.add(outgoType);
        values.add(incomeType);
        return new TypeAdapter(this, android.R.layout.simple_list_item_1, values);
    }

    /**
     * Returns accounts adapter.
     *
     * @return ArrayAdapter<Type>
     */
    private ArrayAdapter<Account> getAccountAdapter() {
        final ArrayList<Account> values = new ArrayList<Account>();
        Account noneAccount = new Account();
        noneAccount.setId(0);
        noneAccount.setTitle("-");
        noneAccount.setAmount(new Float(0));
        values.add(noneAccount);

        AccountsDataSource accountsDataSource = new AccountsDataSource(this);
        accountsDataSource.open();
        values.addAll(accountsDataSource.getAllAccounts());
        accountsDataSource.close();

        return new AccountsAdapter(this, android.R.layout.simple_list_item_1, values);
    }

    /**
     * Sets account.
     */
    private void setAccountView() {
        final FiltersOrdersActivity context = this;

        TextView account = (TextView) findViewById(R.id.account);
        TextView accountSum = (TextView) findViewById(R.id.account_sum);
        RelativeLayout accountRow = (RelativeLayout) findViewById(R.id.account_row);

        if (selectedAccount.getId() > 0) {
            account.setText(selectedAccount.getTitle());
            accountSum.setText("" + selectedAccount.getAmount());
        } else {
            account.setText("-");
            accountSum.setText("");
        }

        accountRow.setOnClickListener(new TextView.OnClickListener() {
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
     * Returns categories adapter.
     *
     * @return ArrayAdapter<Category>
     */
    private ArrayAdapter<Category> getCategoryAdapter() {
        final ArrayList<Category> values = new ArrayList<Category>();

        Category noneCategory = new Category();
        noneCategory.setId(0);
        noneCategory.setParentId(0);
        noneCategory.setTitle("-");
        values.add(noneCategory);

        CategoriesDataSource categoriesDataSource = new CategoriesDataSource(this);
        categoriesDataSource.open();
        values.addAll(categoriesDataSource.getAllSorted());
        categoriesDataSource.close();

        return new CategoriesAdapter(this, android.R.layout.simple_list_item_1, values);
    }

    /**
     * Sets category.
     */
    private void setCategoryView() {
        final FiltersOrdersActivity context = this;

        TextView categoryView = (TextView) findViewById(R.id.category);
        RelativeLayout categoryRow = (RelativeLayout) findViewById(R.id.category_row);

        categoryView.setText(selectedCategory.getTitle());

        categoryRow.setOnClickListener(new TextView.OnClickListener() {
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

    /**
     * Sets type.
     */
    private void setTypeView() {
        final FiltersOrdersActivity context = this;

        TextView typeView = (TextView) findViewById(R.id.type);
        ImageView typeIcon = (ImageView) findViewById(R.id.type_icon);
        RelativeLayout typeRow = (RelativeLayout) findViewById(R.id.type_row);
        int iconResource = R.drawable.minus;

        if (selectedTypeId == OrdersDataSource.TYPE_INCOME) {
            typeView.setText(getString(R.string.type_income));
            iconResource = R.drawable.plus;
        } else if (selectedTypeId == OrdersDataSource.TYPE_OUTGO) {
            typeView.setText(getString(R.string.type_outgo));
        } else {
            typeView.setText("-");
            iconResource = R.drawable.type;
        }

        typeIcon.setImageResource(iconResource);

        typeRow.setOnClickListener(new TextView.OnClickListener() {
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
     * Sets selected filters.
     *
     * @param intent
     */
    private void setSelecteds(Intent intent) {
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
            selectedAccount.setTitle("-");
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
            selectedCategory.setTitle("-");
            selectedCategory.setLevel(0);
            selectedCategory.setParentId(0);
            selectedCategory.setId(0);
        }
    }
}