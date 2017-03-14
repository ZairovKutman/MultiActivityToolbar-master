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

    public MyLocationsRepo() {
        myLocation = new MyLocation();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + MyLocation.TABLE  + " ("
                + MyLocation.KEY_MyLocationId  + "   PRIMARY KEY    ,"
                + MyLocation.KEY_agent  + "   TEXT    ,"
                + MyLocation.KEY_latitude  + "   TEXT    ,"
                + MyLocation.KEY_longitude  + "   TEXT    ,"
                + MyLocation.KEY_formattedDate  + "   TEXT);";
    }

    public int insert(MyLocation myLocation) {
        int myLocationId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(MyLocation.KEY_agent, myLocation.getAgent());
        values.put(MyLocation.KEY_formattedDate, myLocation.getFormattedDate());
        values.put(MyLocation.KEY_latitude, myLocation.getLatitude());
        values.put(MyLocation.KEY_longitude, myLocation.getLongitude());


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

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + MyLocation.KEY_longitude
                + ", "+MyLocation.KEY_MyLocationId
                + ", "+MyLocation.KEY_latitude
                + ", "+MyLocation.KEY_formattedDate
                + ", "+MyLocation.KEY_agent
                + " FROM " + MyLocation.TABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                MyLocation myLocation = new MyLocation();
                myLocation.setAgent(cursor.getString(cursor.getColumnIndexOrThrow(MyLocation.KEY_agent)));
                myLocation.setFormattedDate(cursor.getString(cursor.getColumnIndexOrThrow(MyLocation.KEY_formattedDate)));
                myLocation.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(MyLocation.KEY_latitude)));
                myLocation.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(MyLocation.KEY_longitude)));

                arrayList.add(myLocation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public void delete(MyLocation myLocation)
    {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
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
}
