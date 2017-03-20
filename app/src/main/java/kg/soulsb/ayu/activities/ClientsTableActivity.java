package kg.soulsb.ayu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.ArrayList;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.adapters.ClientAdapter;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.models.Client;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */
public class ClientsTableActivity extends BaseActivity implements MaterialSearchView.OnQueryTextListener{
    private MaterialSearchView searchView;
    private ListView listViewClients;
    private ClientAdapter arrayAdapter;
    private ArrayList<Client> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        DBHelper dbHelper = new DBHelper(getBaseContext());
        DatabaseManager.initializeInstance(dbHelper);

        arrayList = new ClientsRepo().getClientsObject();
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);
        arrayAdapter = new ClientAdapter(this,R.layout.list_client_layout, arrayList);

        listViewClients = (ListView) findViewById(R.id.list_view_clients);
        listViewClients.setTextFilterEnabled(true);
        listViewClients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Client client = arrayList.get(position);
                Intent intent = new Intent(getBaseContext(), ClientDetailActivity.class);
                intent.putExtra("name", client.getName());
                intent.putExtra("address", client.getAddress());
                intent.putExtra("phone", client.getPhone());
                intent.putExtra("comment", "");
                intent.putExtra("longitude", client.getLongitude());
                intent.putExtra("latitude", client.getLatitude());
                intent.putExtra("debt", Double.toString(client.getDebt()));
                intent.putExtra("guid", client.getGuid());
                startActivity(intent);
            }
        });
        listViewClients.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    protected boolean useDrawerToggle() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_clients)
            return true;
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            listViewClients.clearTextFilter();
        }
        else {
            listViewClients.setFilterText(newText);
        }
        return true;
    }
}