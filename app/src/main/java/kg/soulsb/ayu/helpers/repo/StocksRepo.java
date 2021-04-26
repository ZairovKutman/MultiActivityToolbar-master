package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.models.Price;
import kg.soulsb.ayu.models.Stock;
import kg.soulsb.ayu.models.Unit;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class StocksRepo {

    public Stock stock;
    Cursor cursor;
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
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
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Stock.KEY_ItemGuid, stock.getItemGuid());
        values.put(Stock.KEY_Stock, stock.getStock());
        values.put(Stock.KEY_Base, stock.getBase());
        values.put(Stock.KEY_WarehouseGuid, stock.getWarehouseGuid());

        // Inserting Row
        if (db.isOpen()) {
            stockId=(int)db.insert(Stock.TABLE, null, values);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            stockId=(int)db.insert(Stock.TABLE, null, values);
        }

        DatabaseManager.getInstance().closeDatabase();

        return stockId;
    }


    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(Stock.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }


    public int getItemStockByWarehouse(String itemGUID, String warehouse) {
        int stockFromOrders=0;
        db = DatabaseManager.getInstance().openDatabase();
/*
        String selectQuery =  " SELECT IFNULL(SUM(" + Stock.KEY_Stock
                + "),0) AS "+ Stock.KEY_Stock
                + ", IFNULL(SUM("+Order.TABLE_ITEM +"."+ Item.KEY_Quantity
                + "),0) AS usedItems, "+ Stock.KEY_ItemGuid
                + " FROM " + Stock.TABLE
                + " LEFT JOIN " + Order.TABLE_ITEM
                + " ON "+Stock.TABLE+"."+Stock.KEY_ItemGuid+" = "+Order.TABLE_ITEM+"."+ Item.KEY_Guid
                + " WHERE "+Stock.KEY_Base+" = '"+ CurrentBaseClass.getInstance().getCurrentBase()
                +"' AND "+Stock.KEY_ItemGuid+"= '"+itemGUID
                +"' AND "+Stock.KEY_WarehouseGuid+" = '"+warehouse+"'"
//                +"' AND "+Order.TABLE_ITEM+"."+Order.KEY_warehouse+" = '"+warehouse+"'"
                + "GROUP BY "+Stock.KEY_ItemGuid;
*/

        String selectQuery =  " SELECT " + Stock.KEY_Stock
                + " AS "+ Stock.KEY_Stock
                + ", IFNULL(SUM("+Order.TABLE_ITEM +"."+ Item.KEY_Quantity
                + "),0) AS usedItems, "+ Stock.KEY_ItemGuid
                + ", "+ Order.TABLE_ITEM+"."+Item.KEY_UnitGUID + " AS unitGuid"
                + " FROM " + Stock.TABLE
                + " LEFT JOIN " + Order.TABLE_ITEM
                + " ON "+Stock.TABLE+"."+Stock.KEY_ItemGuid+" = "+Order.TABLE_ITEM+"."+ Item.KEY_Guid
                +" AND "+Order.TABLE_ITEM+"." + Item.KEY_isDelivered + "= 'false' "
                + " WHERE "+Stock.KEY_Base+" = '"+ CurrentBaseClass.getInstance().getCurrentBase()
                +"' AND "+Stock.KEY_ItemGuid+"= '"+itemGUID
                +"' AND "+Stock.KEY_WarehouseGuid+" = '"+ warehouse +"' "
//                +"' AND "+Order.TABLE_ITEM+"."+Order.KEY_warehouse+" = '"+warehouse+"'"
                + "GROUP BY "+Stock.KEY_ItemGuid
                + ", "+Stock.KEY_Stock
                + ", "+ Order.TABLE_ITEM+"."+Item.KEY_UnitGUID;



        if (db.isOpen()) {
            cursor = db.rawQuery(selectQuery, null);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            cursor = db.rawQuery(selectQuery, null);
        }
        // looping through all rows and adding to list
        int originalStock = 0;
        UnitsRepo unitsRepo = new UnitsRepo();
        if (cursor.moveToFirst()) {
            do {
                originalStock = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Stock.KEY_Stock)));
                Unit unit = unitsRepo.getUnitsObjectByItemGuidAndUnitGuid(cursor.getString(cursor.getColumnIndexOrThrow(Stock.KEY_ItemGuid)),cursor.getString(cursor.getColumnIndexOrThrow("unitGuid")));
                stockFromOrders = (int) (stockFromOrders+Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("usedItems")))*unit.getCoefficient());
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return originalStock-stockFromOrders;

    }

    public void deleteByBase(String bazaString)
    {
        db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Stock.KEY_Base+" = '"+bazaString+"'";
        db.delete(Stock.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}
