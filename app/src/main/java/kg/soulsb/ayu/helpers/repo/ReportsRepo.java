package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.Report;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class ReportsRepo {

    public Report report;

    public ReportsRepo() {
        report = new Report();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Report.TABLE  + " ("
                + Report.KEY_ReportId  + "   PRIMARY KEY    ,"
                + Report.KEY_Guid  + "   TEXT    ,"
                + Report.KEY_Base  + "   TEXT    ,"
                + Report.KEY_Name  + "   TEXT);";
    }

    public int insert(Report report) {
        int reportId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Report.KEY_Guid, report.getGuid());
        values.put(Report.KEY_Name, report.getName());
        values.put(Report.KEY_Base, report.getBase());

        // Inserting Row
        reportId=(int)db.insert(Report.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return reportId;
    }

    public ArrayList<String> getReportsName()
    {
        ArrayList<String> arrayList = new ArrayList<>();



        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Report.KEY_Name
                + " FROM " + Report.TABLE
                + " WHERE " + Report.KEY_Base + " = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"'";


        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                arrayList.add(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_Name)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public void deleteTable() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Report.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Report> getReportsObject() {
        ArrayList<Report> arrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Report.KEY_Name
                + ", "+Report.KEY_ReportId
                + ", "+Report.KEY_Guid
                + ", "+Report.KEY_Base
                + " FROM " + Report.TABLE
                + " WHERE "+ Report.KEY_Base+" = '"+CurrentBaseClass.getInstance().getCurrentBase()+"'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Report report = new Report();
                report.setReportId(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_ReportId)));
                report.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_Guid)));
                report.setName(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_Name)));
                report.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_Base)));


                arrayList.add(report);
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
        String whereClause = Report.KEY_Base+" = '"+bazaString+"'";
        db.delete(Report.TABLE,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}
