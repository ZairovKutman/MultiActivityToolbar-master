package kg.soulsb.ayu.activities.zakaz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kg.soulsb.ayu.activities.BaseActivity;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.adapters.ViewPagerAdapter;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.CurrentLocationClass;
import kg.soulsb.ayu.singletons.UserSettings;

public class OrderAddActivity extends BaseActivity {
    public ArrayList<Item> orderedItemsArrayList = new ArrayList<>();
    private String priceTypeGUID = "";
    private String warehouseGUID = "";
    ViewPagerAdapter adapter;
    String doctype = "";
    private Timer mTimer = new Timer();
    Location mLastLocation;
    String clientLat="0";
    String clientLong="0";
    Location clientLocation = new Location("");
    Order order = null;
    String isDelivered = "false";
    String category = "";
    ViewPager viewPager;
    float distance=0;

    AddOrderFragment addOrderFragment = new AddOrderFragment();
    //AddTovarFragment addTovarFragment = new AddTovarFragment();
    TovarsFragment tovarsFragment = new TovarsFragment();
    OthersFragment  othersFragment = new OthersFragment();

    double totalSum=0;
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

            if (Double.parseDouble(addOrderFragment.clientLat) == 0) return;
            clientLat = addOrderFragment.clientLat;
            clientLong = addOrderFragment.clientLong;
            clientLocation.setLatitude(Double.parseDouble(clientLat));
            clientLocation.setLongitude(Double.parseDouble(clientLong));
            distance = mLastLocation.distanceTo(clientLocation);
            System.out.println("DISTANCE = "+distance);
            if (distance<100) {

            }
            else {

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

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }

    private void createDialog() {
        if (isDelivered.equals("true"))
        {
            OrderAddActivity.super.onBackPressed();
            return;
        }

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);

        alertDlg.setMessage("Сохранить документ перед выходом?");

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

        alertDlg.setCancelable(false); // We avoid that the dialog can be cancelled, forcing the user to choose one of the options

        alertDlg.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
        mLastLocation = getLocation();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
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
            else {
                setTitle("Новый документ - Продажа");
                checkGps(OrderAddActivity.this);
            }
        }
        else
        {
            doctype = "0";
            if (getIntent().getSerializableExtra("savedobj")!=null)
                setTitle("Сохраненный документ - Заказ");
            else {
                setTitle("Новый документ - Заказ");
                checkGps(OrderAddActivity.this);
        }

        }

        order = (Order) getIntent().getSerializableExtra("savedobj");

        if (getIntent().getStringExtra("isDelivered")!=null)
        {
            isDelivered = getIntent().getStringExtra("isDelivered");
            Toast.makeText(this, "Документ выгружен, редактирование запрещено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    public void addItem(Item item)
    {
        orderedItemsArrayList.add(item);
        totalSum=totalSum+item.getSum();
        othersFragment.updateTotalSum();

    }

    public double calculateTotalSum() {
        double mTotalSum=0;

        for (Item item: orderedItemsArrayList)
        {
            mTotalSum = mTotalSum + item.getSum();
        }

        return mTotalSum;
    }
    public ArrayList<Item> getSelectedItems()
    {
        return orderedItemsArrayList;
    }

    public void setupViewPager(ViewPager viewPager) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        adapter = new ViewPagerAdapter(fragmentManager);

        adapter.addFrag(addOrderFragment, "Клиент");
        //adapter.addFrag(addTovarFragment, "Товары");
        adapter.addFrag(tovarsFragment, "Товары RV");
        adapter.addFrag(othersFragment, "Прочее");

        addOrderFragment = (AddOrderFragment) adapter.getItem(0);
        //addTovarFragment = (AddTovarFragment) adapter.getItem(1);
        tovarsFragment = (TovarsFragment) adapter.getItem(1);
        othersFragment = (OthersFragment) adapter.getItem(2);

        viewPager.setAdapter(adapter);
    }

//    public void checkItem(Item item) {
//        ArrayList<Item> itemArrayList2 = new ArrayList<>();
//
//        for (Item item2: orderedItemsArrayList)
//        {
//            if (item.getGuid().equals(item2.getGuid()))
//            {
//                itemArrayList2.add(item2);
//                totalSum = totalSum - item2.getSum();
//            }
//        }
//        orderedItemsArrayList.removeAll(itemArrayList2);
//        othersFragment.updateTotalSum(totalSum);
//    }

    public String getPriceType()
    {
        return priceTypeGUID;
    }

    public void setPriceTypeGUID(String priceTypeGUID) {
        this.priceTypeGUID = priceTypeGUID;
    }

    public void updatePrices()
    {
        if (tovarsFragment != null)
            tovarsFragment.updatePrices();
    }

    public String getWarehouse() {
        return warehouseGUID;
    }

    public void setWarehouseGuid(String warehouseGuid) {
        this.warehouseGUID = warehouseGuid;
    }

    public void updateStock()
    {
        if (tovarsFragment != null)
            tovarsFragment.updateStock();
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