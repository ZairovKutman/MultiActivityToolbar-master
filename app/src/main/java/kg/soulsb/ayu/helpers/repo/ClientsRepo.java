package kg.soulsb.ayu.helpers.repo;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.ArrayList;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */
public class ClientsRepo {

    public Client client;
    public ClientsRepo() {
        client = new Client();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Client.TABLE  + "("
                + Client.KEY_ClientId  + "   PRIMARY KEY    ,"
                + Client.KEY_Guid  + "   TEXT    ,"
                + Client.KEY_Name  + "   TEXT    ,"
                + Client.KEY_Address  + "   TEXT    ,"
                + Client.KEY_Phone  + "   TEXT    ,"
                + Client.KEY_Latitude  + "   TEXT    ,"
                + Client.KEY_Longitude  + "   TEXT    ,"
                + Client.KEY_Base  + "   TEXT    ,"
                + Client.KEY_Debt + " TEXT )";
    }

    public int insert(Client client) {
        int courseId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Client.KEY_Guid, client.getGuid());
        values.put(Client.KEY_Name, client.getName());
        values.put(Client.KEY_Address, client.getAddress());
        values.put(Client.KEY_Phone, client.getPhone());
        values.put(Client.KEY_Latitude, client.getLatitude());
        values.put(Client.KEY_Longitude, client.getLongitude());
        values.put(Client.KEY_Debt, client.getDebt());
        values.put(Client.KEY_Base, client.getBase());
        // Inserting Row
        courseId=(int)db.insert(Client.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return courseId;
    }

    public Client getClientObjectByGuid(String guid)
    {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Client myClient = new Client();
        String selectQuery =  " SELECT " + Client.KEY_Name
                + ", "+Client.KEY_ClientId
                + ", "+Client.KEY_Address
                + ", "+Client.KEY_Guid
                + ", "+Client.KEY_Base
                + ", "+Client.KEY_Latitude
                + ", "+Client.KEY_Longitude
                + ", "+Client.KEY_Phone
                + ", "+Client.KEY_Debt
                + " FROM " + Client.TABLE
                + " WHERE "+Client.KEY_Guid+"='"+guid+"' AND "+Client.KEY_Base+" = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                myClient.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Guid)));
                myClient.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Base)));
                myClient.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Phone)));
                myClient.setName(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Name)));
                myClient.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Address)));
                myClient.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Latitude)));
                myClient.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Longitude)));
                myClient.setClientId(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_ClientId)));
                myClient.setDebt(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Debt))));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return myClient;
    }

    public void deleteTable( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Client.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Client> getClientsObject() {
        ArrayList<Client> arrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Client.KEY_Name
                + ", "+Client.KEY_ClientId
                + ", "+Client.KEY_Address
                + ", "+Client.KEY_Guid
                + ", "+Client.KEY_Latitude
                + ", "+Client.KEY_Longitude
                + ", "+Client.KEY_Base
                + ", "+Client.KEY_Phone
                + ", "+Client.KEY_Debt
                + " FROM " + Client.TABLE
                + " WHERE "+Client.KEY_Base+" = '"+CurrentBaseClass.getInstance().getCurrentBase()+"'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Client client = new Client();
                client.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Guid)));
                client.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Base)));
                client.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Phone)));
                client.setName(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Name)));
                client.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Address)));
                client.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Latitude)));
                client.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Longitude)));
                client.setClientId(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_ClientId)));
                client.setDebt(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Client.KEY_Debt))));
                arrayList.add(client);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public void setClientLocation(String guid,Location location)
    {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(Client.KEY_Latitude, location.getLatitude());
        values.put(Client.KEY_Longitude, location.getLongitude());

        String whereClause = Client.KEY_Guid + " = '"+guid+"'";
        // Inserting Row
        db.update(Client.TABLE, values, whereClause, null);
        DatabaseManager.getInstance().closeDatabase();

    }

    public void deleteByBase(String bazaString)
    {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Client.KEY_Base+" = '"+bazaString+"'";
        db.delete(Client.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}