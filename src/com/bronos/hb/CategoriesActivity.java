package com.bronos.hb;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.bronos.hb.ds.CategoriesDataSource;
import com.bronos.hb.model.Account;
import com.bronos.hb.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends ListActivity {
    private CategoriesDataSource datasource;
    final private static int ACCOUNTS_MENU_EDIT = 1;
    final private static int ACCOUNTS_MENU_REMOVE = 2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);

        datasource = new CategoriesDataSource(this);
        datasource.open();

        showList();

        ListView list = (ListView)getListView();
        registerForContextMenu(list);
    }

    private void showList() {
        List<Category> values = datasource.getAllSorted();
        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    private void addCancelButtonToMenu(AlertDialog.Builder builder)
    {
        builder.setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    private void addItem() {
        final CategoriesActivity context = this;

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(R.string.add_category);

        final List<Category> values = new ArrayList<Category>();
        Category rootCategory = new Category();
        rootCategory.setId(0);
        rootCategory.setParentId(0);
        rootCategory.setLevel(0);
        rootCategory.setTitle(getString(R.string.root_category));
        values.add(rootCategory);
        values.addAll(datasource.getAllSorted());
        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_1, values);

        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int item) {
                AlertDialog.Builder builderTitle = new AlertDialog.Builder(context);
                builderTitle.setCancelable(false);
                builderTitle.setTitle(R.string.add_category);
                final EditText inputView = new EditText(context);
                builderTitle.setView(inputView);
                builderTitle.setPositiveButton(context.getString(R.string.add), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogTitle, int itemTitle) {
                        datasource.create(values.get(item).getId(), inputView.getText().toString());
                        showList();
                        dialog.cancel();
                    }
                });
                addCancelButtonToMenu(builderTitle);
                builderTitle.show();
            }
        });

        addCancelButtonToMenu(builder);

        builder.show();
    }

    private void editItem(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Category category = (Category)getListAdapter().getItem(info.position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.edit_category);

        final EditText input = new EditText(this);
        input.setText(category.getTitle());
        builder.setView(input);

        builder.setPositiveButton(this.getString(R.string.edit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                category.setTitle(input.getText().toString());
                datasource.update(category);
                showList();
            }
        });

        addCancelButtonToMenu(builder);

        builder.show();
    }

    private void removeItem(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Category category = (Category)getListAdapter().getItem(info.position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.remove_category);
        builder.setMessage(R.string.remove_category_description);

        builder.setPositiveButton(this.getString(R.string.remove), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO: remove orders.
                datasource.delete(category);
                showList();
            }
        });

        addCancelButtonToMenu(builder);

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.categories_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                addItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.manage_category));
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
    public void onListItemClick(ListView parent, View view, int position, long id) {
        final Category category = (Category) getListAdapter().getItem(position);
        Intent intent = new Intent(this, OrdersActivity.class);
        intent.putExtra("category", category.getId());
        startActivity(intent);
    }
}