package kg.soulsb.ayu.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
                    .getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        }

        SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);

        if (sharedPreferences.contains("default_name")) {

            if (sharedPreferences.getString(UserSettings.can_create_orders,"true").equals("false"))
            {
                Menu menuNav=navigationView.getMenu();
                MenuItem nav_item2 = menuNav.findItem(R.id.nav_orders);
                nav_item2.setEnabled(false);
            }
            else
            {
                Menu menuNav=navigationView.getMenu();
                MenuItem nav_item2 = menuNav.findItem(R.id.nav_orders);
                nav_item2.setEnabled(true);
            }

            if (sharedPreferences.getString(UserSettings.can_create_sales,"true").equals("false"))
            {
                Menu menuNav=navigationView.getMenu();
                MenuItem nav_item2 = menuNav.findItem(R.id.nav_orders_real);
                nav_item2.setEnabled(false);
            }
            else
            {
                Menu menuNav=navigationView.getMenu();
                MenuItem nav_item2 = menuNav.findItem(R.id.nav_orders);
                nav_item2.setEnabled(true);
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

                    if (newTime - oldTime > 12*3600*1000) {
                        //
                        Toast.makeText(getBaseContext(),"Сделайте обмен!",Toast.LENGTH_SHORT).show();
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

        if (checkDailyExchange() && id != R.id.nav_settings) return super.onOptionsItemSelected(item);
        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.nav_main:
                startActivity(new Intent(this,MainActivity.class));
                return true;

            case R.id.nav_orders:
                intent = new Intent(this, OrderAddActivity.class);
                intent.putExtra("doctype","0");
                startActivity(intent);
                return true;

            case R.id.nav_orders_real:
                intent = new Intent(this, OrderAddActivity.class);
                intent.putExtra("doctype","1");
                startActivity(intent);
                return true;

            case R.id.nav_journal :
                startActivity(new Intent(this, SavedDocumentsActivity.class));
                return true;

            case R.id.nav_catalog :
                startActivity(new Intent(this, ItemsTableActivity.class));
                return true;
            case R.id.nav_clients :
                startActivity(new Intent(this, ClientsTableActivity.class));
                return true;
            case R.id.nav_reports :
                startActivity(new Intent(this, ReportsActivity.class));
                return true;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsBasesActivity.class));
                return true;
            case R.id.nav_settings_obmen :
                startActivity(new Intent(this, SettingsObmenActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setBaseAgentName() {

        SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
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
}

