package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.DailyTask;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

public class DailyTasksRepo {

    public DailyTask dailyTask;
    Cursor cursor;

    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
    public DailyTasksRepo() {
        dailyTask = new DailyTask();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + DailyTask.TABLE  + " ("
                + DailyTask.KEY_dailyTaskId  + "   PRIMARY KEY    ,"
                + DailyTask.KEY_agentName  + "   TEXT    ,"
                + DailyTask.KEY_base  + "   TEXT    ,"
                + DailyTask.KEY_clientGuid  + "   TEXT    ,"
                + DailyTask.KEY_dateClosed  + "   TEXT    ,"
                + DailyTask.KEY_docdate  + "   TEXT    ,"
                + DailyTask.KEY_docGuid  + "   TEXT    ,"
                + DailyTask.KEY_docid  + "   TEXT    ,"
                + DailyTask.KEY_latitude  + "   TEXT    ,"
                + DailyTask.KEY_longitude  + "   TEXT    ,"
                + DailyTask.KEY_photo  + "   TEXT    ,"
                + DailyTask.KEY_rate_comment  + "   TEXT    ,"
                + DailyTask.KEY_rate  + "   TEXT    ,"
                + DailyTask.KEY_rate_date  + "   TEXT    ,"
                + DailyTask.KEY_priority  + "   INTEGER    ,"
                + DailyTask.KEY_status  +  "   TEXT    )";
    }

    public int insert(DailyTask dailyTask)
    {
        int dailyTaskId;

        ContentValues values = new ContentValues();
        values.put(DailyTask.KEY_agentName, dailyTask.getAgentName());
        values.put(DailyTask.KEY_clientGuid, dailyTask.getClientGuid());
        values.put(DailyTask.KEY_dateClosed, dailyTask.getDateClosed());
        values.put(DailyTask.KEY_docdate, dailyTask.getDocDate());
        values.put(DailyTask.KEY_docGuid, dailyTask.getDocGuid());
        values.put(DailyTask.KEY_docid, dailyTask.getDocId());
        values.put(DailyTask.KEY_latitude, dailyTask.getLatitude());
        values.put(DailyTask.KEY_longitude, dailyTask.getLongitude());
        values.put(DailyTask.KEY_priority, dailyTask.getPriority());
        values.put(DailyTask.KEY_base, dailyTask.getBase());
        values.put(DailyTask.KEY_status, dailyTask.getStatus());
        values.put(DailyTask.KEY_rate, dailyTask.getRate());
        values.put(DailyTask.KEY_rate_comment, dailyTask.getRateComment());
        values.put(DailyTask.KEY_rate_date, dailyTask.getRateDate());

        // Inserting Row
        if (db.isOpen()) {
            dailyTaskId=(int)db.insert(DailyTask.TABLE, null, values);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            dailyTaskId=(int)db.insert(DailyTask.TABLE, null, values);
        }

        DatabaseManager.getInstance().closeDatabase();

        return dailyTaskId;
    }

