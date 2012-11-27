package com.bronos.hb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.bronos.hb.model.Account;

import java.util.List;

public class HB extends ListActivity {
    private AccountsDataSource datasource;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        datasource = new AccountsDataSource(this);
        datasource.open();

        showAccounts();
    }

    public void showAccounts()
    {
        List<Account> values = datasource.getAllAccounts();
        ArrayAdapter<Account> adapter = new ArrayAdapter<Account>(this, android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        ListView list = (ListView)getListView();
        registerForContextMenu(list);
    }

    private void addAccount()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.add_account);

//        String message = this.getString(R.string.del_alert_message);
//        message = String.format(message, selectedContact.getName());
//        builder.setMessage(message);

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton(this.getString(R.string.add), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                datasource.createAccount(input.getText().toString(), new Float(0));
                showAccounts();
            }
        });
        builder.setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void editAccount(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Account account = (Account)getListAdapter().getItem(info.position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.edit_account);

        final EditText input = new EditText(this);
        input.setText(account.getTitle());
        builder.setView(input);

        builder.setPositiveButton(this.getString(R.string.edit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                account.setTitle(input.getText().toString());
                datasource.updateAccount(account);
                showAccounts();
            }
        });
        builder.setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.context_menu, menu);
        menu.setHeaderTitle(getString(R.string.manage_account));
        menu.add(0, 1, 1, R.string.edit);
        menu.add(0, 2, 2, R.string.remove);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.accounts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                addAccount();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                editAccount(item);
                return true;
            case 2:
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
}
