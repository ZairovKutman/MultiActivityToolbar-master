package kg.soulsb.ayu.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.activities.zakaz.OrderAddActivity;
import kg.soulsb.ayu.adapters.OrderAdapter;
import kg.soulsb.ayu.adapters.TasksAdapter;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.BazasRepo;
import kg.soulsb.ayu.helpers.repo.DailyTasksRepo;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.DailyTask;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.services.LocationService;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.CurrentLocationClass;
import kg.soulsb.ayu.singletons.DataHolderClass;
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
    TextView agentNameText, notDeliveredTextView;
    DBHelper dbHelper;
    ImageView image_gps;
    TasksAdapter listAdapter;
    ArrayList<DailyTask> orderTasksArraylist = new ArrayList<>();
    public static final String ACTION_LOCATION_BROADCAST = "LocationBroadcast";
    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String active = intent.getStringExtra("active");

            if (active.equals("yes_green"))  {  image_gps.setBackgroundColor(Color.GREEN); return;}

            if (active.equals("yes_yellow")) { image_gps.setBackgroundColor(Color.YELLOW); return;}

            if (active.equals("no")) {image_gps.setBackgroundColor(Color.RED); return;}

            if (active.equals("checkgps")) {image_gps.setBackgroundColor(Color.RED); checkGps(MainActivity.this); return;}

            if (active.equals("destroyed")) {image_gps.setBackgroundColor(Color.RED); startStep1();}

    }
    };

    @Override
    protected void onStart() {
        super.onStart();
        updateListView();
        updateMainMenu();
        setUpNavView();
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
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
        image_gps = (ImageView) findViewById(R.id.imageViewgps);
        notDeliveredTextView = findViewById(R.id.main_textView_notDeliveredDocuments);

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
        int x=0;
        if (sharedPreferences.getString(UserSettings.can_create_orders, "true").equals("false")) {

            if (sharedPreferences.getString(UserSettings.can_create_sales, "true").equals("true")) {
                createButton.setText("\nНовая продажа");
                x=1;

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
            x=1;
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

        if (sharedPreferences.getString(UserSettings.workWithTasks, "false").equals("true")) {
            createButton.setText("\nЗадания");
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    intent = new Intent(getApplicationContext(), TasksActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

        }
        else {
            if (x==0){
                createButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void updateDocuments() {
        dbHelper = new DBHelper(getBaseContext());
        DatabaseManager.initializeInstance(dbHelper);

        listViewDocuments.setEmptyView(findViewById(R.id.empty));
        SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(), MODE_PRIVATE);
        if (sharedPreferences.getString(UserSettings.workWithTasks,"false").equals("true"))
        {
            notDeliveredTextView.setText("Задания");
            orderTasksArraylist = new DailyTasksRepo().getDailyTasksForMain();
            listAdapter = new TasksAdapter(MainActivity.this, R.layout.tasks_list, orderTasksArraylist);
            listViewDocuments.setAdapter(listAdapter);

            listViewDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0)
                    {
                        if (orderTasksArraylist.get(position-1).getStatus().equals("0") && orderTasksArraylist.get(position-1).getPriority()!= orderTasksArraylist.get(position).getPriority()) {
                            Toast.makeText(MainActivity.this,"Нужно закончить предыдущее задание!",Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    Location clientLocation = orderTasksArraylist.get(position).getClientLocation();
                    Location myLocation = CurrentLocationClass.getInstance().getCurrentLocation();

                    float distance = myLocation.distanceTo(clientLocation);
                    if (!sharedPreferences.getString(UserSettings.create_order_at_clients_coordinates,"false").equals("true"))
                    {
                        distance = 1;
                    }

                    if (distance>200) {
                        AlertDialog.Builder alertDlg = new AlertDialog.Builder(MainActivity.this);
                        alertDlg.setMessage("Клиент находится на расстоянии " + distance + "м. Подойдите ближе!");
                        alertDlg.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                return;
                            }
                        });
                        alertDlg.setCancelable(false);
                        alertDlg.show();
                        return;
                    }

                    intent = new Intent(MainActivity.this, TasksDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("clientGuid", orderTasksArraylist.get(position).getClientGuid());
                    intent.putExtra("clientName", orderTasksArraylist.get(position).getClientName());
                    intent.putExtra("clientLat", orderTasksArraylist.get(position).getLatitude());
                    intent.putExtra("clientLon", orderTasksArraylist.get(position).getLongitude());
                    intent.putExtra("priority", orderTasksArraylist.get(position).getPriority());
                    intent.putExtra("status", orderTasksArraylist.get(position).getStatus());


                    startActivity(intent);

                }
            });

        }
        else {
            notDeliveredTextView.setText("Невыгруженные документы");
            orderArrayList = new OrdersRepo().getOrdersObjectNotDelivered(CurrentBaseClass.getInstance().getCurrentBase());
            orderArrayAdapter = new OrderAdapter(this, R.layout.list_docs_layout, orderArrayList);
            listViewDocuments.setAdapter(orderArrayAdapter);
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
        }



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
        IntentFilter filter = new IntentFilter(ACTION_LOCATION_BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

        checkGps(MainActivity.this);
        if (!DataHolderClass.getInstance().isServiceRunning()) {
            startStep1();
        }
    }

    /**
     * Step 1: Check Google Play services
     */
    private void startStep1() {
        startStep2(null);

    }

    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean startStep2(DialogInterface dialog) {
//
        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startStep3() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        if (!DataHolderClass.getInstance().isServiceRunning()) {

            System.out.println("Location Monitoring Service started");
            DataHolderClass.getInstance().setServiceRunning(true);
            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationService.class);
            startService(this,intent);

            //Ends................................................
        }
    }

    public void startService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            System.out.println("STARTING SERVICE FROM startForegroundService");
            context.startForegroundService(intent);
        } else {
            System.out.println("STARTING SERVICE FROM startService");
            context.startService(intent);
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(1,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }

    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    @Override
    public void onDestroy() {


        //Stop location sharing service to app server.........
        System.out.println("ON DESTROY EXECUTED MAIN ACTIVITY AND KILLED SERVICE");
        stopService(new Intent(this, LocationService.class));
        DataHolderClass.getInstance().setServiceRunning(false);
        //Ends................................................


        super.onDestroy();
    }

}
