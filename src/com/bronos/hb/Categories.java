package com.bronos.hb;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.bronos.hb.ds.CategoriesDataSource;
import com.bronos.hb.model.Category;

import java.util.List;

public class Categories extends ListActivity {
    private CategoriesDataSource datasource;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);

        datasource = new CategoriesDataSource(this);
        datasource.open();

        showList();
    }

    private void showList()
    {
        List<Category> values = datasource.getAllSorted();
        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        ListView list = (ListView)getListView();
        registerForContextMenu(list);
    }
}