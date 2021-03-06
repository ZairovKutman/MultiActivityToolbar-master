package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Price;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class PricesRepo {

    public Price price;
    Cursor cursor;
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
    public PricesRepo() {
        price = new Price();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Price.TABLE  + " ("
                + Price.KEY_PriceId  + "   PRIMARY KEY    ,"
                + Price.KEY_Guid  + "   TEXT    ,"
                + Price.KEY_Base  + "   TEXT    ,"
                + Price.KEY_PriceTypeGuid  + "   TEXT    ,"
                + Price.KEY_Price  + "   TEXT);";
    }

    public int insert(Price price) {
        int priceId;
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Price.KEY_Guid, price.getGuid());
        values.put(Price.KEY_Price, price.getPrice());
        values.put(Price.KEY_Base, price.getBase());
        values.put(Price.KEY_PriceTypeGuid, price.getPriceTypeGuid());

        // Inserting Row
        if (db.isOpen()) {
            priceId=(int)db.insert(Price.TABLE, null, values);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            priceId=(int)db.insert(Price.TABLE, null, values);
        }

        DatabaseManager.getInstance().closeDatabase();

        return priceId;
    }


    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(Price.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }


    public double getItemPriceByPriceType(String itemGuid, String priceTypeGuid)
    {
        double price=0;
        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Price.KEY_Price
                + " FROM " + Price.TABLE
                + " WHERE "+Price.KEY_Guid+"= '"+itemGuid+"' AND "+Price.KEY_PriceTypeGuid+" = '"+priceTypeGuid+"'" + " AND "+Price.KEY_Base + " = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"'";

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
                price = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Price.KEY_Price)));
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return price;

    }

    public void deleteByBase(String bazaString)
    {
        db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Price.KEY_Base+" = '"+bazaString+"'";
        db.delete(Price.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}
