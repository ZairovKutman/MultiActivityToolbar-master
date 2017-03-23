package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Contract;
import kg.soulsb.ayu.models.Warehouse;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class ContractsRepo {

    public Contract contract;

    public ContractsRepo() {
        contract = new Contract();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Contract.TABLE  + " ("
                + Contract.KEY_ContractId  + "   PRIMARY KEY    ,"
                + Contract.KEY_Guid  + "   TEXT    ,"
                + Contract.KEY_Name  + "   TEXT    ,"
                + Contract.KEY_Base  + "   TEXT    ,"
                + Contract.KEY_ClientGuid  + "   TEXT);";
    }

    public int insert(Contract contract) {
        int contractId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.KEY_Guid, contract.getGuid());
        values.put(Contract.KEY_Name, contract.getName());
        values.put(Contract.KEY_Base, contract.getBase());
        values.put(Contract.KEY_ClientGuid, contract.getClientGuid());
        // Inserting Row
        contractId=(int)db.insert(Contract.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return contractId;
    }

    public void deleteTable() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Contract.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Contract> getContractsObject(String client_guid) {
        ArrayList<Contract> arrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Contract.KEY_Name
                + ", "+Contract.KEY_ContractId
                + ", "+Contract.KEY_Guid
                + ", "+Contract.KEY_Base
                + ", "+Contract.KEY_ClientGuid
                + " FROM " + Contract.TABLE
                + " WHERE ClientGuid = '"+client_guid+"' AND "+Contract.KEY_Base+" = '"+ CurrentBaseClass.getInstance().getCurrentBase() +"'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Contract contract = new Contract();
                contract.setContractId(cursor.getString(cursor.getColumnIndexOrThrow(Contract.KEY_ContractId)));
                contract.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(Contract.KEY_Guid)));
                contract.setName(cursor.getString(cursor.getColumnIndexOrThrow(Contract.KEY_Name)));
                contract.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Contract.KEY_Base)));
                contract.setClientGuid(cursor.getString(cursor.getColumnIndexOrThrow(Contract.KEY_ClientGuid)));
                arrayList.add(contract);
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
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Contract.KEY_Base+" = '"+bazaString+"'";
        db.delete(Contract.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}