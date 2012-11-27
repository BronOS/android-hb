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
import com.bronos.hb.ds.AccountsDataSource;
import com.bronos.hb.model.Account;
import com.bronos.hb.model.Category;

import java.util.List;

public class HBActivity extends ListActivity {
    private AccountsDataSource datasource;
    final private static int ACCOUNTS_MENU_EDIT = 1;
    final private static int ACCOUNTS_MENU_REMOVE = 2;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        datasource = new AccountsDataSource(this);
        datasource.open();

        showList();

        ListView list = (ListView)getListView();
        registerForContextMenu(list);
    }

    private void showList()
    {
        List<Account> values = datasource.getAllAccounts();
        ArrayAdapter<Account> adapter = new ArrayAdapter<Account>(this, android.R.layout.simple_list_item_1, values);
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

    private void addAccount()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.add_account);

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton(this.getString(R.string.add), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                datasource.createAccount(input.getText().toString(), new Float(0));
                showList();
            }
        });

        addCancelButtonToMenu(builder);

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
                showList();
            }
        });

        addCancelButtonToMenu(builder);

        builder.show();
    }

    private void removeAccount(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Account account = (Account)getListAdapter().getItem(info.position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.remove_account);
        builder.setMessage(R.string.remove_account_description);

        builder.setPositiveButton(this.getString(R.string.remove), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                datasource.deleteAccount(account);
                showList();
            }
        });

        addCancelButtonToMenu(builder);

        builder.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.manage_account));
        menu.add(0, ACCOUNTS_MENU_EDIT, ACCOUNTS_MENU_EDIT, R.string.edit);
        menu.add(0, ACCOUNTS_MENU_REMOVE, ACCOUNTS_MENU_REMOVE, R.string.remove);
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
            case R.id.categories:
                Intent intent = new Intent(this, CategoriesActivity.class);
//                intent.setData(Uri.parse("content://uri_to_my_object"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ACCOUNTS_MENU_EDIT:
                editAccount(item);
                return true;
            case ACCOUNTS_MENU_REMOVE:
                removeAccount(item);
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
    public void onListItemClick(ListView parent, View view, int position, long id)
    {
        final Account account = (Account)getListAdapter().getItem(position);
        view.setEnabled(false);
    }
}
