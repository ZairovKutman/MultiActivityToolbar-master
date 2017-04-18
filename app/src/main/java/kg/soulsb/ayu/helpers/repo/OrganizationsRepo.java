package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Organization;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class OrganizationsRepo {

    public Organization organization;
    Cursor cursor;
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
    public OrganizationsRepo() {
        organization = new Organization();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Organization.TABLE  + " ("
                + Organization.KEY_OrganizationId  + "   PRIMARY KEY    ,"
                + Organization.KEY_Guid  + "   TEXT    ,"
                + Organization.KEY_Base  + "   TEXT    ,"
                + Organization.KEY_Name  + "   TEXT);";
    }

    public int insert(Organization organization) {
        int priceTypeId;
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Organization.KEY_Guid, organization.getGuid());
        values.put(Organization.KEY_Name, organization.getName());
        values.put(Organization.KEY_Base, organization.getBase());

        // Inserting Row
        if (db.isOpen()) {
            priceTypeId=(int)db.insert(Organization.TABLE, null, values);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            priceTypeId=(int)db.insert(Organization.TABLE, null, values);
        }

        DatabaseManager.getInstance().closeDatabase();

        return priceTypeId;
    }

    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(Organization.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Organization> getOrganizationsObject() {
        ArrayList<Organization> arrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Organization.KEY_Name
                + ", "+Organization.KEY_OrganizationId
                + ", "+Organization.KEY_Guid
                + ", "+Organization.KEY_Base
                + " FROM " + Organization.TABLE
                + " WHERE " + Organization.KEY_Base +" = '" + CurrentBaseClass.getInstance().getCurrentBase() + "'";

        if (db.isOpen()) {
            cursor = db.rawQuery(selectQuery, null);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            cursor = db.rawQuery(selectQuery, null);
        }// looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Organization organization = new Organization();
                organization.setOrganizationId(cursor.getString(cursor.getColumnIndexOrThrow(Organization.KEY_OrganizationId)));
                organization.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(Organization.KEY_Guid)));
                organization.setName(cursor.getString(cursor.getColumnIndexOrThrow(Organization.KEY_Name)));
                organization.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Organization.KEY_Base)));


                arrayList.add(organization);
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
        String whereClause = Organization.KEY_Base+" = '"+bazaString+"'";
        db.delete(Organization.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }

}
