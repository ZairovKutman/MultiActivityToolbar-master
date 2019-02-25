package kg.soulsb.ayu.activities.zakaz;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
    float distance=0;
    public static final String ACTION_LOCATION_BROADCAST = "LocationBroadcast";
    Location mLastLocation = new Location("mLocChooseClient");
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Double lat = intent.getExtras().getDouble("lat");

            Double lon = intent.getExtras().getDouble("lon");

            mLastLocation.setLatitude(lat);
            mLastLocation.setLongitude(lon);
            String active = intent.getStringExtra("active");
            if (active.equals("checkgps")) { checkGps(ChooseClientTableActivity.this); return;}
            //if (active.equals("test")) {notasksTextView.setText(testText);}
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_client);
        final SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
        Double lat = 0.0;
        Double lon = 0.0;

        String doctype = getIntent().getExtras().getString("doctype");

        if (!doctype.equals("3")) {
            lat = getIntent().getExtras().getDouble("latitude");
            lon = getIntent().getExtras().getDouble("longitude");
        }

        mLastLocation.setLatitude(lat);
        mLastLocation.setLongitude(lon);

        IntentFilter filter = new IntentFilter(ACTION_LOCATION_BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

        DBHelper dbHelper = new DBHelper(getBaseContext());
        DatabaseManager.initializeInstance(dbHelper);
        arrayList = new ClientsRepo().getClientsObject();
        arrayAdapter = new ClientAdapter(this,R.layout.list_client_layout, arrayList);
        final ListView listViewClients = (ListView) findViewById(R.id.list_view_clients);

        listViewClients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Client str = arrayList.get(position);
                if (sharedPreferences.contains("default_name")) {

                    if (sharedPreferences.getString(UserSettings.forbit_select_client_with_outstanding_debt, "false").equals("true")) {
                        if (str.getDebt() > 0){
                            Toast.makeText(ChooseClientTableActivity.this, "Невозможно выбрать, у клиента есть долг", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                String flag = "false";

                String doctype = getIntent().getExtras().getString("doctype");

                if (doctype.equals("1")){
                    flag = sharedPreferences.getString(UserSettings.create_sales_at_clients_coordinates,"false");}
                else if (doctype.equals("2")){
                    flag = sharedPreferences.getString(UserSettings.create_order_at_clients_coordinates,"false");
                }

                if (flag.equals("true")) {

                    Location clientLocation = new Location("loc");
                    clientLocation.setLatitude(Double.parseDouble(str.getLatitude()));
                    clientLocation.setLongitude(Double.parseDouble(str.getLongitude()));

                    distance = mLastLocation.distanceTo(clientLocation);

                }
                else {distance = 0;}


                if (distance > 100) {
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(ChooseClientTableActivity.this);
                    alertDlg.setMessage("Клиент находится на расстоянии "+distance+"м. Подойдите ближе!");
                    alertDlg.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            return;
                        }
                    });
                    alertDlg.setCancelable(false);
                    alertDlg.show();

                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra("data", str.getName());
                    intent.putExtra("guid", str.getGuid());
                    intent.putExtra("lat", str.getLatitude());
                    intent.putExtra("long", str.getLongitude());
                    setResult(RESULT_OK, intent);
                    finish();
                }
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