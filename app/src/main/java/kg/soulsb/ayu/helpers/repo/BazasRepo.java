package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.PriceType;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class BazasRepo {

    public Baza baza;

    public BazasRepo() {
        baza = new Baza();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Baza.TABLE  + " ("
                + Baza.KEY_BazaId  + "   PRIMARY KEY    ,"
                + Baza.KEY_Host  + "   TEXT    ,"
                + Baza.KEY_port  + "   TEXT    ,"
                + Baza.KEY_Agent  + "   TEXT    ,"
                + Baza.KEY_Name  + "   TEXT);";
    }

    public int insert(Baza baza) {
        int bazaId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Baza.KEY_Host, baza.getHost());
        values.put(Baza.KEY_port, baza.getPort());
        values.put(Baza.KEY_Name, baza.getName());
        values.put(Baza.KEY_Agent, baza.getAgent());

        // Inserting Row
        bazaId=(int)db.insert(Baza.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return bazaId;
    }

    public void delete(Baza baza)
    {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Baza.KEY_Host, baza.getHost());
        values.put(Baza.KEY_port, baza.getPort());
        values.put(Baza.KEY_Name, baza.getName());
        values.put(Baza.KEY_Agent, baza.getAgent());

        // deleting Row
        String whereClause = Baza.KEY_Name+" = '"+baza.getName()+"' AND "+Baza.KEY_Host+" = '"+baza.getHost()+"' AND "+Baza.KEY_port+" = '"+baza.getPort()+"'";
        db.delete(Baza.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();

    }

    public void deleteTable() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Baza.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Baza> getBazasObject() {
        ArrayList<Baza> arrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Baza.KEY_Name
                + ", "+Baza.KEY_Host
                + ", "+Baza.KEY_port
                + ", "+Baza.KEY_Agent
                + " FROM " + Baza.TABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Baza baza = new Baza();
                baza.setHost(cursor.getString(cursor.getColumnIndexOrThrow(Baza.KEY_Host)));
                baza.setPort(cursor.getString(cursor.getColumnIndexOrThrow(Baza.KEY_port)));
                baza.setName(cursor.getString(cursor.getColumnIndexOrThrow(Baza.KEY_Name)));
                baza.setAgent(cursor.getString(cursor.getColumnIndexOrThrow(Baza.KEY_Agent)));


                arrayList.add(baza);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public void updateIpAndPort(String name, String ip, String port, String agent) {
        int bazaId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Baza.KEY_Host, ip);
        values.put(Baza.KEY_port, port);
        String whereClause = Baza.KEY_Name + " = '"+name+"' AND "+Baza.KEY_Agent+" = '"+agent+"'";
        // Inserting Row
        bazaId=(int)db.update(Baza.TABLE, values, whereClause, null);
        DatabaseManager.getInstance().closeDatabase();
    }
}
