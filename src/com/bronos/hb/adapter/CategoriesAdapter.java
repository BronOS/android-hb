package com.bronos.hb.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bronos.hb.R;
import com.bronos.hb.model.Account;
import com.bronos.hb.model.Category;

import java.util.ArrayList;

public class CategoriesAdapter extends ArrayAdapter {

    private ArrayList<Category> items;

    public CategoriesAdapter(Context context, int textViewResourceId, ArrayList<Category> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.category_row, null);
        }
        Category category = items.get(position);
        if (category != null) {
            ImageView iv = (ImageView) v.findViewById(R.id.icon);
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            if (tt != null) {
                tt.setText(category.getTitle());
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.FILL_PARENT
            );
            lp.setMargins((int) (category.getLevel() * 20), 0,10,0);
            iv.setLayoutParams(lp);
        }
        return v;
    }
}
