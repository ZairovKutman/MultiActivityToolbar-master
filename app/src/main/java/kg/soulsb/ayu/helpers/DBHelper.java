package kg.soulsb.ayu.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import kg.soulsb.ayu.helpers.repo.BazasRepo;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.helpers.repo.ContractsRepo;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.MyLocationsRepo;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.helpers.repo.OrganizationsRepo;
import kg.soulsb.ayu.helpers.repo.PriceTypesRepo;
import kg.soulsb.ayu.helpers.repo.PricesRepo;
import kg.soulsb.ayu.helpers.repo.ReportsRepo;
import kg.soulsb.ayu.helpers.repo.SavedReportsRepo;
import kg.soulsb.ayu.helpers.repo.StocksRepo;
import kg.soulsb.ayu.helpers.repo.WarehousesRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.models.Contract;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.MyLocation;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.models.Organization;
import kg.soulsb.ayu.models.Price;
import kg.soulsb.ayu.models.PriceType;
import kg.soulsb.ayu.models.Report;
import kg.soulsb.ayu.models.Stock;
import kg.soulsb.ayu.models.Warehouse;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ayu_sqlite.db";
    private static final String TAG = DBHelper.class.getSimpleName().toString();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL("DROP TABLE IF EXISTS " + Client.TABLE);
        db.execSQL(ClientsRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + Item.TABLE);
        db.execSQL(ItemsRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + Warehouse.TABLE);
        db.execSQL(WarehousesRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE);
        db.execSQL(ContractsRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + PriceType.TABLE);
        db.execSQL(PriceTypesRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + Baza.TABLE);
        db.execSQL(BazasRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + Report.TABLE);
        db.execSQL(ReportsRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + Report.TABLE_SAVED);
        db.execSQL(SavedReportsRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + Price.TABLE);
        db.execSQL(PricesRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + Stock.TABLE);
        db.execSQL(StocksRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + Organization.TABLE);
        db.execSQL(OrganizationsRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + MyLocation.TABLE);
        db.execSQL(MyLocationsRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + Order.TABLE);
        db.execSQL(OrdersRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + Order.TABLE_ITEM);
        db.execSQL(OrdersRepo.createItemTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));

        // Drop table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + Client.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Item.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Warehouse.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PriceType.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Baza.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Report.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Report.TABLE_SAVED);
        db.execSQL("DROP TABLE IF EXISTS " + Price.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Stock.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Organization.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MyLocation.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Order.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Order.TABLE_ITEM);

        onCreate(db);
    }
}
