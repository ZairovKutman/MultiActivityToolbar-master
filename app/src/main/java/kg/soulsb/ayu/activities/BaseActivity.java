package kg.soulsb.ayu.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import kg.soulsb.ayu.activities.zakaz.OrderAddActivity;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.UserSettings;

public class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout fullLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private int selectedNavItemId;
    SharedPreferences sharedPreferences = null;
    Intent intent;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setUpNavView();
    }
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        /**
         * This is going to be our actual root layout.
         */
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        /**
         * {@link FrameLayout} to inflate the child's view. We could also use a {@link android.view.ViewStub}
         */
        FrameLayout activityContainer = (FrameLayout) fullLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        /**
         * Note that we don't pass the child's layoutId to the parent,
         * instead we pass it our inflated layout.
         */
        super.setContentView(fullLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        if (useToolbar())
        {
            setSupportActionBar(toolbar);
        }
        else
        {
            toolbar.setVisibility(View.GONE);
        }

        setUpNavView();

    }

    /**
     * Helper method that can be used by child classes to
     * specify that they don't want a {@link Toolbar}
     * @return true
     */
    protected boolean useToolbar()
    {
        return true;
    }

    protected void setUpNavView()
    {

        navigationView.setNavigationItemSelectedListener(this);

        if( useDrawerToggle()) { // use the hamburger menu
            drawerToggle = new ActionBarDrawerToggle(this, fullLayout, toolbar,
                    R.string.nav_drawer_opened,
                    R.string.nav_drawer_closed);

            fullLayout.setDrawerListener(drawerToggle);
            drawerToggle.syncState();
        } else if(useToolbar() && getSupportActionBar() != null) {
            // Use home/back button instead
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources()
                    .getDrawable(R.drawable.ic_action_navigation_arrow_back));
        }

        Menu menuNav=navigationView.getMenu();
        MenuItem nav_item2 = menuNav.findItem(R.id.nav_messages);
        nav_item2.setEnabled(false);

        MenuItem nav_itemTasks = menuNav.findItem(R.id.nav_tasks);
        nav_itemTasks.setEnabled(false);

        MenuItem nav_itemScan = menuNav.findItem(R.id.nav_scan);
        nav_itemScan.setEnabled(false);

        SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);

        if (sharedPreferences.contains("default_name")) {
            if (sharedPreferences.getString(UserSettings.can_create_orders,"true").equals("false"))
            {
                MenuItem nav_item4 = menuNav.findItem(R.id.nav_orders);
                nav_item4.setEnabled(false);
            }
            else
            {

                MenuItem nav_item4 = menuNav.findItem(R.id.nav_orders);
                nav_item4.setEnabled(true);
            }

            if (sharedPreferences.getString(UserSettings.can_create_sales,"true").equals("false"))
            {

                MenuItem nav_item5 = menuNav.findItem(R.id.nav_orders_real);
                nav_item5.setEnabled(false);
            }
            else
            {

                MenuItem nav_item5 = menuNav.findItem(R.id.nav_orders_real);
                nav_item5.setEnabled(true);
            }

            if (sharedPreferences.getString(UserSettings.can_create_payment,"true").equals("false"))
            {

                MenuItem nav_item7 = menuNav.findItem(R.id.nav_pay_svod);
                nav_item7.setEnabled(false);

                MenuItem nav_item71 = menuNav.findItem(R.id.nav_pay);
                nav_item71.setEnabled(false);
            }
            else
            {

                MenuItem nav_item7 = menuNav.findItem(R.id.nav_pay_svod);
                nav_item7.setEnabled(true);

                MenuItem nav_item71 = menuNav.findItem(R.id.nav_pay);
                nav_item71.setEnabled(false);
            }
        }
        setBaseAgentName();
    }

    public boolean checkDailyExchange() {
        SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
        if (sharedPreferences.getString(UserSettings.force_daily_exchange,"false").equals("true"))
        {
            if (sharedPreferences.getString(UserSettings.force_daily_exchange,"false").equals("true"))
            {
                if (sharedPreferences.getLong("LAST_OBMEN_MILLI",0) != 0)
                {
                    Long oldTime = sharedPreferences.getLong("LAST_OBMEN_MILLI",0);
                    Long newTime = System.currentTimeMillis();

                    if (newTime - oldTime > 18*3600*1000) {
                        //
                        Toast.makeText(getBaseContext(),"Необходимо сделать обмен!",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(),SettingsObmenActivity.class);
                        startActivity(intent);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Helper method to allow child classes to opt-out of having the
     * hamburger menu.
     * @return
     */
    protected boolean useDrawerToggle()
    {
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        fullLayout.closeDrawer(GravityCompat.START);
        selectedNavItemId = menuItem.getItemId();

        return onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO: Расскоментировать код потом.

        //if (checkDailyExchange() && id != R.id.nav_settings) return super.onOptionsItemSelected(item);
        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.nav_main:
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.nav_tasks:
                intent = new Intent(this, TasksActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.nav_scan:
                intent = new Intent(this, ScanDocumentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.nav_orders:
                intent = new Intent(this, OrderAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("doctype","0");
                startActivity(intent);
                return true;

            case R.id.nav_orders_real:
                intent = new Intent(this, OrderAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("doctype","1");
                startActivity(intent);
                return true;

            case R.id.nav_journal :
                intent = new Intent(this, SavedDocumentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.nav_catalog :
                intent = new Intent(this, ItemsTableActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.nav_clients :
                intent = new Intent(this, ClientsTableActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.nav_reports :
                intent = new Intent(this, ReportsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsBasesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.nav_settings_obmen :
                intent = new Intent(this, SettingsObmenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.nav_messages :
                intent = new Intent(this, MessagesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.nav_pay :
                intent = new Intent(this, PayActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.nav_pay_svod:
                intent = new Intent(this, PaySvodActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.nav_svod :
                intent = new Intent(this, SvodActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setBaseAgentName() {
            sharedPreferences = getSharedPreferences("DefaultBase", MODE_PRIVATE);
            String currentBaseString = "нет базы";
            String currentAgentString = "Анонимный пользователь";
            if (sharedPreferences.contains("default_name")) {
                currentBaseString = sharedPreferences.getString("default_name", null);
                currentAgentString = sharedPreferences.getString("default_agent", null);
            }


        NavigationView navigationView = (NavigationView) super.findViewById(R.id.navigationView);
        View headerLayout = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerLayout.findViewById(R.id.main_BaseName);
        TextView agentView = (TextView) headerLayout.findViewById(R.id.main_agentName);

        textView.setText("База: "+currentBaseString);
        agentView.setText(currentAgentString);
    }

    public void checkGps(final Context context) {
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        System.out.println(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(), MODE_PRIVATE);

            if (sharedPreferences.getString(UserSettings.force_gps_turn_on, "false").equals("true")) {
                final AlertDialog.Builder alertDlg = new AlertDialog.Builder(context);
                alertDlg.setMessage("Включите GPS !");
                alertDlg.setCancelable(false); // We avoid that the dialog can be cancelled, forcing the user to choose one of the options
                alertDlg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            checkGps(context);
                        }
                    }
                } );

                alertDlg.setNegativeButton("Обмен данными", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(context,SettingsObmenActivity.class));
                    }
                } );
                alertDlg.create().show();
            };
        }
    }
}

