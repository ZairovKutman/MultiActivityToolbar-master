package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.models.Contract;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class ItemsRepo {

    public Item tovar;
    Cursor cursor;
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
    public ItemsRepo() {
        tovar = new Item();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Item.TABLE  + " ("
                + Item.KEY_ItemId  + "   PRIMARY KEY    ,"
                + Item.KEY_Guid  + "   TEXT    ,"
                + Item.KEY_Name  + "   TEXT    ,"
                + Item.KEY_Price  + "   TEXT    ,"
                + Item.KEY_Stock  + "   TEXT    ,"
                + Item.KEY_Base  + "   TEXT    ,"
                + Item.KEY_Category  + "   TEXT    ,"
                + Item.KEY_Sum  + "   TEXT    ,"
                + Item.KEY_Unit  + "   TEXT    )";
    }

    public int insert(Item tovar) {
        int courseId;
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Item.KEY_Guid, tovar.getGuid());
        values.put(Item.KEY_Name, tovar.getName());
        values.put(Item.KEY_Price, tovar.getPrice());
        values.put(Item.KEY_Stock, tovar.getStock());
        values.put(Item.KEY_Unit, tovar.getUnit());
        values.put(Item.KEY_Base, tovar.getBase());
        values.put(Item.KEY_Category, tovar.getCategory());
        values.put(Item.KEY_Sum, tovar.getSum());

        // Inserting Row
        if (db.isOpen()) {
            courseId=(int)db.insert(Item.TABLE, null, values);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            courseId=(int)db.insert(Item.TABLE, null, values);
        }

        DatabaseManager.getInstance().closeDatabase();

        return courseId;
    }


    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(Item.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Item> getItemsObject() {
        ArrayList<Item> arrayList = new ArrayList<>();

        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Item.KEY_Name
                + ", "+Item.KEY_ItemId
                + ", "+Item.KEY_Unit
                + ", "+Item.KEY_Guid
                + ", "+Item.KEY_Price
                + ", "+Item.KEY_Stock
                + ", "+Item.KEY_Category
                + ", "+Item.KEY_Base
                + ", "+Item.KEY_Sum
                + " FROM " + Item.TABLE
                + " WHERE "+Item.KEY_Base + " = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"'"
                + " ORDER BY "+Client.KEY_Name+" ASC;";

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
                Item item = new Item();
                item.setItemId(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_ItemId)));
                item.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Guid)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Name)));
                item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Unit)));
                item.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Base)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Category)));
                item.setPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Price))));
                item.setStock(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Stock))));
                item.setSum(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Sum))));

                arrayList.add(item);
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
        String whereClause = Item.KEY_Base+" = '"+bazaString+"'";
        db.delete(Item.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Item> getItemsObjectByCategory(String category) {
        ArrayList<Item> arrayList = new ArrayList<>();
        String whereClause;

        if (category.equals(""))
            whereClause = " WHERE "+Item.KEY_Base + " = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"'";
        else
            whereClause = " WHERE "+Item.KEY_Base + " = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"' AND "+Item.KEY_Category+" = '"+category+"'";

        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Item.KEY_Name
                + ", "+Item.KEY_ItemId
                + ", "+Item.KEY_Unit
                + ", "+Item.KEY_Guid
                + ", "+Item.KEY_Price
                + ", "+Item.KEY_Stock
                + ", "+Item.KEY_Category
                + ", "+Item.KEY_Base
                + " FROM " + Item.TABLE
                + whereClause
                + " ORDER BY "+Client.KEY_Name+" ASC;";

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
                Item item = new Item();
                item.setItemId(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_ItemId)));
                item.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Guid)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Name)));
                item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Unit)));
                item.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Base)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Category)));
                item.setPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Price))));

                item.setStock(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Item.KEY_Stock))));

                arrayList.add(item);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }
}
