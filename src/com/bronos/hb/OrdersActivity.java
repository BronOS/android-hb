package com.bronos.hb;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;

public class OrdersActivity extends ListActivity {
    final public static long accountId = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders);

    }
}