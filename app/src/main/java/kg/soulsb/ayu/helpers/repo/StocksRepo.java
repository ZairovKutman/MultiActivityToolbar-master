package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Stock;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class StocksRepo {

    public Stock stock;

    public StocksRepo() {
        stock = new Stock();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Stock.TABLE  + " ("
                + Stock.KEY_StockId  + "   PRIMARY KEY    ,"
                + Stock.KEY_ItemGuid  + "   TEXT    ,"
                + Stock.KEY_WarehouseGuid  + "   TEXT    ,"
                + Stock.KEY_Base  + "   TEXT    ,"
                + Stock.KEY_Stock  + "   TEXT);";
    }

    public int insert(Stock stock) {
        int stockId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Stock.KEY_ItemGuid, stock.getItemGuid());
        values.put(Stock.KEY_Stock, stock.getStock());
        values.put(Stock.KEY_Base, stock.getBase());
        values.put(Stock.KEY_WarehouseGuid, stock.getWarehouseGuid());

        // Inserting Row
        stockId=(int)db.insert(Stock.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return stockId;
    }


    public void deleteTable() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Stock.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Stock> getStocksObject() {
        ArrayList<Stock> arrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Stock.KEY_Stock
                + ", "+Stock.KEY_StockId
                + ", "+Stock.KEY_ItemGuid
                + ", "+Stock.KEY_Base
                + ", "+Stock.KEY_WarehouseGuid
                + " FROM " + Stock.TABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Stock stock = new Stock();
                stock.setStockId(cursor.getString(cursor.getColumnIndexOrThrow(Stock.KEY_StockId)));
                stock.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Stock.KEY_Base)));
                stock.setItemGuid(cursor.getString(cursor.getColumnIndexOrThrow(Stock.KEY_ItemGuid)));
                stock.setWarehouseGuid(cursor.getString(cursor.getColumnIndexOrThrow(Stock.KEY_WarehouseGuid)));
                stock.setStock(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Stock.KEY_Stock))));


                arrayList.add(stock);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public double getItemStockByWarehouse(String itemGUID, String warehouse) {
        double stock=0;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Stock.KEY_Stock
                + " FROM " + Stock.TABLE
                + " WHERE "+Stock.KEY_Base+" = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"' AND "+Stock.KEY_ItemGuid+"= '"+itemGUID+"' AND "+Stock.KEY_WarehouseGuid+" = '"+warehouse+"'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                stock = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Stock.KEY_Stock)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return stock;

    }

    public void deleteByBase(String bazaString)
    {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Stock.KEY_Base+" = '"+bazaString+"'";
        db.delete(Stock.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}
