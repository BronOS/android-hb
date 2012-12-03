package com.bronos.hb.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.bronos.hb.R;
import com.bronos.hb.model.Account;

import java.util.ArrayList;

public class AccountsAdapter extends ArrayAdapter {

    private ArrayList<Account> items;

    public AccountsAdapter(Context context, int textViewResourceId, ArrayList<Account> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.account_row, null);
        }
        Account account = items.get(position);
        if (account != null) {
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            if (tt != null) {
                tt.setText(account.getTitle() + ":");
            }
            if(bt != null){
                bt.setText(account.getAmount().toString());
                if (account.getAmount() < 1000) {
                    bt.setTextColor(Color.parseColor("#F0C5D6"));
                } else if (account.getAmount() < 100) {
                    bt.setTextColor(Color.parseColor("#FF7A7A"));
                } else if (account.getAmount() < 0) {
                    bt.setTextColor(Color.RED);
                } else {
                    bt.setTextColor(Color.GREEN);
                }
            }
        }
        return v;
    }
}
