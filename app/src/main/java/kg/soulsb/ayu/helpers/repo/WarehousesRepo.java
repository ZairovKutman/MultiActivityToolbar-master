package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Warehouse;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class WarehousesRepo {

    public Warehouse warehouse;
    Cursor cursor;
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
    public WarehousesRepo() {
        warehouse = new Warehouse();
    }

    public static String createTable() {
        return "CREATE TABLE IF NOT EXISTS " + Warehouse.TABLE  + " ("
                + Warehouse.KEY_WarehouseId  + "   PRIMARY KEY    ,"
                + Warehouse.KEY_Guid  + "   TEXT    ,"
                + Warehouse.KEY_Base  + "   TEXT    ,"
                + Warehouse.KEY_Name  + "   TEXT);";
    }

    public int insert(Warehouse warehouse) {
        int warehouseId;
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Warehouse.KEY_Guid, warehouse.getGuid());
        values.put(Warehouse.KEY_Base, warehouse.getBase());
        values.put(Warehouse.KEY_Name, warehouse.getName());

        // Inserting Row
        if (db.isOpen()) {
            warehouseId=(int)db.insert(Warehouse.TABLE, null, values);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            warehouseId=(int)db.insert(Warehouse.TABLE, null, values);
        }

        DatabaseManager.getInstance().closeDatabase();

        return warehouseId;
    }

    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(Warehouse.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Warehouse> getWarehousesObject() {
        ArrayList<Warehouse> arrayList = new ArrayList<>();

        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Warehouse.KEY_Name
                + ", "+Warehouse.KEY_WarehouseId
                + ", "+Warehouse.KEY_Guid
                + ", "+Warehouse.KEY_Base
                + " FROM " + Warehouse.TABLE
                + " WHERE "+Warehouse.KEY_Base+" = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"'";

        if (db.isOpen()) {
            cursor = db.rawQuery(selectQuery, null);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            cursor = db.rawQuery(selectQuery, null);
        }
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Warehouse warehouse = new Warehouse();
                warehouse.setWarehouseId(cursor.getString(cursor.getColumnIndexOrThrow(Warehouse.KEY_WarehouseId)));
                warehouse.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(Warehouse.KEY_Guid)));
                warehouse.setName(cursor.getString(cursor.getColumnIndexOrThrow(Warehouse.KEY_Name)));
                warehouse.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Warehouse.KEY_Base)));

                arrayList.add(warehouse);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public void deleteByBase(String bazaString) {
        db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Warehouse.KEY_Base+" = '"+bazaString+"'";
        db.delete(Warehouse.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}