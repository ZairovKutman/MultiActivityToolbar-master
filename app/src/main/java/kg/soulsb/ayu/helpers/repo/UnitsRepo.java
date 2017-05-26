package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Unit;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class UnitsRepo {

    public Unit unit;
    Cursor cursor;
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
    public UnitsRepo() {
        unit = new Unit();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Unit.TABLE  + " ("
                + Unit.KEY_UnitId  + "   PRIMARY KEY    ,"
                + Unit.KEY_unitGuid  + "   TEXT    ,"
                + Unit.KEY_ItemGuid  + "   TEXT    ,"
                + Unit.KEY_coefficient  + "   TEXT    ,"
                + Unit.KEY_Base  + "   TEXT    ,"
                + Unit.KEY_Default  + "   TEXT    ,"
                + Unit.KEY_name  + "   TEXT);";
    }

    public int insert(Unit unit) {
        int unitId;
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Unit.KEY_unitGuid, unit.getUnitGuid());
        values.put(Unit.KEY_ItemGuid, unit.getItemGuid());
        values.put(Unit.KEY_coefficient, unit.getCoefficient());
        values.put(Unit.KEY_name, unit.getName());
        values.put(Unit.KEY_Base, unit.getBase());
        values.put(Unit.KEY_Default, unit.isDefault());

        // Inserting Row
        if (db.isOpen()) {
            unitId=(int)db.insert(Unit.TABLE, null, values);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            unitId=(int)db.insert(Unit.TABLE, null, values);
        }

        DatabaseManager.getInstance().closeDatabase();

        return unitId;
    }


    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(Unit.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Unit> getUnitsObjectByItemGuid(String guid) {
        ArrayList<Unit> arrayList = new ArrayList<>();

        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Unit.KEY_name
                + ", "+Unit.KEY_UnitId
                + ", "+Unit.KEY_ItemGuid
                + ", "+Unit.KEY_unitGuid
                + ", "+Unit.KEY_Base
                + ", "+Unit.KEY_coefficient
                + ", "+Unit.KEY_Default
                + " FROM " + Unit.TABLE
                + " WHERE "+ Unit.KEY_Base + " = '"+CurrentBaseClass.getInstance().getCurrentBase()+"' AND "+Unit.KEY_ItemGuid+" = '"+guid+"'";

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
                Unit unit = new Unit();
                unit.setUnitId(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_UnitId)));
                unit.setName(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_name)));
                unit.setItemGuid(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_ItemGuid)));
                unit.setUnitGuid(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_unitGuid)));
                unit.setCoefficient(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_coefficient))));
                unit.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_Base)));
                unit.setDefault(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_Default)));

                arrayList.add(unit);
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
        String whereClause = Unit.KEY_Base+" = '"+bazaString+"'";
        db.delete(Unit.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public String getDefaultUnitNameByItemGuid(String itemGuid) {
        String defaultName="шт.";

        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Unit.KEY_name
                + " FROM " + Unit.TABLE
                + " WHERE "+ Unit.KEY_Base + " = '"+CurrentBaseClass.getInstance().getCurrentBase()+"' AND "+Unit.KEY_ItemGuid+" = '"+itemGuid+"' AND "+Unit.KEY_Default+" = '"+"true"+"'";

        if (db.isOpen()) {
            cursor = db.rawQuery(selectQuery, null);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            cursor = db.rawQuery(selectQuery, null);
        }


        if (cursor.moveToFirst()) {
            do {
                defaultName = cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_name));
                break;
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return defaultName;
    }

    public Unit getUnitsObjectByItemGuidAndUnitGuid(String itemGuid, String myUnitGuid) {
        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Unit.KEY_name
                + ", "+Unit.KEY_UnitId
                + ", "+Unit.KEY_ItemGuid
                + ", "+Unit.KEY_unitGuid
                + ", "+Unit.KEY_Base
                + ", "+Unit.KEY_coefficient
                + ", "+Unit.KEY_Default
                + " FROM " + Unit.TABLE
                + " WHERE "+ Unit.KEY_Base + " = '"+CurrentBaseClass.getInstance().getCurrentBase()+"' AND "+Unit.KEY_ItemGuid+" = '"+itemGuid+"' AND "+Unit.KEY_unitGuid+" = '"+myUnitGuid+"'";

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
                Unit unit = new Unit();
                unit.setUnitId(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_UnitId)));
                unit.setName(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_name)));
                unit.setItemGuid(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_ItemGuid)));
                unit.setUnitGuid(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_unitGuid)));
                unit.setCoefficient(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_coefficient))));
                unit.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_Base)));
                unit.setDefault(cursor.getString(cursor.getColumnIndexOrThrow(Unit.KEY_Default)));

                if (!cursor.isClosed())
                {
                    cursor.close();
                }
                DatabaseManager.getInstance().closeDatabase();

               return unit;
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return new Unit();
    }
}
