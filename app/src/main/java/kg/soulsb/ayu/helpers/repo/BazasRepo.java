package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.PriceType;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class BazasRepo {

    public Baza baza;
    Cursor cursor;
    SQLiteDatabase db;
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
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Baza.KEY_Host, baza.getHost());
        values.put(Baza.KEY_port, baza.getPort());
        values.put(Baza.KEY_Name, baza.getName());
        values.put(Baza.KEY_Agent, baza.getAgent());
        values.put(Baza.KEY_BazaId, baza.getBazaId());

        // Inserting Row
        bazaId=(int)db.insert(Baza.TABLE, null, values);
        db.close();

        return bazaId;
    }

    public void delete(Baza baza)
    {
        db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Baza.KEY_Name+" = '"+baza.getName()+"' AND "+Baza.KEY_Host+" = '"+baza.getHost()+"' AND "+Baza.KEY_port+" = '"+baza.getPort()+"' AND "+Baza.KEY_BazaId+" = '"+baza.getBazaId()+"'";
        db.delete(Baza.TABLE,whereClause,null);
        db.close();

    }

    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(Baza.TABLE,null,null);
        db.close();
    }

    public ArrayList<Baza> getBazasObject() {
        ArrayList<Baza> arrayList = new ArrayList<>();

        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Baza.KEY_Name
                + ", "+Baza.KEY_Host
                + ", "+Baza.KEY_port
                + ", "+Baza.KEY_Agent
                + ", "+Baza.KEY_BazaId
                + " FROM " + Baza.TABLE;
        if (db.isOpen()) {
            cursor = db.rawQuery(selectQuery, null);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            cursor = db.rawQuery(selectQuery, null);
        }
        cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Baza baza = new Baza();
                baza.setHost(cursor.getString(cursor.getColumnIndexOrThrow(Baza.KEY_Host)));
                baza.setPort(cursor.getString(cursor.getColumnIndexOrThrow(Baza.KEY_port)));
                baza.setName(cursor.getString(cursor.getColumnIndexOrThrow(Baza.KEY_Name)));
                baza.setAgent(cursor.getString(cursor.getColumnIndexOrThrow(Baza.KEY_Agent)));
                baza.setBazaId(cursor.getString(cursor.getColumnIndexOrThrow(Baza.KEY_BazaId)));


                arrayList.add(baza);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        db.close();

        return arrayList;
    }

    public void updateIpAndPortAndAgent(String name, String ip, String port, String agent, String bazaId) {
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Baza.KEY_Host, ip);
        values.put(Baza.KEY_port, port);
        values.put(Baza.KEY_Agent, agent);
        String whereClause = Baza.KEY_Name + " = '"+name+"' AND "+Baza.KEY_BazaId+" = '"+bazaId+"'";
        // Inserting Row
        db.update(Baza.TABLE, values, whereClause, null);
        db.close();
    }
}
