package com.bronos.hb;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.bronos.hb.ds.CategoriesDataSource;
import com.bronos.hb.ds.OrdersDataSource;
import com.bronos.hb.model.Category;
import com.bronos.hb.model.Order;

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
     * Creates new activity.
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders);

        datasource = new OrdersDataSource(this);
        datasource.open();

        showList();

        ListView list = getListView();
        registerForContextMenu(list);

        // TODO: on delete/create/update change account amount.
        // TODO: implement search.
        // TODO: implement filter as preferences and received parameters.
    }

    /**
     * Shows list.
     *
     * @return void
     */
    private void showList() {
        List<Order> values = datasource.getAll();
        ArrayAdapter<Order> adapter = new ArrayAdapter<Order>(this, android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    /**
     * Open activity "EditOrderActivity" for creating new order.
     */
    private void addItem() {

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}