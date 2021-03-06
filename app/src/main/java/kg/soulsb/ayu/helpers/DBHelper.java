package kg.soulsb.ayu.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import kg.soulsb.ayu.grpctest.nano.Units;
import kg.soulsb.ayu.helpers.repo.BazasRepo;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.helpers.repo.ContractsRepo;
import kg.soulsb.ayu.helpers.repo.DailyTasksRepo;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.MyLocationsRepo;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.helpers.repo.OrganizationsRepo;
import kg.soulsb.ayu.helpers.repo.PhotosRepo;
import kg.soulsb.ayu.helpers.repo.PriceTypesRepo;
import kg.soulsb.ayu.helpers.repo.PricesRepo;
import kg.soulsb.ayu.helpers.repo.ReportsRepo;
import kg.soulsb.ayu.helpers.repo.SalesHistoryRepo;
import kg.soulsb.ayu.helpers.repo.SavedReportsRepo;
import kg.soulsb.ayu.helpers.repo.StocksRepo;
import kg.soulsb.ayu.helpers.repo.UnitsRepo;
import kg.soulsb.ayu.helpers.repo.WarehousesRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.models.Contract;
import kg.soulsb.ayu.models.DailyPhoto;
import kg.soulsb.ayu.models.DailyTask;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.MyLocation;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.models.Organization;
import kg.soulsb.ayu.models.Price;
import kg.soulsb.ayu.models.PriceType;
import kg.soulsb.ayu.models.Report;
import kg.soulsb.ayu.models.SalesHistory;
import kg.soulsb.ayu.models.Stock;
import kg.soulsb.ayu.models.Unit;
import kg.soulsb.ayu.models.Warehouse;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 15;
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

        db.execSQL("DROP TABLE IF EXISTS " + Unit.TABLE);
        db.execSQL(UnitsRepo.createTable());

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

        db.execSQL("DROP TABLE IF EXISTS " + Order.TABLE_SvodPay);
        db.execSQL(OrdersRepo.createSvodPayTable());

        db.execSQL("DROP TABLE IF EXISTS " + DailyTask.TABLE);
        db.execSQL(DailyTasksRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + DailyPhoto.TABLE);
        db.execSQL(PhotosRepo.createTable());

        db.execSQL("DROP TABLE IF EXISTS " + SalesHistory.TABLE);
        db.execSQL(SalesHistoryRepo.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));
        // UPGRADES MIGRATIONS.

        if ((oldVersion==1 || oldVersion==2) && newVersion ==3)
        {
            db.execSQL("ALTER TABLE "+ Order.TABLE_ITEM +" ADD COLUMN "+ Item.KEY_isDelivered  +" TEXT DEFAULT 'true'");
        }

        if ((oldVersion==1 || oldVersion==2 || oldVersion==3))
        {
            try {
                db.execSQL(OrdersRepo.createSvodPayTable());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if ((oldVersion==1 || oldVersion==2 || oldVersion==3) && (newVersion == 5 ))
        {
            try {
                db.execSQL(OrdersRepo.createSvodPayTable());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if ((oldVersion==1 || oldVersion==2 || oldVersion==3 || oldVersion==4) && newVersion == 5)
        {
            db.execSQL("DROP TABLE IF EXISTS " + MyLocation.TABLE);
            db.execSQL(MyLocationsRepo.createTable());
        }

        if ((oldVersion==1 || oldVersion==2 || oldVersion==3 || oldVersion==4 || oldVersion==5 || oldVersion==6) && newVersion == 7)
        {
            db.execSQL("DROP TABLE IF EXISTS " + MyLocation.TABLE);
            db.execSQL(MyLocationsRepo.createTable());
        }

        if ((oldVersion==1 || oldVersion==2 || oldVersion==3 || oldVersion==4 || oldVersion==5 || oldVersion==6 || oldVersion==7) && (newVersion > 8 && newVersion <= 10))
        {
            db.execSQL("DROP TABLE IF EXISTS " + MyLocation.TABLE);
            db.execSQL(MyLocationsRepo.createTable());
        }

        if ((oldVersion==1 || oldVersion==2 || oldVersion==3 || oldVersion==4 || oldVersion==5 || oldVersion==6 || oldVersion==7 || oldVersion==8 || oldVersion==9) && newVersion == 10)
        {
            db.execSQL("ALTER TABLE " + Order.TABLE + " ADD COLUMN " + Order.KEY_checkedBonusTT + " text NOT NULL DEFAULT 'false';");
            db.execSQL("DROP TABLE IF EXISTS " + MyLocation.TABLE);
            db.execSQL(MyLocationsRepo.createTable());
        }

        if ((oldVersion==1 || oldVersion==2 || oldVersion==3 || oldVersion==4 || oldVersion==5 || oldVersion==6 || oldVersion==7 || oldVersion==8 || oldVersion==9 || oldVersion==10) && newVersion == 11)
        {
            if (oldVersion!=10)
            {
                db.execSQL("ALTER TABLE " + Order.TABLE + " ADD COLUMN " + Order.KEY_checkedBonusTT + " text NOT NULL DEFAULT 'false';");
            }

            db.execSQL("UPDATE " + Order.TABLE + " SET " + Order.KEY_checkedBonusTT + " = 'false';");
            db.execSQL("DROP TABLE IF EXISTS " + MyLocation.TABLE);
            db.execSQL(MyLocationsRepo.createTable());
        }

        if (oldVersion<12)
        {
            db.execSQL("DROP TABLE IF EXISTS " + DailyTask.TABLE);
            db.execSQL(DailyTasksRepo.createTable());

            db.execSQL("DROP TABLE IF EXISTS " + DailyPhoto.TABLE);
            db.execSQL(PhotosRepo.createTable());

            db.execSQL("ALTER TABLE " + Order.TABLE + " ADD COLUMN " + Order.KEY_isTask + " text NOT NULL DEFAULT 'false';");
        }

        if (oldVersion<13)
        {
            db.execSQL("DROP TABLE IF EXISTS " + SalesHistory.TABLE);
            db.execSQL(SalesHistoryRepo.createTable());
        }

        if (oldVersion<14)
        {
            db.execSQL("ALTER TABLE " + DailyTask.TABLE + " ADD COLUMN " + DailyTask.KEY_rate_date + " text NOT NULL DEFAULT '';");
            db.execSQL("ALTER TABLE " + DailyTask.TABLE + " ADD COLUMN " + DailyTask.KEY_rate + " text NOT NULL DEFAULT '';");
            db.execSQL("ALTER TABLE " + DailyTask.TABLE + " ADD COLUMN " + DailyTask.KEY_rate_comment + " text NOT NULL DEFAULT '';");
        }

        if (oldVersion<15)
        {
            db.execSQL("ALTER TABLE " + Client.TABLE + " ADD COLUMN " + Client.KEY_Oborot + " text NOT NULL DEFAULT '';");
        }
    }
}
