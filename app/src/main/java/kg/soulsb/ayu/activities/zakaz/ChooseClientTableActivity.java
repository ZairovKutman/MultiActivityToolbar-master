package kg.soulsb.ayu.activities.zakaz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import android.support.v7.widget.SearchView;

import java.util.ArrayList;

import kg.soulsb.ayu.activities.BaseActivity;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.adapters.ClientAdapter;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.UserSettings;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class ChooseClientTableActivity extends BaseActivity {
    SearchView searchView;
    ClientAdapter arrayAdapter;
    ArrayList<Client> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_client);
        DBHelper dbHelper = new DBHelper(getBaseContext());
        DatabaseManager.initializeInstance(dbHelper);
        arrayList = new ClientsRepo().getClientsObject();
        arrayAdapter = new ClientAdapter(this,R.layout.list_client_layout, arrayList);
        final ListView listViewClients = (ListView) findViewById(R.id.list_view_clients);

        listViewClients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
                Client str = arrayList.get(position);
                if (sharedPreferences.contains("default_name")) {

                    if (sharedPreferences.getString(UserSettings.forbit_select_client_with_outstanding_debt, "false").equals("true")) {
                        if (str.getDebt() > 0){
                            Toast.makeText(ChooseClientTableActivity.this, "Невозможно выбрать, у клиента есть долг", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("data",str.getName());
                intent.putExtra("guid",str.getGuid());
                intent.putExtra("lat",str.getLatitude());
                intent.putExtra("long",str.getLongitude());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        listViewClients.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_orders)
            return true;

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }
}