package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.SalesHistory;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

public class SalesHistoryRepo {

    public SalesHistory salesHistory;
    Cursor cursor;
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
    public SalesHistoryRepo() {
        salesHistory = new SalesHistory();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + SalesHistory.TABLE  + " ("
                + SalesHistory.KEY_salesHistoryID  + "   PRIMARY KEY    ,"
                + SalesHistory.KEY_ClientGuid  + "   TEXT    ,"
                + SalesHistory.KEY_Base  + "   TEXT    ,"
                + SalesHistory.KEY_ItemGuid  + "   TEXT    ,"
                + SalesHistory.KEY_Date1  + "   TEXT    ,"
                + SalesHistory.KEY_Date2  + "   TEXT    ,"
                + SalesHistory.KEY_Date3  + "   TEXT    ,"
                + SalesHistory.KEY_Qty1  + "   TEXT    ,"
                + SalesHistory.KEY_Qty2  + "   TEXT    ,"
                + SalesHistory.KEY_Qty3  + "   TEXT);";
    }

    public int insert(SalesHistory salesHistory) {
        int salesHistoryId;
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(SalesHistory.KEY_ClientGuid, salesHistory.getClientGuid());
        values.put(SalesHistory.KEY_ItemGuid, salesHistory.getItemGuid());
        values.put(SalesHistory.KEY_Base, salesHistory.getBase());
        values.put(SalesHistory.KEY_Date1, salesHistory.getDate1());
        values.put(SalesHistory.KEY_Date2, salesHistory.getDate2());
        values.put(SalesHistory.KEY_Date3, salesHistory.getDate3());
        values.put(SalesHistory.KEY_Qty1, salesHistory.getQty1());
        values.put(SalesHistory.KEY_Qty2, salesHistory.getQty2());
        values.put(SalesHistory.KEY_Qty3, salesHistory.getQty3());


        // Inserting Row
        if (db.isOpen()) {
            salesHistoryId=(int)db.insert(SalesHistory.TABLE, null, values);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            salesHistoryId=(int)db.insert(SalesHistory.TABLE, null, values);
        }

        DatabaseManager.getInstance().closeDatabase();

        return salesHistoryId;
    }


    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(SalesHistory.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }


    public SalesHistory getItemSalesHistory(String clientGuid, String itemGuid)
    {
        SalesHistory salesHistory2 = new SalesHistory();
        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + SalesHistory.KEY_Date1
                + ", "+ SalesHistory.KEY_Qty1
                + ", "+ SalesHistory.KEY_Date2
                + ", "+ SalesHistory.KEY_Qty2
                + ", "+ SalesHistory.KEY_Date3
                + ", "+ SalesHistory.KEY_Qty3
                + ", "+ SalesHistory.KEY_ItemGuid
                + ", "+ SalesHistory.KEY_ClientGuid
                + " FROM " + SalesHistory.TABLE
                + " WHERE "+SalesHistory.KEY_ItemGuid+"= '"+itemGuid+"' AND "+SalesHistory.KEY_ClientGuid+" = '"+clientGuid+"'" + " AND "+SalesHistory.KEY_Base + " = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"'";

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

                salesHistory2.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                salesHistory2.setClientGuid(cursor.getString(cursor.getColumnIndexOrThrow(SalesHistory.KEY_ClientGuid)));
                salesHistory2.setItemGuid(cursor.getString(cursor.getColumnIndexOrThrow(SalesHistory.KEY_ItemGuid)));
                salesHistory2.setDate1(cursor.getString(cursor.getColumnIndexOrThrow(SalesHistory.KEY_Date1)));
                salesHistory2.setDate2(cursor.getString(cursor.getColumnIndexOrThrow(SalesHistory.KEY_Date2)));
                salesHistory2.setDate3(cursor.getString(cursor.getColumnIndexOrThrow(SalesHistory.KEY_Date3)));
                salesHistory2.setQty1(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(SalesHistory.KEY_Qty1))));
                salesHistory2.setQty2(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(SalesHistory.KEY_Qty2))));
                salesHistory2.setQty3(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(SalesHistory.KEY_Qty3))));

            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return salesHistory2;

    }

    public void deleteByBase(String bazaString)
    {
        db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = SalesHistory.KEY_Base+" = '"+bazaString+"'";
        db.delete(SalesHistory.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}