    public ArrayList<DailyTask> getDailyTasksObject() {
        ArrayList<DailyTask> arrayList = new ArrayList<>();

        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + DailyTask.KEY_clientGuid
                + ", "+DailyTask.KEY_dateClosed
                + ", "+DailyTask.KEY_docdate
                + ", "+DailyTask.KEY_docGuid
                + ", "+DailyTask.KEY_docid
                + ", "+DailyTask.KEY_latitude
                + ", "+DailyTask.KEY_longitude
                + ", "+DailyTask.KEY_photo
                + ", "+DailyTask.KEY_priority
                + ", "+DailyTask.KEY_status
                + ", "+DailyTask.KEY_base
                + ", "+DailyTask.KEY_rate_date
                + ", "+DailyTask.KEY_rate
                + ", "+DailyTask.KEY_rate_comment
                + " FROM " + DailyTask.TABLE
                + " WHERE "+DailyTask.KEY_base+" = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"'"
                + " ORDER BY "+DailyTask.KEY_priority+" ASC;";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DailyTask client = new DailyTask();
                client.setClientGuid(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_clientGuid)));
                client.setBase(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_base)));
                client.setDateClosed(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_dateClosed)));
                client.setDocDate(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_docdate)));
                client.setDocGuid(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_docGuid)));
                client.setDocId(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_docid)));
                client.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_latitude)));
                client.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_longitude)));
                client.setPhoto(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_photo)));
                client.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(DailyTask.KEY_priority)));
                client.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_status)));
                client.setRate(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_rate)));
                client.setRateComment(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_rate_comment)));
                client.setRateDate(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_rate_date)));

                arrayList.add(client);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public ArrayList<DailyTask> getDailyTasksForMain() {
        ArrayList<DailyTask> arrayList = new ArrayList<>();

        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + DailyTask.KEY_clientGuid
                + ", "+DailyTask.KEY_dateClosed
                + ", "+DailyTask.KEY_docdate
                + ", "+DailyTask.KEY_docGuid
                + ", "+DailyTask.KEY_docid
                + ", "+DailyTask.KEY_latitude
                + ", "+DailyTask.KEY_longitude
                + ", "+DailyTask.KEY_photo
                + ", "+DailyTask.KEY_priority
                + ", "+DailyTask.KEY_status
                + ", "+DailyTask.KEY_base
                + ", "+DailyTask.KEY_rate_date
                + ", "+DailyTask.KEY_rate
                + ", "+DailyTask.KEY_rate_comment
                + " FROM " + DailyTask.TABLE
                + " WHERE "+DailyTask.KEY_base+" = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"' "
                + " ORDER BY "+DailyTask.KEY_priority+" ASC;";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DailyTask client = new DailyTask();
                client.setClientGuid(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_clientGuid)));
                client.setBase(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_base)));
                client.setDateClosed(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_dateClosed)));
                client.setDocDate(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_docdate)));
                client.setDocGuid(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_docGuid)));
                client.setDocId(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_docid)));
                client.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_latitude)));
                client.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_longitude)));
                client.setPhoto(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_photo)));
                client.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(DailyTask.KEY_priority)));
                client.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_status)));
                client.setRate(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_rate)));
                client.setRateComment(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_rate_comment)));
                client.setRateDate(cursor.getString(cursor.getColumnIndexOrThrow(DailyTask.KEY_rate_date)));

                arrayList.add(client);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(DailyTask.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public void updateStatus(String clientGuid, String docId, String docDate, double latitude, double longitude, String dateClosed, String status)
    {
        db = DatabaseManager.getInstance().openDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DailyTask.KEY_status,status);
        cv.put(DailyTask.KEY_docid,docId);
        cv.put(DailyTask.KEY_docdate,docDate);
        cv.put(DailyTask.KEY_latitude, latitude);
        cv.put(DailyTask.KEY_longitude, longitude);
        cv.put(DailyTask.KEY_dateClosed, dateClosed);


        db.update(DailyTask.TABLE, cv, DailyTask.KEY_clientGuid+" = '"+clientGuid+"'", null);

        DatabaseManager.getInstance().closeDatabase();


    }


    public void deleteByBase(String bazaString)
    {
        db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = DailyTask.KEY_base+" = '"+bazaString+"'";
        db.delete(DailyTask.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public void updateStatusByPhoto(String guid, double latitude, double longitude, String dateClosed, String status) {
        db = DatabaseManager.getInstance().openDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DailyTask.KEY_status,status);
        cv.put(DailyTask.KEY_latitude, latitude);
        cv.put(DailyTask.KEY_longitude, longitude);
        cv.put(DailyTask.KEY_dateClosed, dateClosed);


        db.update(DailyTask.TABLE, cv, DailyTask.KEY_clientGuid+" = '"+guid+"'", null);

        DatabaseManager.getInstance().closeDatabase();
    }
}