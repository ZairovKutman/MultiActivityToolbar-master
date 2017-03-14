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
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Price.KEY_Guid, price.getGuid());
        values.put(Price.KEY_Price, price.getPrice());
        values.put(Price.KEY_Base, price.getBase());
        values.put(Price.KEY_PriceTypeGuid, price.getPriceTypeGuid());

        // Inserting Row
        priceId=(int)db.insert(Price.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return priceId;
    }


    public void deleteTable() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Price.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Price> getPricesObject() {
        ArrayList<Price> arrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Price.KEY_Price
                + ", "+Price.KEY_PriceId
                + ", "+Price.KEY_Guid
                + ", "+Price.KEY_Base
                + ", "+Price.KEY_PriceTypeGuid
                + " FROM " + Price.TABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Price price = new Price();
                price.setPriceId(cursor.getString(cursor.getColumnIndexOrThrow(Price.KEY_PriceId)));
                price.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(Price.KEY_Guid)));
                price.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Price.KEY_Base)));
                price.setPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Price.KEY_Price))));
                price.setPriceTypeGuid(cursor.getString(cursor.getColumnIndexOrThrow(Price.KEY_PriceTypeGuid)));


                arrayList.add(price);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public double getItemPriceByPriceType(String itemGuid, String priceTypeGuid)
    {
        double price=0;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Price.KEY_Price
                + " FROM " + Price.TABLE
                + " WHERE "+Price.KEY_Guid+"= '"+itemGuid+"' AND "+Price.KEY_PriceTypeGuid+" = '"+priceTypeGuid+"'" + " AND "+Price.KEY_Base + " = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                price = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Price.KEY_Price)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return price;

    }

    public void deleteByBase(String bazaString)
    {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Price.KEY_Base+" = '"+bazaString+"'";
        db.delete(Price.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}
