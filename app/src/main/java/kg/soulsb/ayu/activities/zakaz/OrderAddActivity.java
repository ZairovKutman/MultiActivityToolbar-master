package kg.soulsb.ayu.activities.zakaz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import kg.soulsb.ayu.activities.BaseActivity;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.adapters.ViewPagerAdapter;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.CurrentLocationClass;
import kg.soulsb.ayu.singletons.DataHolderClass;
import kg.soulsb.ayu.singletons.UserSettings;

public class OrderAddActivity extends BaseActivity {
    public Map<Item,Integer> orderedItemsArrayList = new HashMap<>();
    private String priceTypeGUID = "";
    private String warehouseGUID = "";
    ViewPagerAdapter adapter;
    String doctype = "";
    private Timer mTimer = new Timer();
    Location mLastLocation;
    AddOrderFragment addOrderFragment;
    String clientLat="0";
    String clientLong="0";
    Location clientLocation = new Location("");
    Order order = null;
    Fragment addOrder = new AddOrderFragment();
    float distance=0;
    public Location getLocation() {
        System.out.println(" I GOT A LOCATION: lat="+CurrentLocationClass.getInstance().getCurrentLocation().getLatitude()
                +" long="+CurrentLocationClass.getInstance().getCurrentLocation().getLongitude());
        return CurrentLocationClass.getInstance().getCurrentLocation();
    }

public void locationUpdate(){
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
            // run on another thread
            mLastLocation = getLocation();
            clientLat = addOrderFragment.clientLat;
            clientLong = addOrderFragment.clientLong;
            clientLocation.setLatitude(Double.parseDouble(clientLat));
            clientLocation.setLongitude(Double.parseDouble(clientLong));
            distance = mLastLocation.distanceTo(clientLocation);
            System.out.println("DISTANCE = "+distance);
            if (distance<51) {
                addOrderFragment.createDocButton.setEnabled(true);
            }
            else {
                addOrderFragment.createDocButton.setEnabled(false);
            }
        }
    }, 0);
}

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            locationUpdate();
        }
    }

    @Override
    public void onBackPressed()
    {
        createDialog();
    }

    private void createDialog() {

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage("Сохранить документ перед выходом?");
        alertDlg.setCancelable(false); // We avoid that the dialong can be cancelled, forcing the user to choose one of the options
        alertDlg.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (addOrderFragment.documentIsReady()) {
                            addOrderFragment.saveDocument(false);
                            Toast.makeText(getBaseContext(),"Сохранено",Toast.LENGTH_SHORT).show();
                            OrderAddActivity.super.onBackPressed();
                        }
                    }
        });

        alertDlg.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OrderAddActivity.super.onBackPressed();
            }
        });
        alertDlg.create().show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //
        SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
        String flag;
        if (doctype.equals("1")){
            flag = sharedPreferences.getString(UserSettings.create_sales_at_clients_coordinates,"false");}
        else {
            flag = sharedPreferences.getString(UserSettings.create_order_at_clients_coordinates,"false");
        }
        if (flag.equals("true")) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new OrderAddActivity.TimeDisplay(), 2000, 2000);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add);
        DataHolderClass.getInstance().setAddOrderComments("");
        DataHolderClass.getInstance().setAddOrderDateOtgruzki("");


        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        Toast.makeText(getApplicationContext(),"Клиент",Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(),"Товары",Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(),"Прочее",Toast.LENGTH_LONG).show();
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if (getIntent().getStringExtra("doctype").equals("1")) {
            doctype = "1";
            if (getIntent().getSerializableExtra("savedobj")!=null)
                setTitle("Сохраненный документ - Продажа");
            else
                setTitle("Новый документ - Продажа");
        }
        else
        {
            doctype = "0";
            if (getIntent().getSerializableExtra("savedobj")!=null)
                setTitle("Сохраненный документ - Заказ");
            else
                setTitle("Новый документ - Заказ");

        }

        order = (Order) getIntent().getSerializableExtra("savedobj");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (doctype.equals("1"))
        switch (item.getItemId()) {
            case R.id.nav_orders_real:
                return true;
        }
        else
            switch (item.getItemId()) {
                case R.id.nav_orders:
                    return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addItem(Item item, int quantity)
    {
        orderedItemsArrayList.put(item,quantity);
    }
    public Map<Item, Integer> getSelectedItems()
    {
        return orderedItemsArrayList;
    }

    public void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(addOrder, "Клиент");
        adapter.addFrag(new AddTovarFragment(), "Товары");
        adapter.addFrag(new OthersFragment(), "Прочее");
        addOrderFragment = (AddOrderFragment) adapter.getItem(0);
        viewPager.setAdapter(adapter);
    }

    public void checkItem(Item item) {
        if (orderedItemsArrayList.containsKey(item))
        {
            orderedItemsArrayList.remove(item);
        }
    }

    public String getPriceType()
    {
        return priceTypeGUID;
    }

    public void setPriceTypeGUID(String priceTypeGUID) {
        this.priceTypeGUID = priceTypeGUID;
    }

    public void updatePrices()
    {
        AddTovarFragment fragment = (AddTovarFragment) adapter.getItem(1);
        if (fragment != null)
            fragment.updatePrices();
    }

    public String getWarehouse() {
        return warehouseGUID;
    }

    public void setWarehouseGuid(String warehouseGuid) {
        this.warehouseGUID = warehouseGuid;
    }

    public void updateStock()
    {
        AddTovarFragment fragment = (AddTovarFragment) adapter.getItem(1);
        if (fragment != null)
            fragment.updateStock();
    }

     @Override
     public void onPause()
     {
         super.onPause();
         SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
         String flag;
         if (doctype.equals("1")){
             flag = sharedPreferences.getString(UserSettings.create_sales_at_clients_coordinates,"false");}
         else{
             flag = sharedPreferences.getString(UserSettings.create_order_at_clients_coordinates,"false");
         }
         if (flag.equals("true"))
         {
             mTimer.cancel();
         }
     }

}