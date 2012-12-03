package com.bronos.hb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bronos.hb.R;
import com.bronos.hb.ds.OrdersDataSource;
import com.bronos.hb.model.Category;
import com.bronos.hb.model.Order;

import java.util.ArrayList;

public class OrdersAdapter extends ArrayAdapter {

    private ArrayList<Order> items;

    public OrdersAdapter(Context context, int textViewResourceId, ArrayList<Order> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.order_row, null);
        }
        Order order = items.get(position);
        if (order != null) {
            TextView ts = (TextView) v.findViewById(R.id.sumtext);
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView tb = (TextView) v.findViewById(R.id.bottomtext);
            ImageView iv = (ImageView) v.findViewById(R.id.icon);
            if (ts != null) {
                ts.setText((order.getType() == OrdersDataSource.TYPE_INCOME ? "+" : "-") + order.getOrderSum());
            }
            if (tt != null) {
                tt.setText(order.getCategoryTitle());
            }
            if (tb != null) {
                tb.setText(order.getDescription());
            }

            if (order.getType() == OrdersDataSource.TYPE_INCOME) {
                iv.setImageResource(R.drawable.plus);
            } else {
                iv.setImageResource(R.drawable.minus);
            }
        }
        return v;
    }
}
