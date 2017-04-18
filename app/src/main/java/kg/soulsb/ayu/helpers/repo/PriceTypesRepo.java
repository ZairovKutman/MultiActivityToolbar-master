package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.models.Contract;
import kg.soulsb.ayu.models.Price;
import kg.soulsb.ayu.models.PriceType;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class PriceTypesRepo {

    public PriceType pricetype;
    Cursor cursor;
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
    public PriceTypesRepo() {
        pricetype = new PriceType();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + PriceType.TABLE  + " ("
                + PriceType.KEY_PricetypeId  + "   PRIMARY KEY    ,"
                + PriceType.KEY_Guid  + "   TEXT    ,"
                + PriceType.KEY_Base  + "   TEXT    ,"
                + PriceType.KEY_Name  + "   TEXT);";
    }

    public int insert(PriceType pricetype) {
        int priceTypeId;
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(PriceType.KEY_Guid, pricetype.getGuid());
        values.put(PriceType.KEY_Base, pricetype.getBase());
        values.put(PriceType.KEY_Name, pricetype.getName());

        // Inserting Row
        if (db.isOpen()) {
            priceTypeId=(int)db.insert(PriceType.TABLE, null, values);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            priceTypeId=(int)db.insert(PriceType.TABLE, null, values);
        }

        DatabaseManager.getInstance().closeDatabase();

        return priceTypeId;
    }


    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(PriceType.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<PriceType> getPricetypesObject() {
        ArrayList<PriceType> arrayList = new ArrayList<>();

        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + PriceType.KEY_Name
                + ", "+PriceType.KEY_PricetypeId
                + ", "+PriceType.KEY_Guid
                + ", "+PriceType.KEY_Base
                + " FROM " + PriceType.TABLE
                + " WHERE "+ PriceType.KEY_Base + " = '"+CurrentBaseClass.getInstance().getCurrentBase()+"'";

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
                PriceType pricetype = new PriceType();
                pricetype.setPriceTypeId(cursor.getString(cursor.getColumnIndexOrThrow(PriceType.KEY_PricetypeId)));
                pricetype.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(PriceType.KEY_Guid)));
                pricetype.setName(cursor.getString(cursor.getColumnIndexOrThrow(PriceType.KEY_Name)));
                pricetype.setBase(cursor.getString(cursor.getColumnIndexOrThrow(PriceType.KEY_Base)));

                arrayList.add(pricetype);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public void deleteByBase(String bazaString)
    {
        db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = PriceType.KEY_Base+" = '"+bazaString+"'";
        db.delete(PriceType.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }

}
