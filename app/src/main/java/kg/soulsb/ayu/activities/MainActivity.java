package kg.soulsb.ayu.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.activities.zakaz.OrderAddActivity;
import kg.soulsb.ayu.adapters.OrderAdapter;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.BazasRepo;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.MyServiceActivatorClass;
import kg.soulsb.ayu.singletons.UserSettings;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private Spinner baza;
    ArrayList<Baza> arrayList = new ArrayList<>();
    ArrayAdapter<Baza> arrayAdapter;
    ListView listViewDocuments;
    ArrayList<Order> orderArrayList = new ArrayList<>();
    OrderAdapter orderArrayAdapter;
    Button createButton, obmenButton, svodButton;
    TextView agentNameText;
    DBHelper dbHelper;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyServiceActivatorClass.getInstance(this).start();
                    // permission was granted, yay! Do the
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Дайте разрешение на геопозицию в настройках!!!", Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateListView();
        updateMainMenu();
        setUpNavView();
    }


    private void updateMainMenu() {
        TextView textView = (TextView) findViewById(R.id.textView_last_obmen);
        agentNameText = (TextView) findViewById(R.id.textView_agent_name);
        SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(), MODE_PRIVATE);
        String myDate = sharedPreferences.getString("LAST_OBMEN", null);

        if (myDate != null)
            textView.setText(myDate);
        else {
            textView.setText("Никогда");
        }

        sharedPreferences = getSharedPreferences("DefaultBase", MODE_PRIVATE);

        String currentAgentString = "Анонимный пользователь";
        if (sharedPreferences.contains("default_name")) {

            currentAgentString = sharedPreferences.getString("default_agent", null);
        }

        agentNameText.setText(currentAgentString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE},
                1);

        // Starting service
        // Intent mIntent = new Intent(this, MyService.class);
        // startService(mIntent);
        // База данных выбор из списка
        baza = (Spinner) findViewById(R.id.spinner_baza);

        baza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Baza baza1 = arrayList.get(position);
                SharedPreferences sharedPreferences1 = getSharedPreferences("DefaultBase", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.putString("default_name", baza1.getName());
                editor1.putString("default_host", baza1.getHost());
                editor1.putString("default_port", Integer.toString(baza1.getPort()));
                editor1.putString("default_agent", baza1.getAgent());
                editor1.apply();

                CurrentBaseClass.getInstance().setCurrentBase(baza1.getName());
                CurrentBaseClass.getInstance().setCurrentBaseObject(baza1);
                baza.setSelection(position);
                updateMainMenu();
                updateDocuments();
                updateFirstButton();
                setUpNavView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        updateListView();
        setUpNavView();

        listViewDocuments = (ListView) findViewById(R.id.listView_documents);
        updateDocuments();

        createButton = (Button) findViewById(R.id.create_order_button);
        obmenButton = (Button) findViewById(R.id.obmen_button);
        svodButton = (Button) findViewById(R.id.svod_button);

        updateFirstButton();


        obmenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), SettingsObmenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        svodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), SvodActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void updateFirstButton() {
        SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(), MODE_PRIVATE);

        if (sharedPreferences.getString(UserSettings.can_create_orders, "true").equals("false")) {

            if (sharedPreferences.getString(UserSettings.can_create_sales, "true").equals("true")) {
                createButton.setText("\nНовая продажа");


                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        intent = new Intent(getApplicationContext(), OrderAddActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("doctype", "1");
                        startActivity(intent);
                    }
                });

            } else {

                createButton.setVisibility(View.INVISIBLE);
            }
        } else {
            createButton.setText("\nНовый заказ");

            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(getApplicationContext(), OrderAddActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("doctype", "0");
                    startActivity(intent);
                }
            });
        }
    }

    private void updateDocuments() {
        dbHelper = new DBHelper(getBaseContext());
        DatabaseManager.initializeInstance(dbHelper);
        orderArrayList = new OrdersRepo().getOrdersObjectNotDelivered(CurrentBaseClass.getInstance().getCurrentBase());
        orderArrayAdapter = new OrderAdapter(this, R.layout.list_docs_layout, orderArrayList);
        listViewDocuments.setAdapter(orderArrayAdapter);
        listViewDocuments.setEmptyView(findViewById(R.id.empty));
        listViewDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (orderArrayList.get(i).getDoctype().equals("3")) {
                    Intent intent = new Intent(getBaseContext(), PaySvodActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("doctype", orderArrayList.get(i).getDoctype());
                    intent.putExtra("savedobj", orderArrayList.get(i));
                    startActivity(intent);
                } else if (!orderArrayList.get(i).getDoctype().equals("2")) {
                    Intent intent = new Intent(getBaseContext(), OrderAddActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("doctype", orderArrayList.get(i).getDoctype());
                    intent.putExtra("savedobj", orderArrayList.get(i));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getBaseContext(), PayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("doctype", orderArrayList.get(i).getDoctype());
                    intent.putExtra("savedobj", orderArrayList.get(i));
                    startActivity(intent);
                }
            }
        });

        updateMainMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.nav_main:
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsObmenActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateListView() {
        dbHelper = new DBHelper(getBaseContext());
        DatabaseManager.initializeInstance(dbHelper);
        arrayList = new BazasRepo().getBazasObject();
        arrayAdapter = new ArrayAdapter<Baza>(this, R.layout.baza_spinner_item, arrayList);
        baza.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences = getSharedPreferences("DefaultBase", MODE_PRIVATE);

        if (sharedPreferences.contains("default_name")) {
            String myString = sharedPreferences.getString("default_name", null);
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).getName().equals(myString)) {
                    baza.setSelection(i);
                    CurrentBaseClass.getInstance().setCurrentBase(myString);
                    CurrentBaseClass.getInstance().setCurrentBaseObject(arrayList.get(i));
                }
            }
        } else {
            // set default base
            SharedPreferences sharedPreferences1 = getSharedPreferences("DefaultBase", MODE_PRIVATE);
            CurrentBaseClass.getInstance().setCurrentBase(sharedPreferences1.getString("default_name", ""));
            CurrentBaseClass.getInstance().setCurrentBaseObject(new Baza(sharedPreferences1.getString("default_host", ""), Integer.parseInt(sharedPreferences1.getString("default_port", "0000")), sharedPreferences1.getString("default_name", ""), sharedPreferences1.getString("default_agent", ""), sharedPreferences1.getString("default_bazaId", "")));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
        setUpNavView();
        updateDocuments();
        checkGps(MainActivity.this);
    }
}
