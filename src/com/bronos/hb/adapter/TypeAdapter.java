package com.bronos.hb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bronos.hb.R;
import com.bronos.hb.ds.OrdersDataSource;
import com.bronos.hb.model.Category;
import com.bronos.hb.model.Type;

import java.util.ArrayList;

public class TypeAdapter extends ArrayAdapter {

    private ArrayList<Type> items;

    public TypeAdapter(Context context, int textViewResourceId, ArrayList<Type> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.type_row, null);
        }
        Type type = items.get(position);
        if (type != null) {
            ImageView iv = (ImageView) v.findViewById(R.id.icon);
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            if (tt != null) {
                tt.setText(type.getTitle());
            }
            if (iv != null) {
                int resId = R.drawable.type;
                if (type.getId() == OrdersDataSource.TYPE_INCOME) {
                    resId = R.drawable.plus;
                } else if (type.getId() == OrdersDataSource.TYPE_OUTGO) {
                    resId = R.drawable.minus;
                }
                iv.setImageResource(resId);
            }
        }
        return v;
    }
}
