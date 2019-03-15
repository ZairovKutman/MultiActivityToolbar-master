package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.MyLocation;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class MyLocationsRepo {

    public MyLocation myLocation;
    Cursor cursor;
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
    public MyLocationsRepo() {
        myLocation = new MyLocation();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + MyLocation.TABLE  + " ("
                + MyLocation.KEY_MyLocationId  + "   PRIMARY KEY    ,"
                + MyLocation.KEY_agent  + "   TEXT    ,"
                + MyLocation.KEY_latitude  + "   REAL    ,"
                + MyLocation.KEY_longitude  + "   REAL    ,"
                + MyLocation.KEY_accuracy  + "   REAL    ,"
                + MyLocation.KEY_speed  + "   REAL    ,"
                + MyLocation.KEY_deviceId  + "   TEXT    ,"
                + MyLocation.KEY_formattedDate  + "   TEXT);";
    }

    public int insert(MyLocation myLocation) {
        int myLocationId;
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(MyLocation.KEY_agent, myLocation.getAgent());
        values.put(MyLocation.KEY_formattedDate, myLocation.getFormattedDate());
        values.put(MyLocation.KEY_latitude, myLocation.getLatitude());
        values.put(MyLocation.KEY_longitude, myLocation.getLongitude());
        values.put(MyLocation.KEY_speed, myLocation.getSpeed());
        values.put(MyLocation.KEY_deviceId, myLocation.getDeviceID());
        values.put(MyLocation.KEY_accuracy, myLocation.getAccuracy());


        // Inserting Row
        myLocationId=(int)db.insert(MyLocation.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return myLocationId;
    }

    public void deleteTable() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(MyLocation.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<MyLocation> getMyLocationsObject() {
        ArrayList<MyLocation> arrayList = new ArrayList<>();

        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + MyLocation.KEY_longitude
                + ", "+MyLocation.KEY_MyLocationId
                + ", "+MyLocation.KEY_latitude
                + ", "+MyLocation.KEY_formattedDate
                + ", "+MyLocation.KEY_agent
                + ", "+MyLocation.KEY_speed
                + ", "+MyLocation.KEY_accuracy
                + ", "+MyLocation.KEY_deviceId
                + " FROM " + MyLocation.TABLE;

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
                MyLocation myLocation = new MyLocation();
                myLocation.setAgent(cursor.getString(cursor.getColumnIndexOrThrow(MyLocation.KEY_agent)));
                myLocation.setFormattedDate(cursor.getString(cursor.getColumnIndexOrThrow(MyLocation.KEY_formattedDate)));
                myLocation.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(MyLocation.KEY_latitude)));
                myLocation.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(MyLocation.KEY_longitude)));
                myLocation.setSpeed(cursor.getFloat(cursor.getColumnIndexOrThrow(MyLocation.KEY_speed)));
                myLocation.setDeviceID(cursor.getString(cursor.getColumnIndexOrThrow(MyLocation.KEY_deviceId)));
                myLocation.setAccuracy(cursor.getFloat(cursor.getColumnIndexOrThrow(MyLocation.KEY_accuracy)));

                arrayList.add(myLocation);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public void delete(MyLocation myLocation)
    {
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(MyLocation.KEY_formattedDate, myLocation.getFormattedDate());
        values.put(MyLocation.KEY_agent, myLocation.getAgent());
        values.put(MyLocation.KEY_longitude, myLocation.getLongitude());
        values.put(MyLocation.KEY_latitude, myLocation.getLatitude());


        // deleting Row
        String whereClause = MyLocation.KEY_agent+" = '"+myLocation.getAgent()+"' AND "+MyLocation.KEY_latitude+" = '"+myLocation.getLatitude()
                +"' AND "+MyLocation.KEY_longitude+" = '"+myLocation.getLongitude()+"' AND "+MyLocation.KEY_formattedDate+" = '"+myLocation.getFormattedDate()+"'";
        db.delete(MyLocation.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public void deleteAll() {
        db = DatabaseManager.getInstance().openDatabase();
        // deleting Row
        db.delete(MyLocation.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}
